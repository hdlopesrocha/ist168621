package main;

import exceptions.ServiceException;
import models.HyperContent;
import models.Message;
import models.RecordingChunk;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kurento.client.*;
import org.kurento.client.EventListener;
import org.kurento.jsonrpc.JsonUtils;
import org.kurento.repository.service.pojo.RepositoryItemPlayer;
import play.mvc.WebSocket;
import services.*;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;


/**
 * The Class UserSession.
 */
public class UserSession implements Closeable, Comparable<UserSession> {

    private static final boolean QR_ENABLED = true;

    /** The out. */
    private final WebSocket.Out<String> out;
    
    private final Room room;

    private final Object playerLock = new Object();

    private boolean play = true;
    private long timeOffset = 0L;
    private String playSid;
    private ObjectId playingId;
    private String userId;
    private Boolean hasAudio = true;
    private Boolean hasVideo = true;
    private UUID sid = UUID.randomUUID();

    private Recorder recorder;
    private ZBarFilter qrCodeFilter;

    public String getUserId() {
        return userId;
    }

    private WebRtcEndpoint endPoint;
    private HubPort compositePort;
    private PlayerEndpoint playerVideo;
    private PlayerEndpoint playerAudio;

    private Map<String,Object> currentQRCodes = new TreeMap<String,Object>();
    private EventListener<CodeFoundEvent> codeListener;

    public UserSession(final String userId, final Room room, WebSocket.Out<String> out) throws ServiceException {
        this.out = out;
        this.userId = userId;
        this.room = room;
        playSid = "group";
        initEndpoint();
    }

    private void initEndpoint(){
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

        endPoint.setStunServerAddress("74.125.206.127");
        endPoint.setStunServerPort(19302);

        //    endPoint.setTurnUrl("citysdk.tagus.ist.utl.pt:3478");

        compositePort = room.getCompositePort(sid.toString());
        endPoint.addMediaSessionStartedListener(new EventListener<MediaSessionStartedEvent>() {
            @Override
            public void onEvent(MediaSessionStartedEvent arg0) {
                if(QR_ENABLED) {
                    initQRCodeReader();
                }
                endPoint.connect(compositePort);
                compositePort.connect(endPoint);
                room.record();
            }
        });
    }

    private void initQRCodeReader(){
        qrCodeFilter = new ZBarFilter.Builder(endPoint.getMediaPipeline()).build();
        codeListener = new EventListener<CodeFoundEvent>() {
            @Override
            public void onEvent(CodeFoundEvent event) {
                System.out.println(".");
                final String content = event.getValue();
                final String hash = Tools.md5(content);
                final Date startTime = new Date(new Date().getTime() - timeOffset);

                if (hash != null) {
                    boolean containsQR;
                    synchronized (currentQRCodes) {
                        containsQR = currentQRCodes.containsKey(hash);
                    }

                    if (!containsQR) {
                        JSONObject msg = new JSONObject();
                        msg.put("cmd", "qrCode");
                        msg.put("data", content);
                        msg.put("hash", hash);
                        room.sendMessage(msg.toString());
                    }

                    synchronized (currentQRCodes) {
                        currentQRCodes.put(hash,startTime);
                    }

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Object newestQRCode;
                            synchronized (currentQRCodes) {
                                newestQRCode = currentQRCodes.get(hash);
                            }

                            if (startTime == newestQRCode) {
                                synchronized (currentQRCodes) {
                                    currentQRCodes.remove(hash);
                                }
                                JSONObject msg = new JSONObject();
                                msg.put("cmd", "qrCode");
                                msg.put("hash", hash);
                                room.sendMessage(msg.toString());


                                Date endTime = new Date(new Date().getTime() - timeOffset);
                                CreateHyperContentService service = new CreateHyperContentService(userId,
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
        qrCodeFilter.addCodeFoundListener(codeListener);
        endPoint.connect(qrCodeFilter, MediaType.VIDEO);

    }

    /**
     * Record.
     *
      * @param duration the duration
     */
    public void record(int duration) {
        if(hasVideo()) {
           final Date start = new Date();
            recorder = new Recorder(endPoint, duration, new Recorder.RecorderHandler() {
                @Override
                public void onFileRecorded(String filepath) {
                    Date end = new Date();
                    RecordingChunk rec = new RecordingChunk(new ObjectId(room.getGroupId()),new ObjectId(userId),start,end,getSid(),filepath);
                    rec.save();
                }
            });
        }
    }

    public boolean hasAudio() {
        return hasAudio;
    }

    public boolean hasVideo() {
        return hasVideo;
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
            GetCurrentHyperContentService service = new GetCurrentHyperContentService(getUserId(),
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
        if(qrCodeFilter!=null) {
            qrCodeFilter.release();
        }
        if(recorder!=null){
            recorder.release();
        }
        compositePort.release();
        endPoint.release();
    }

    public JSONObject sendChannels(){
        JSONArray channels = new JSONArray();

        if(timeOffset==0L) {
            channels = room.getCurrentChannels();
        }else {
            Date currentTime = new Date(new Date().getTime() - timeOffset);
            ListCurrentRecordingsService service = new ListCurrentRecordingsService(getUserId(),
                    getGroupId(), currentTime);
            try {
                List<RecordingChunk> recs = service.execute();
                for (RecordingChunk ru : recs) {
                    JSONObject obj = new JSONObject();
                    obj.put("sid",ru.getSid());
                    obj.put("uid",ru.getOwner());
                    channels.put(obj);
                }

            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }


        JSONObject ans = new JSONObject();
        ans.put("cmd","channels");
        ans.put("data",channels);
        ans.put("play",playSid);
        sendMessage(ans.toString());
        return ans;
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
        return sid.hashCode();
    }

    /**
     * Sets the historic.
     *
     */
    public void setHistoric(String owner, Long sequence, String sessionId) {

        playSid = sessionId;
        Date currentTime = new Date(new Date().getTime() - timeOffset);
        try {
            RecordingChunk videoRec;
            RecordingChunk audioRec;

            GetCurrentRecordingService audioService = new GetCurrentRecordingService(getUserId(),
                    room.getGroupId(), room.getGroupId(),"group", currentTime,sequence);
            audioRec = audioService.execute();

            if(getGroupId().equals(owner)){
                videoRec =audioRec;
            }else {
                GetCurrentRecordingService videoService = new GetCurrentRecordingService(getUserId(),
                        room.getGroupId(), owner, sessionId, currentTime, sequence);
                videoRec = videoService.execute();
            }

            String videoUrl=null;
            String audioUrl=null;
            if(audioRec != null){
                audioUrl = audioRec.getUrl();
            }

            if(videoRec != null){
                videoUrl = videoRec.getUrl();
            }



            if (videoUrl!=null && audioUrl!=null && !videoRec.getId().equals(playingId)) {
                playingId = videoRec.getId();
                synchronized (playerLock) {
                    System.out.println("HISTORIC PLAY: " + videoUrl+ " / "+ audioUrl);

                    if(ObjectId.isValid(videoUrl)){
                        RepositoryItemPlayer item =  KurentoManager.repository.getReadEndpoint(videoUrl);
                        videoUrl= item.getUrl();
                    }

                    System.out.println("URL: "+videoUrl);
                    PlayerEndpoint tempVideo = new PlayerEndpoint.Builder(room.getMediaPipeline(), videoUrl).build();

                    if(ObjectId.isValid(audioUrl)){
                        RepositoryItemPlayer item =  KurentoManager.repository.getReadEndpoint(audioUrl);
                        audioUrl= item.getUrl();
                    }

                    PlayerEndpoint tempAudio = new PlayerEndpoint.Builder(room.getMediaPipeline(),audioUrl).build();

                    tempAudio.addErrorListener(new EventListener<ErrorEvent>() {
                        @Override
                        public void onEvent(ErrorEvent arg0) {
                            System.out.println("FAILURE: " + arg0.getDescription());
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            setHistoric(owner,audioRec.getSequence()+1,audioRec.getSid());
                        }
                    });

                    tempAudio.addEndOfStreamListener(new EventListener<EndOfStreamEvent>() {
                        @Override
                        public void onEvent(EndOfStreamEvent arg0) {
                            setHistoric(owner,audioRec.getSequence()+1,audioRec.getSid());
                        }
                    });

                    // stop old video
                    if (playerVideo != null) {
                        playerVideo.release();
                    }
                    // stop old audio
                    if (playerAudio != null) {
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
                            Long position = currentTime.getTime()-audioRec.getStart().getTime();
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
                            msg.put("time", Tools.FORMAT.format(audioRec.getStart()));
                            timeOffset = new Date().getTime()- audioRec.getStart().getTime();
                            sendMessage(msg.toString());
                        }
                    }
                }
            } else {
                if(videoRec==null) {
                    playingId = null;
                    System.out.println("No video here!");
                }else {
                    System.out.println("Already playing!");
                }
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        sendChannels();
    }



    /**
     * Sets the realtime.
     *
     * @param userId the new realtime
     */
    public void setRealTime(String userId, String sessionId) {
        System.out.println("REALTIME");
        synchronized (playerLock) {
            if (playerVideo != null) {
                playerVideo.release();
                playerVideo = null;
            }

            if (playerAudio != null) {
                playerAudio.release();
                playerAudio = null;
            }
            timeOffset = 0L;
            playSid = sessionId;
        }
        if (userId == null) {
            compositePort.connect(endPoint);

        } else {
            UserSession session = room.getEndPoint(userId,sessionId);
            if (session != null) {
                session.endPoint.connect(endPoint,MediaType.VIDEO);
                session.endPoint.connect(compositePort,MediaType.AUDIO);
            }
        }
        sendChannels();
        // mixerPort.connect(endPoint, MediaType.AUDIO);

    }

    /**
     * Process offer.
     *
     * @param rsd the description
     */
    public void processOffer(String rsd) {
        String [] lines = rsd.split("\n");

        boolean isAudio = false;

        for(String line : lines) {
            if (line.startsWith("m")) {
                isAudio = line.contains("audio");
            }

            if (line.startsWith("a")) {
                if (line.contains("recvonly")) {
                    if (isAudio) {
                        hasAudio = false;
                    } else {
                        hasVideo = false;
                    }

                }
                if (line.contains("send")) {
                    if (isAudio) {
                        hasAudio = true;
                    } else {
                        hasVideo = true;
                    }
                }
            }
        }




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

    public String getSid() {
        return sid.toString();
    }

}
