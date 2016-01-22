package main;

import exceptions.ServiceException;
import models.*;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kurento.client.*;
import play.mvc.WebSocket;
import services.CreateIntervalService;
import services.CreateRecordingService;
import services.ListGroupMembersService;
import services.ListOwnerAttributesService;

import javax.annotation.PreDestroy;
import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Room implements Closeable {

    private final ConcurrentMap<String, UserSession> participants = new ConcurrentHashMap<String, UserSession>();
    private final MediaPipeline mediaPipeline;
    private final Group group;
    private Interval interval;
    private Hub composite = null;
    private HubPort hubPort;

    public Room(final MediaPipeline mediaPipeline) {
        this.mediaPipeline = mediaPipeline;
        this.mediaPipeline.addErrorListener(new EventListener<ErrorEvent>() {

            @Override
            public void onEvent(ErrorEvent arg0) {
                System.out.println("PIPELINE ERROR");
                System.out.println(arg0.getDescription());
            }
        });

        for (MediaObject obj : mediaPipeline.getChilds()) {
            if (obj.getName().equals("composite")) {
                this.composite = (Hub) obj;
                for (MediaObject child : obj.getChilds()) {
                    child.release();
                }
            }
        }
        this.group = Group.findById(new ObjectId(mediaPipeline.getName()));

        if (this.composite == null) {
            this.composite = new Composite.Builder(mediaPipeline).build();
            this.composite.setName("composite");
            this.hubPort = getCompositePort("this");
            try {
                this.interval = new CreateIntervalService(group.getId().toString(),new Date()).execute();
                record(10000);
            } catch (ServiceException e) {
                e.printStackTrace();
            }

        }
        System.out.println("ROOM " + mediaPipeline.getName() + " has been created");
    }

    public HubPort getHubPort() {
        return hubPort;
    }

    private void record(int duration) {
        MyRecorder.record(hubPort,new Date(),duration,new MyRecorder.RecorderHandler() {
            @Override
            public void onFileRecorded(Date begin, Date end, String filepath, String filename) {
                try {
                    CreateRecordingService srs = new CreateRecordingService(filepath, getGroupId(), group.getId().toString(), begin,
                            end, filename, "video/webm");
                    Recording rec = srs.execute();
                    if (rec != null) {

                        interval.setEnd(end);
                        interval.save();

                        JSONArray array = new JSONArray();
                        array.put(Tools.FORMAT.format(interval.getStart()));
                        array.put(Tools.FORMAT.format(interval.getEnd()));


                        JSONObject msg = new JSONObject();
                        msg.put("id", "rec");
                        msg.put(interval.getId().toString(), array);
                        sendMessage(msg.toString());

                        System.out.println("REC: " + filepath);
                    } else {
                        return;
                    }
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
                if(participants.size()>0) {
                    record(duration);
                }
            }
        });

        for(UserSession session : participants.values()){
            session.record(duration);
        }


    }

    public HubPort getCompositePort(String id) {
        for (MediaObject port : getComposite().getChilds()) {
            if (port.getName().equals(id)) {
                return (HubPort) port;
            }
        }

        HubPort port = new HubPort.Builder(getComposite()).build();
        port.setName(id);
        return port;
    }

    public void sendMessage(final String string) {
        for (UserSession user : participants.values()) {
            user.sendMessage(string);
        }
    }

    private Hub getComposite() {
        return composite;

    }

    @PreDestroy
    private void shutdown() {
        this.close();
    }

    public String getGroupId() {
        return group.getId().toString();
    }

    public UserSession join(final User user, final WebSocket.Out<String> out) {
        try {
            System.out.println(user.getId().toString() + " joining " + mediaPipeline.getName());
            final UserSession participant = new UserSession(user, this, out);
            // add myself to the room
            participants.put(participant.getUser().getId().toString(), participant);
            JSONArray otherUsers = new JSONArray();
            List<Attribute> attributes1 = new ListOwnerAttributesService(user.getId().toString(), user.getId().toString()).execute();
            JSONObject myProfile = new JSONObject();
            myProfile.put("id", user.getId().toString());
            for (Attribute attribute : attributes1) {
                myProfile.put(attribute.getKey(), attribute.getValue());
            }


            final JSONObject myAdvertise = new JSONObject().put("id", "participants").put("data",
                    new JSONArray().put(myProfile.put("online", true)));

            ListGroupMembersService service = new ListGroupMembersService(user.getId().toString(), getGroupId());

            for (KeyValuePair<Membership, User> m : service.execute()) {
                UserSession otherSession = participants.get(m.getKey().getUserId().toString());
                ObjectId otherId = m.getKey().getUserId();

                List<Attribute> attributes2 = new ListOwnerAttributesService(otherId.toString(),
                        m.getValue().getId().toString()).execute();
                JSONObject otherProfile = new JSONObject();
                otherProfile.put("id", otherId.toString());
                for (Attribute attribute : attributes2) {
                    otherProfile.put(attribute.getKey(), attribute.getValue());
                }


                otherProfile.put("online", otherSession != null);
                if (otherSession != null && !otherId.equals(user.getId())) {
                    otherSession.sendMessage(myAdvertise.toString());

                }
                otherUsers.put(otherProfile);
            }
            final JSONObject currentParticipants = new JSONObject().put("id", "participants").put("data", otherUsers);
            participant.sendMessage(currentParticipants.toString());

            return participant;
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void leave(final UserSession user) throws IOException {
        String uid = user.getUser().getId().toString();

        participants.remove(uid);
        try {

            List<Attribute> attributes = new ListOwnerAttributesService(uid, uid).execute();
            JSONObject result = new JSONObject();
            result.put("id",uid);
            for(Attribute attribute : attributes){
                result.put(attribute.getKey(),attribute.getValue());
            }


            final JSONObject myAdvertise = new JSONObject().put("id", "participants").put("data",
                    new JSONArray().put(result.put("online", false)));
            sendMessage(myAdvertise.toString());
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        user.close();
        if (participants.size() == 0) {
            close();
        }
    }

    /**
     * @return a collection with all the participants in the room
     */
    public Collection<UserSession> getParticipants() {
        return participants.values();
    }

    /**
     * @return the participant from this session
     */
    public UserSession getParticipant(final String uid) {
        return uid != null ? participants.get(uid) : null;
    }

    @Override
    public void close() {
        System.out.println("------------- ROOM CLOSE --------------");
        for (final UserSession user : participants.values()) {
            try {
                user.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        participants.clear();
        mediaPipeline.release();
        Global.manager.removeRoom(this);
    }

    public String getId() {
        return group.getId().toString();
    }

    public MediaPipeline getMediaPipeline() {
        return mediaPipeline;
    }

}
