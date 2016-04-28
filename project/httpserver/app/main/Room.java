package main;

import exceptions.ServiceException;
import models.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kurento.client.*;
import org.kurento.client.EventListener;
import play.mvc.WebSocket;
import services.CreateIntervalService;
import services.GetCurrentRecordingService;
import services.ListGroupMembersService;
import services.ListOwnerAttributesService;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;


/**
 * The Class Room.
 */
public class Room implements Closeable {

    private  final static  Random RANDOM = new Random();
    /** The participants. */
    private final Set<UserSession> participants = new HashSet<UserSession>();
    
    /** The media pipeline. */
    private final MediaPipeline mediaPipeline;
    
    /** The group. */
    private final String groupId;
    
    /** The interval. */
    private RecordingInterval interval;
    
    /** The composite. */
    private Hub composite = null;
    private long sequence = 0;
    /** The hub port. */
    private HubPort compositePort;
    private Recorder recorder;
    private Object operationLock = new Object();



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

        this.groupId = mediaPipeline.getName();

        this.composite = getComposite();
        System.out.println("composite for room " + mediaPipeline.getName() + " has been created");
        this.compositePort = getCompositePort("this");

        System.out.println("ROOM " + mediaPipeline.getName() + " has been created");
    }

    public JSONArray getCurrentChannels(){
        JSONArray ans = new JSONArray();

        for (UserSession session : participants) {
            if(session.hasVideo() || session.hasAudio()) {
                JSONObject obj = new JSONObject();
                obj.put("uid", session.getUserId());
                obj.put("sid", session.getSid());
                ans.put(obj);
            }
        }
        return ans;
    }



    private boolean recording = false;

    /**
     * Record.
     *
     */
    public void record(){
        record(10000,false);
    }

    /**
     * Record.
     *
     * @param duration the duration
     */

    private synchronized void record(int duration, boolean continuation) {
        if(!recording || continuation) {



            boolean r = false;
            synchronized (participants) {
                for (UserSession session : participants) {
                    if (session.hasVideo() || session.hasAudio()) {
                        r = true;
                        break;
                    }
                }
            }

            recording = r;
            if (recording) {
                try {
                    if(this.interval==null) {
                        this.interval = new CreateIntervalService(groupId, new Date()).execute();
                    }
                    //record(10000);
                } catch (ServiceException e) {
                    e.printStackTrace();
                }

                Date start = new Date();

                ObjectId gid = new ObjectId(groupId);
                recorder = new Recorder(compositePort, duration, new Recorder.RecorderHandler() {
                    @Override
                    public void onFileRecorded(String filepath) {
                        Date end = new Date();

                        RecordingChunk rec = new RecordingChunk(gid,gid,start,end,"group",filepath);

                        rec.save();

                        interval.setEnd(end);
                        interval.save();

                        JSONArray array = new JSONArray();
                        array.put(Tools.FORMAT.format(interval.getStart()));
                        array.put(Tools.FORMAT.format(interval.getEnd()));

                        JSONObject msg = new JSONObject();
                        msg.put("cmd", "rec");
                        msg.put(interval.getId().toString(), array);
                        sendMessage(msg.toString());

                        System.out.println("GRP REC: " + filepath);
                        record(duration,true);
                    }
                });
                synchronized (participants) {
                    for (UserSession session : participants) {
                        session.record(duration);
                    }
                }
            }
        }
    }



    private Hub getComposite(){
        Composite ans =  new Composite.Builder(mediaPipeline).build();
        ans.setName("composite");
        return ans;
    }

    /**
     * Gets the composite port.
     *
     * @param id the id
     * @return the composite port
     */
    public HubPort getCompositePort(String id) {
        HubPort port = new HubPort.Builder(composite).build();
        port.setName(id);
        return port;
    }

    /**
     * Send message.
     *
     * @param string the string
     */
    public void sendMessage(final String string) {
        synchronized (participants) {
            for (UserSession user : participants) {
                user.sendMessage(string);
            }
        }
    }

    public void sendMessage(final UserSession ignore,final String string) {
        synchronized (participants) {
            for (UserSession user : participants) {
                if(user!=ignore) {
                    user.sendMessage(string);
                }
            }
        }
    }


    public Object getOperationLock() {
        return operationLock;
    }


    /**
     * Join.
     *
     * @param out the out
     * @return the user session
     */
    public UserSession join(final String userId, final WebSocket.Out<String> out) {
        try {
            synchronized (participants) {
                System.out.println(userId + " joining " + mediaPipeline.getName());
                final UserSession participant = new UserSession(userId, this, out);

                // add myself to the room
                participants.add(participant);
                JSONArray otherUsers = new JSONArray();
                Document attributes1 = new ListOwnerAttributesService(userId, userId, null).execute();
                JSONObject myProfile = new JSONObject(attributes1.toJson());
                myProfile.put("id", userId);

                final JSONObject myAdvertise = new JSONObject().put("cmd", "participants").put("data",
                        new JSONArray().put(myProfile.put("online", true)));

                ListGroupMembersService service = new ListGroupMembersService(userId, getGroupId());
                for (KeyValuePair<GroupMembership, User> m : service.execute()) {
                    UserSession otherSession = null;
                    for (UserSession os : participants) {
                        if (os.getUserId().equals(m.getValue().getId().toString())) {
                            otherSession = os;
                        }
                    }
                    ObjectId otherId = m.getKey().getUserId();
                    Document attributes2 = new ListOwnerAttributesService(otherId.toString(),
                            m.getValue().getId().toString(), null).execute();
                    JSONObject otherProfile = new JSONObject(attributes2.toJson());
                    otherProfile.put("id", otherId.toString());
                    otherProfile.put("online", otherSession != null);
                    if (otherSession != null && !participant.equals(otherSession)) {
                        otherSession.sendMessage(myAdvertise.toString());
                    }
                    otherUsers.put(otherProfile);
                }
                final JSONObject currentParticipants = new JSONObject().put("cmd", "participants").put("data", otherUsers);
                participant.sendMessage(currentParticipants.toString());
                for(UserSession us : participants){
                    us.sendChannels();
                }
                return participant;
            }
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
        String uid = user.getUserId();
        synchronized (participants) {
            participants.remove(user);
            try {
                Document attributes = new ListOwnerAttributesService(uid, uid, null).execute();
                JSONObject result = new JSONObject(attributes.toJson());
                result.put("id", uid);

                final JSONObject myAdvertise = new JSONObject().put("cmd", "participants").put("data",
                        new JSONArray().put(result.put("online", false)));
                sendMessage(myAdvertise.toString());
            } catch (ServiceException e) {
                e.printStackTrace();
            }
            user.close();
            for(UserSession us : participants){
                us.sendChannels();
            }

            if (participants.size() == 0) {
                close();
            }



        }

    }



    /**
     * Gets the participant.
     *
     * @param uid the uid
     * @return the participant from this session
     */
    public UserSession getEndPoint(final String uid, final String sid) {
        UserSession alternativeSession = null;



        if(uid!=null) {
            synchronized (participants) {
                for (UserSession session : participants) {
                    if (uid.equals(session.getUserId())) {
                        if(sid!=null && sid.equals(session.getSid())) {
                            return session;
                        }
                        alternativeSession = session;
                    }
                }
            }
        }
        return alternativeSession;
    }

    public UserSession getUser(final String sid) {
        if(sid!=null) {
            synchronized (participants) {
                for (UserSession session : participants) {
                    if (sid.equals(session.getSid())) {
                        return session;
                    }
                }
            }
        }
        return null;
    }


    /* (non-Javadoc)
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() {
        System.out.println("------------- ROOM CLOSE --------------");
        synchronized (participants) {
            for (final UserSession user : participants) {
                try {
                    user.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            participants.clear();
        }
        if(recorder!=null){
            recorder.release();
        }

        compositePort.release();
        composite.release();
        mediaPipeline.release();
        Global.manager.removeRoom(this);
    }



    /**
     * Gets the group id.
     *
     * @return the group id
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Gets the media pipeline.
     *
     * @return the media pipeline
     */
    public MediaPipeline getMediaPipeline() {
        return mediaPipeline;
    }

    public void sendContents() {
        synchronized (participants) {
            for (UserSession us : participants) {
                us.sendMessage(us.getContent());
            }
        }
    }

    public UserSession getCoordinator(UserSession ignore) {
        synchronized (participants){
            List<UserSession> candidates = new ArrayList<UserSession>();
            for (UserSession us : participants) {
                if(us!=ignore){
                    candidates.add(us);
                }
            }
            if(candidates.size()>0){
                return candidates.get(RANDOM.nextInt(candidates.size()));
            }
            return null;
        }
    }
}
