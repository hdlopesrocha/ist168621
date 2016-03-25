package main;

import exceptions.ServiceException;
import models.HyperContent;
import models.Message;
import models.RecordingChunk;
import models.User;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kurento.client.*;
import org.kurento.client.EventListener;
import org.kurento.jsonrpc.JsonUtils;
import org.kurento.repository.service.pojo.RepositoryItemPlayer;
import play.mvc.WebSocket;
import services.CreateHyperContentService;
import services.GetCurrentHyperContentService;
import services.GetCurrentRecordingService;
import services.ListMessagesService;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;


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
    private ObjectId playingId;

    public Boolean isReceiveOnly() {
        return receiveOnly;
    }

    private Boolean receiveOnly = true;

    private UUID sid = UUID.randomUUID();

    private ZBarFilter qrCodeFilter;
    private Object lastQRCode;
    private Map<String,Date> currentQRCodes = new TreeMap<String,Date>();
    /**
     * Instantiates a new user session.
     *
     * @param user the user
     * @param room the room
     * @param out the out
     * @throws ServiceException the service exception
     */
    private final EventListener<CodeFoundEvent> codeListener;
    private ListenerSubscription codeSubscritption;

    public UserSession(final User user, final Room room, WebSocket.Out<String> out) throws ServiceException {
        this.out = out;
        this.user = user;
        this.room = room;

        // XXX [ICE_01] XXX

        endPoint = new WebRtcEndpoint.Builder(room.getMediaPipeline()).build();
        endPoint.addOnIceCandidateListener(new EventListener<OnIceCandidateEvent>() {
            public void onEvent(OnIceCandidateEvent event) {
                JSONObject response = new JSONObject();
                response.put("cmd", "iceCandidate");
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

        qrCodeFilter = new ZBarFilter.Builder(endPoint.getMediaPipeline()).build();




       codeListener = new EventListener<CodeFoundEvent>() {
            @Override
            public void onEvent(CodeFoundEvent event) {
                System.out.println(".");
                final String content = event.getValue();
                final String hash = Tools.md5(content);
                if(hash!=null) {
                    boolean containsQR = false;
                    synchronized (currentQRCodes) {
                        containsQR = currentQRCodes.containsKey(hash);
                    }
                    if (!containsQR) {
                        synchronized (currentQRCodes) {
                            Date startTime = new Date(new Date().getTime() - timeOffset);

                            currentQRCodes.put(hash,startTime);
                        }
                        JSONObject msg = new JSONObject();
                        msg.put("cmd", "qrCode");
                        msg.put("data", content);
                        msg.put("hash", hash);
                        room.sendMessage(msg.toString());
                    }

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Object currentQRCode = new Object();
                            lastQRCode = currentQRCode;
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (lastQRCode == currentQRCode) {
                                Date startTime;
                                Date endTime = new Date(new Date().getTime() - timeOffset);
                                synchronized (currentQRCodes) {
                                    startTime = currentQRCodes.remove(hash);
                                }
                                JSONObject msg = new JSONObject();
                                msg.put("cmd", "qrCode");
                                msg.put("hash", hash);
                                room.sendMessage(msg.toString());

                                CreateHyperContentService service = new CreateHyperContentService(getUser().getId().toString(),
                                        getGroupId(), startTime, endTime, content);
                                try {
                                    service.execute();
                                    room.sendContents();
                                } catch (ServiceException e) {
                                    e.printStackTrace();
                                }


                            }
                        }
                    }).start();
                }
            }
        };

        codeSubscritption =  qrCodeFilter.addCodeFoundListener(codeListener);


        compositePort = room.getCompositePort(sid.toString());


        endPoint.addMediaSessionStartedListener(new EventListener<MediaSessionStartedEvent>() {
            @Override
            public void onEvent(MediaSessionStartedEvent arg0) {
                endPoint.connect(qrCodeFilter, MediaType.VIDEO);
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
    public void record(RecordingChunk rec, int duration) {
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
        jRoot.put("cmd", "content");
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
        System.out.println("SEND:" + string);
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
        qrCodeFilter.release();
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
    public void setHistoric(String userId, ObjectId interval, Long sequence) {
        playUser = userId;
        Date currentTime = new Date(new Date().getTime() - timeOffset);

        try {
            // saying "no video here!", for group video
            GetCurrentRecordingService service = new GetCurrentRecordingService(user.getId().toString(),
                    room.getGroupId(),interval, currentTime,sequence);
            RecordingChunk rec = service.execute();
            String ownerUrl=null;
            String groupUrl=null;
            if(rec!=null){
                ownerUrl = rec.getUrl(userId !=null ? userId : room.getId());
                groupUrl = rec.getUrl(room.getId());
            }


            if (ownerUrl!=null && groupUrl!=null && rec!=null && !rec.getId().equals(playingId)) {
                playingId = rec.getId();
                synchronized (playerLock) {
                    System.out.println("HISTORIC PLAY: " + ownerUrl+ " / "+ groupUrl);

                    String videoUrl = ownerUrl;
                    if(ObjectId.isValid(videoUrl)){
                        RepositoryItemPlayer item =  KurentoManager.repository.getReadEndpoint(videoUrl);
                        videoUrl= item.getUrl();
                    }

                    System.out.println("URL: "+videoUrl);
                    PlayerEndpoint tempVideo = new PlayerEndpoint.Builder(room.getMediaPipeline(), videoUrl).build();

                    String audioUrl = groupUrl;
                    if(ObjectId.isValid(audioUrl)){
                        RepositoryItemPlayer item =  KurentoManager.repository.getReadEndpoint(audioUrl);
                        audioUrl= item.getUrl();
                    }

                    PlayerEndpoint tempAudio = new PlayerEndpoint.Builder(room.getMediaPipeline(),audioUrl).build();

                    tempVideo.addErrorListener(new EventListener<ErrorEvent>() {
                        @Override
                        public void onEvent(ErrorEvent arg0) {
                            System.out.println("FAILURE: " + arg0.getDescription());
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            setHistoric(playUser, rec.getInterval(),rec.getSequence()+1);
                        }
                    });

                    tempVideo.addEndOfStreamListener(new EventListener<EndOfStreamEvent>() {
                        @Override
                        public void onEvent(EndOfStreamEvent arg0) {
                            setHistoric(playUser,rec.getInterval(),rec.getSequence()+1);
                        }
                    });

                    // stop old video
                    if (playerVideo != null) {
                        playerVideo.stop();
                        playerVideo.release();
                    }
                    // stop old audio
                    if (playerAudio != null) {
                        playerAudio.stop();
                        playerAudio.release();
                    }

                    playerVideo = tempVideo;
                    playerAudio = tempAudio;


                    if (play) {

                        tempAudio.play();
                        tempVideo.play();

                        tempAudio.connect(endPoint, MediaType.AUDIO);
                        tempVideo.connect(endPoint, MediaType.VIDEO);

                        if(tempVideo.getVideoInfo().getIsSeekable()) {
                            Long position = currentTime.getTime()-rec.getStart().getTime();
                            if(position >= tempVideo.getVideoInfo().getDuration()){
                                position = tempVideo.getVideoInfo().getDuration() -1;
                            }else if(position<0){
                                position = 0l;
                            }

                            tempVideo.setPosition(position);
                            if(tempAudio.getVideoInfo().getIsSeekable()) {
                                tempAudio.setPosition(position);
                            }
                        }
                        else {
                            System.out.println("not seekable!");
                            JSONObject msg = new JSONObject();
                            msg.put("cmd", "setTime");
                            msg.put("time", Tools.FORMAT.format(rec.getStart()));
                            timeOffset = new Date().getTime()- rec.getStart().getTime();
                            sendMessage(msg.toString());
                        }
                    }
                }
            } else {
                if(rec==null) {
                    playingId = null;
                    System.out.println("No video here!");
                }else {
                    System.out.println("Already playing!");
                }
            }
        } catch (ServiceException e) {
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
     * @param rsd the description
     */
    public void processOffer(String rsd) {
        receiveOnly = rsd.contains("a=recvonly");


        // XXX [CLIENT_ICE_04] XXX
        // XXX [CLIENT_OFFER_04] XXX
        // XXX [CLIENT_OFFER_05] XXX

        String lsd = endPoint.processOffer(rsd);
        // XXX [CLIENT_OFFER_06] XXX
        JSONObject data = new JSONObject().put("sdp", lsd).put("type", "answer");

        JSONObject msg = new JSONObject().put("cmd", "answer").put("data", data);
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
        msg.put("cmd", "msg");
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
