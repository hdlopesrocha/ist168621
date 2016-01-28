package main;

import exceptions.ServiceException;
import models.HyperContent;
import models.Message;
import models.Recording;
import models.User;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kurento.client.*;
import org.kurento.jsonrpc.JsonUtils;
import org.kurento.repository.service.pojo.RepositoryItemPlayer;
import play.mvc.WebSocket;
import services.GetCurrentHyperContentService;
import services.GetCurrentRecordingService;
import services.ListMessagesService;

import java.io.Closeable;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * The Class UserSession.
 */
public class UserSession implements Closeable, Comparable<UserSession> {

    /** The out. */
    private final WebSocket.Out<String> out;
    
    /** The user. */
    private final User user;
    
    /** The room. */
    private final Room room;
    
    /** The end point. */
    private final WebRtcEndpoint endPoint;
    
    /** The composite point. */
    private final WebRtcEndpoint compositePoint;
    
    /** The end points. */
    private final Map<String, WebRtcEndpoint> endPoints = new TreeMap<String, WebRtcEndpoint>();
    
    /** The composite port. */
    private final HubPort compositePort;
    
    /** The player lock. */
    private final Object playerLock = new Object();
    
    /** The player. */
    private PlayerEndpoint player;
    
    /** The play. */
    private boolean play = true;
    
    /** The time offset. */
    private long timeOffset = 0L;
    
    /** The play user. */
    private String playUser = "";

    public Boolean isReceiveOnly() {
        return receiveOnly;
    }

    private Boolean receiveOnly = true;

    /**
     * Instantiates a new user session.
     *
     * @param user the user
     * @param room the room
     * @param out the out
     * @throws ServiceException the service exception
     */
    public UserSession(final User user, final Room room, WebSocket.Out<String> out) throws ServiceException {
        this.out = out;
        this.user = user;
        this.room = room;

        // XXX [ICE_01] XXX
        endPoint = getWebRtcEndPoint("main");
        compositePoint = getWebRtcEndPoint("mixer");

        compositePort = room.getCompositePort(user.getId().toString());


        endPoint.addMediaSessionStartedListener(new EventListener<MediaSessionStartedEvent>() {
            @Override
            public void onEvent(MediaSessionStartedEvent arg0) {
                endPoint.connect(endPoint);
                room.record();
            }
        });

        compositePoint.addMediaSessionStartedListener(new EventListener<MediaSessionStartedEvent>() {
            @Override
            public void onEvent(MediaSessionStartedEvent arg0) {
                endPoint.connect(compositePort/* , MediaType.AUDIO */);
                compositePort.connect(compositePoint, MediaType.AUDIO);
            }
        });

    }


    /**
     * Record.
     *
     * @param rec
     * @param duration the duration
     */
    public void record(Recording rec, int duration) {
        if(!isReceiveOnly()) {
            MyRecorder.record(endPoint, duration, new MyRecorder.RecorderHandler() {
                @Override
                public void onFileRecorded(Date end, String filepath) {

                    rec.setUrl(getUser().getId().toString(),filepath);
                    rec.save();
                    System.out.println("REC: " + filepath);

                }
            });
        }
    }

    /**
     * Gets the time offset.
     *
     * @return the time offset
     */
    private long getTimeOffset() {
        return timeOffset;
    }

    /**
     * Gets the content.
     *
     * @return the content
     */
    public String getContent() {
        long offset = getTimeOffset();
        Date time = new Date(new Date().getTime() - offset);
        JSONArray jArr = new JSONArray();
        boolean hasMore = false;
        try {
            GetCurrentHyperContentService service = new GetCurrentHyperContentService(user.getId().toString(),
                    room.getGroupId(), time);
            List<HyperContent> result = service.execute();
            hasMore = service.hasMore();
            for (HyperContent content : result) {
                String eventId = content.getId().toString();
                {
                    JSONObject jObj = new JSONObject();
                    jObj.put("time", Tools.FORMAT.format(content.getStart()));
                    jObj.put("type", "start");
                    jObj.put("id", eventId);
                    jObj.put("content", content.getContent());
                    jArr.put(jObj);
                }
                {
                    JSONObject jObj = new JSONObject();
                    jObj.put("time", Tools.FORMAT.format(content.getEnd()));
                    jObj.put("type", "end");
                    jObj.put("id", eventId);
                    jArr.put(jObj);
                }
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        JSONObject jRoot = new JSONObject();
        jRoot.put("id", "content");
        jRoot.put("data", jArr);
        jRoot.put("more", hasMore);
        return jRoot.toString();

    }

    /**
     * Gets the web rtc end point.
     *
     * @param name the name
     * @return the web rtc end point
     */
    private WebRtcEndpoint getWebRtcEndPoint(String name) {
        WebRtcEndpoint ep = endPoints.get(name);
        if (ep == null) {
            ep = new WebRtcEndpoint.Builder(room.getMediaPipeline()).build();
            ep.addOnIceCandidateListener(new EventListener<OnIceCandidateEvent>() {
                @Override
                public void onEvent(OnIceCandidateEvent event) {
                    JSONObject response = new JSONObject();
                    response.put("id", "iceCandidate");
                    response.put("name", name);
                    response.put("data", new JSONObject(JsonUtils.toJson(event.getCandidate())));

                    try {
                        sendMessage(response.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            ep.addErrorListener(new EventListener<ErrorEvent>() {
                @Override
                public void onEvent(ErrorEvent arg0) {
                    System.out.println("ERROR: " + arg0.getDescription());
                }
            });

            ep.setStunServerAddress("64.233.184.127");
            ep.setStunServerPort(19302);
            endPoints.put(name, ep);
        }
        return ep;
    }

    /**
     * Send message.
     *
     * @param string the string
     */
    public synchronized void sendMessage(final String string) {

        System.out.println("\nSEND:" + string);
        out.write(string);
    }

    /**
     * The room to which the user is currently attending.
     *
     * @return The room
     */
    private String getGroupId() {
        return room.getGroupId();
    }

    /* (non-Javadoc)
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        System.out.println("!!!!!!!!!!!!!!!!! CLOSING SESSION !!!!!!!!!!!!!!!!!");
        endPoint.disconnect(compositePort);
        compositePort.disconnect(compositePoint);
        compositePort.release();
        compositePoint.release();
        endPoint.release();
    }

    /**
     * Adds the candidate.
     *
     * @param candidate the candidate
     * @param name the name
     */
    public void addCandidate(IceCandidate candidate, String name) {
        WebRtcEndpoint ep = endPoints.get(name == null ? "main" : name);
        // XXX [CLIENT_ICE_04] XXX
        ep.addIceCandidate(candidate);
    }

    /**
     * Gets the user.
     *
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(UserSession o) {
        return getUser().compareTo(o.getUser());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof UserSession)) {
            return false;
        }
        UserSession other = (UserSession) obj;
        boolean eq = user.equals(other.getUser());
        eq &= getGroupId().equals(other.getGroupId());
        return eq;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + user.getId().hashCode();
        result = 31 * result + getGroupId().hashCode();
        return result;
    }

    /**
     * Sets the historic.
     *
     * @param userId the user id
     */
    public void setHistoric(String userId) {
        playUser = userId;
        Date currentTime = new Date(new Date().getTime() - timeOffset + 500);
        UserSession session = room.getParticipant(playUser);
        String owner = session != null ? session.getUser().getId().toString() : room.getGroupId();

        try {
            // saying "no video here!", for group video
            GetCurrentRecordingService service = new GetCurrentRecordingService(user.getId().toString(),
                    room.getGroupId(), currentTime);
            Recording rec = service.execute();

            if (rec != null) {
                synchronized (playerLock) {
                    if (player != null) {
                        player.stop();
                        player.release();
                        player = null;
                    }

                    // WEBM
                    String url = rec.getUrl(owner);
                    System.out.println("HISTORIC PLAY: " + url);
                    RepositoryItemPlayer item = KurentoManager.repository.getReadEndpoint(url);

                    player = new PlayerEndpoint.Builder(room.getMediaPipeline(), item.getUrl()).build();
                    // player = new
                    // PlayerEndpoint.Builder(room.getMediaPipeline(),
                    // uri).build();
                    player.addErrorListener(new EventListener<ErrorEvent>() {
                        @Override
                        public void onEvent(ErrorEvent arg0) {
                            System.out.println("FAILURE: " + arg0.getDescription());
                            setHistoric(playUser);
                        }
                    });

                    player.addEndOfStreamListener(new EventListener<EndOfStreamEvent>() {
                        @Override
                        public void onEvent(EndOfStreamEvent arg0) {
                            setHistoric(playUser);
                        }
                    });
                    player.connect(endPoint/* , MediaType.VIDEO */);
                    if (play) {
                        // player.connect(compositePoint , MediaType.AUDIO);
                        player.play();
                        JSONObject msg = new JSONObject();
                        msg.put("id", "setTime");
                        msg.put("time", Tools.FORMAT.format(rec.getStart()));
                        sendMessage(msg.toString());
                    }
                }
            } else {
                System.out.println("No video here!");
            }

        } catch (ServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }




    /**
     * Sets the realtime.
     *
     * @param userId the new realtime
     */
    public void setRealtime(String userId) {
        System.out.println("REALTIME");
        synchronized (playerLock) {
            if (player != null) {
                player.stop();
                player.release();
                player = null;
            }

            timeOffset = 0L;
            playUser = userId;
        }
        if (userId == null) {
            compositePort.connect(endPoint);
        } else {
            UserSession session = room.getParticipant(playUser);
            if (session != null) {
                session.endPoint.connect(endPoint);
            }
        }
        // mixerPort.connect(endPoint, MediaType.AUDIO);

    }

    /**
     * Process offer.
     *
     * @param description the description
     * @param name the name
     */
    public void processOffer(String description, String name) {
        receiveOnly = description.contains("a=recvonly");

        // XXX [CLIENT_ICE_04] XXX
        WebRtcEndpoint ep = endPoints.get(name == null ? "main" : name);

        // XXX [CLIENT_OFFER_04] XXX
        // XXX [CLIENT_OFFER_05] XXX

        String lsd = ep.processOffer(description);
        // XXX [CLIENT_OFFER_06] XXX
        JSONObject data = new JSONObject().put("sdp", lsd).put("type", "answer");

        JSONObject msg = new JSONObject().put("id", "answer").put("data", data).put("name", name);
        // XXX [CLIENT_OFFER_07] XXX
        sendMessage(msg.toString());
        ep.gatherCandidates();
    }

    /**
     * Sets the play.
     *
     * @param play the new play
     */
    public void setPlay(boolean play) {
        synchronized (playerLock) {

            if (player != null) {
                if (play) {
                    player.play();
                } else {
                    player.pause();
                }
            }
            this.play = play;
        }
    }

    /**
     * Send messages.
     *
     * @param end the end
     * @param len the len
     */
    public void sendMessages(Long end, int len) {
        ListMessagesService messagesService = new ListMessagesService(room.getGroupId(), end, len);
        JSONArray messagesArray = new JSONArray();
        try {
            List<Message> messages = messagesService.execute();
            for (Message message : messages) {
                JSONObject messageObj = message.toJsonObject();
                messagesArray.put(messageObj);
            }

        } catch (ServiceException e1) {
            e1.printStackTrace();
        }

        JSONObject msg = new JSONObject();
        msg.put("id", "msg");
        msg.put("data", messagesArray);
        sendMessage(msg.toString());
    }

    public void setOffset(long offset) {
        synchronized (playerLock) {
            this.timeOffset = offset;
        }
    }
}
