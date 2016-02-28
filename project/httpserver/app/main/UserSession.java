package main;

import exceptions.ServiceException;
import models.HyperContent;
import models.Message;
import models.Recording;
import models.User;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kurento.client.*;
import org.kurento.commons.testing.SystemCompatibilityTests;
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
import java.util.UUID;


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
    private WebRtcEndpoint endPoint;

    /** The composite port. */
    private final HubPort compositePort;
    
    /** The playerVideo lock. */
    private final Object playerLock = new Object();
    
    /** The playerVideo. */
    private PlayerEndpoint playerVideo;

    /** The playerVideo. */
    private PlayerEndpoint playerAudio;

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

    private UUID sid = UUID.randomUUID();

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

        endPoint = new WebRtcEndpoint.Builder(room.getMediaPipeline()).build();
        endPoint.addOnIceCandidateListener(new EventListener<OnIceCandidateEvent>() {
            public void onEvent(OnIceCandidateEvent event) {
                JSONObject response = new JSONObject();
                response.put("id", "iceCandidate");
                response.put("data", new JSONObject(JsonUtils.toJson(event.getCandidate())));

                try {
                    sendMessage(response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        endPoint.addErrorListener(new EventListener<ErrorEvent>() {
            @Override
            public void onEvent(ErrorEvent arg0) {
                System.out.println("ERROR: " + arg0.getDescription());
            }
        });

        endPoint.setStunServerAddress("64.233.184.127");
        endPoint.setStunServerPort(19302);


        compositePort = room.getCompositePort(sid.toString());


        endPoint.addMediaSessionStartedListener(new EventListener<MediaSessionStartedEvent>() {
            @Override
            public void onEvent(MediaSessionStartedEvent arg0) {
                endPoint.connect(compositePort);
                compositePort.connect(endPoint);
                room.record();
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
            new Recorder(endPoint, duration, new Recorder.RecorderHandler() {
                @Override
                public void onFileRecorded(Date end, String filepath) {

                    rec.setUrl(getUser().getId().toString(),filepath);
                    rec.save();
                    System.out.println("USR REC: " + filepath);

                }
            });
        }
    }


    /**
     * Gets the content.
     *
     * @return the content
     */
    public String getContent() {
        Date time = new Date(new Date().getTime() - timeOffset);
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
        compositePort.release();
        endPoint.release();
    }

    /**
     * Adds the candidate.
     *
     * @param candidate the candidate
     */
    public void addCandidate(IceCandidate candidate) {
        // XXX [CLIENT_ICE_04] XXX
        endPoint.addIceCandidate(candidate);
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
        return sid.compareTo(o.sid);
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
        return sid.equals(other.sid);
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
        Date currentTime = new Date(new Date().getTime() - timeOffset + 10);

        try {
            // saying "no video here!", for group video
            GetCurrentRecordingService service = new GetCurrentRecordingService(user.getId().toString(),
                    room.getGroupId(), currentTime);
            Recording rec = service.execute();
            String ownerUrl=null;
            String groupUrl=null;
            if(rec!=null){
                ownerUrl = rec.getUrl(userId !=null ? userId : room.getId());
                groupUrl = rec.getUrl(room.getId());
            }


            if (ownerUrl!=null && groupUrl!=null) {
                synchronized (playerLock) {

                    // WEBM
                    System.out.println("HISTORIC PLAY: " + ownerUrl+ " / "+ groupUrl);
                    RepositoryItemPlayer itemVideo =  KurentoManager.repository.getReadEndpoint(ownerUrl);
                    System.out.println("URL: "+itemVideo.getUrl());
                    PlayerEndpoint tempVideo = new PlayerEndpoint.Builder(room.getMediaPipeline(), itemVideo.getUrl()).build();


                    RepositoryItemPlayer itemAudio = KurentoManager.repository.getReadEndpoint(groupUrl);
                    PlayerEndpoint tempAudio = new PlayerEndpoint.Builder(room.getMediaPipeline(), itemAudio.getUrl()).build();

                    tempVideo.addErrorListener(new EventListener<ErrorEvent>() {
                        @Override
                        public void onEvent(ErrorEvent arg0) {
                            System.out.println("FAILURE: " + arg0.getDescription());
                            setHistoric(playUser);
                        }
                    });

                    tempVideo.addEndOfStreamListener(new EventListener<EndOfStreamEvent>() {
                        @Override
                        public void onEvent(EndOfStreamEvent arg0) {
                            setHistoric(playUser);
                        }
                    });

                    if (playerVideo != null) {
                        playerVideo.stop();
                        playerVideo.release();
                    }

                    if (playerAudio != null) {
                        playerAudio.stop();
                        playerAudio.release();
                    }
                    playerVideo = tempVideo;
                    playerAudio = tempAudio;


                    if (play) {
                        playerAudio.connect(endPoint, MediaType.AUDIO);
                        playerVideo.connect(endPoint, MediaType.VIDEO);
                        playerAudio.play();
                        playerVideo.play();

                        if(playerVideo.getVideoInfo().getIsSeekable()) {
                            Long position = currentTime.getTime()-rec.getStart().getTime();
                            if(position >= playerVideo.getVideoInfo().getDuration()){
                                position = playerVideo.getVideoInfo().getDuration() -1;
                            }else if(position<0){
                                position = 0l;
                            }



                            playerVideo.setPosition(position);
                            if(playerAudio.getVideoInfo().getIsSeekable()) {
                                playerAudio.setPosition(position);
                            }
                        }
                        else {
                            JSONObject msg = new JSONObject();
                            msg.put("id", "setTime");
                            msg.put("time", Tools.FORMAT.format(rec.getStart()));
                            sendMessage(msg.toString());
                        }
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
    public void setRealTime(String userId) {
        System.out.println("REALTIME");
        synchronized (playerLock) {
            if (playerVideo != null) {
                playerVideo.stop();
                playerVideo.release();
                playerVideo = null;
            }

            if (playerAudio != null) {
                playerAudio.stop();
                playerAudio.release();
                playerAudio = null;
            }
            timeOffset = 0L;
            playUser = userId;
        }
        if (userId == null) {
            compositePort.connect(endPoint);
        } else {
            UserSession session = room.getUser(playUser);
            if (session != null) {
                session.endPoint.connect(endPoint,MediaType.VIDEO);
                session.endPoint.connect(compositePort,MediaType.AUDIO);
            }
        }
        // mixerPort.connect(endPoint, MediaType.AUDIO);

    }

    /**
     * Process offer.
     *
     * @param description the description
     */
    public void processOffer(String description) {
        receiveOnly = description.contains("a=recvonly");

        // XXX [CLIENT_ICE_04] XXX
        // XXX [CLIENT_OFFER_04] XXX
        // XXX [CLIENT_OFFER_05] XXX

        String lsd = endPoint.processOffer(description);
        // XXX [CLIENT_OFFER_06] XXX
        JSONObject data = new JSONObject().put("sdp", lsd).put("type", "answer");

        JSONObject msg = new JSONObject().put("id", "answer").put("data", data);
        // XXX [CLIENT_OFFER_07] XXX
        sendMessage(msg.toString());
        endPoint.gatherCandidates();
    }

    /**
     * Sets the play.
     *
     * @param play the new play
     */
    public void setPlay(boolean play) {
        synchronized (playerLock) {

            if (playerVideo != null) {
                if (play) {
                    playerVideo.play();
                } else {
                    playerVideo.pause();
                }
            }

            if (playerAudio != null) {
                if (play) {
                    playerAudio.play();
                } else {
                    playerAudio.pause();
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

    public UUID getSid() {
        return sid;
    }
}
