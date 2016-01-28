package main;

import exceptions.ServiceException;
import models.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kurento.client.*;
import play.mvc.WebSocket;
import services.CreateIntervalService;
import services.ListGroupMembersService;
import services.ListOwnerAttributesService;

import javax.annotation.PreDestroy;
import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * The Class Room.
 */
public class Room implements Closeable {

    /** The participants. */
    private final ConcurrentMap<String, UserSession> participants = new ConcurrentHashMap<String, UserSession>();
    
    /** The media pipeline. */
    private final MediaPipeline mediaPipeline;
    
    /** The group. */
    private final Group group;
    
    /** The interval. */
    private Interval interval;
    
    /** The composite. */
    private Hub composite = null;
    
    /** The hub port. */
    private HubPort hubPort;

    /**
     * Instantiates a new room.
     *
     * @param mediaPipeline the media pipeline
     */
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
            System.out.println("composite for room " + mediaPipeline.getName() + " has been created");
        }
        System.out.println("ROOM " + mediaPipeline.getName() + " has been created");
    }

    /**
     * Gets the hub port.
     *
     * @return the hub port
     */
    public HubPort getHubPort() {
        return hubPort;
    }


    private boolean recording = false;

    /**
     * Record.
     *
     */
    public void record(){
        record(10000,null);
    }

    /**
     * Record.
     *
     * @param duration the duration
     */

    private synchronized void record(int duration, Date start) {
        if(!recording || start!=null) {

            try {
                if(this.interval==null) {
                    this.interval = new CreateIntervalService(group.getId().toString(), new Date()).execute();
                }
                //record(10000);
            } catch (ServiceException e) {
                e.printStackTrace();
            }



            boolean r = false;
            for (UserSession session : participants.values()) {
                if (!session.isReceiveOnly()) {
                    r = true;
                    break;
                }
            }

            recording = r;
            if (recording) {
                if(start==null){
                    start = new Date();
                }

                Recording rec = new Recording(group.getId(),start);


                MyRecorder.record(hubPort, duration, new MyRecorder.RecorderHandler() {

                    @Override
                    public void onFileRecorded(Date end, String filepath) {
                        rec.setEnd(end);
                        rec.setUrl(group.getId().toString(),filepath);
                        rec.save();

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
                        record(duration,end);
                    }
                });

                for (UserSession session : participants.values()) {
                    session.record(rec,duration);
                }
            }
        }
    }

    /**
     * Gets the composite port.
     *
     * @param id the id
     * @return the composite port
     */
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

    /**
     * Send message.
     *
     * @param string the string
     */
    public void sendMessage(final String string) {
        for (UserSession user : participants.values()) {
            user.sendMessage(string);
        }
    }

    /**
     * Gets the composite.
     *
     * @return the composite
     */
    private Hub getComposite() {
        return composite;

    }

    /**
     * Shutdown.
     */
    @PreDestroy
    private void shutdown() {
        this.close();
    }

    /**
     * Gets the group id.
     *
     * @return the group id
     */
    public String getGroupId() {
        return group.getId().toString();
    }

    /**
     * Join.
     *
     * @param user the user
     * @param out the out
     * @return the user session
     */
    public UserSession join(final User user, final WebSocket.Out<String> out) {
        try {
            System.out.println(user.getId().toString() + " joining " + mediaPipeline.getName());
            final UserSession participant = new UserSession(user, this, out);
            // add myself to the room
            participants.put(participant.getUser().getId().toString(), participant);
            JSONArray otherUsers = new JSONArray();
            Document attributes1 = new ListOwnerAttributesService(user.getId().toString(), user.getId().toString(), null).execute();
            JSONObject myProfile = new JSONObject(attributes1.toJson());
            myProfile.put("id", user.getId().toString());

            final JSONObject myAdvertise = new JSONObject().put("id", "participants").put("data",
                    new JSONArray().put(myProfile.put("online", true)));

            ListGroupMembersService service = new ListGroupMembersService(user.getId().toString(), getGroupId());

            for (KeyValuePair<Membership, User> m : service.execute()) {
                UserSession otherSession = participants.get(m.getKey().getUserId().toString());
                ObjectId otherId = m.getKey().getUserId();

                Document attributes2 = new ListOwnerAttributesService(otherId.toString(),
                        m.getValue().getId().toString(), null).execute();
                JSONObject otherProfile = new JSONObject(attributes2.toJson());
                otherProfile.put("id", otherId.toString());


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

    /**
     * Leave.
     *
     * @param user the user
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void leave(final UserSession user) throws IOException {
        String uid = user.getUser().getId().toString();

        participants.remove(uid);
        try {

            Document attributes = new ListOwnerAttributesService(uid, uid, null).execute();
            JSONObject result = new JSONObject(attributes.toJson());
            result.put("id", uid);

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
     * Gets the participants.
     *
     * @return a collection with all the participants in the room
     */
    public Collection<UserSession> getParticipants() {
        return participants.values();
    }

    /**
     * Gets the participant.
     *
     * @param uid the uid
     * @return the participant from this session
     */
    public UserSession getParticipant(final String uid) {
        return uid != null ? participants.get(uid) : null;
    }

    /* (non-Javadoc)
     * @see java.io.Closeable#close()
     */
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

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId() {
        return group.getId().toString();
    }

    /**
     * Gets the media pipeline.
     *
     * @return the media pipeline
     */
    public MediaPipeline getMediaPipeline() {
        return mediaPipeline;
    }

}
