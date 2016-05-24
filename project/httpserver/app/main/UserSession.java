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
import java.net.InetAddress;
import java.net.UnknownHostException;
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
    private WebRtcEndpoint endPoint;
    private HubPort compositePort;
    private PlayerEndpoint playerVideo;
    private PlayerEndpoint playerAudio;
    private RecordingChunk nextVideo;
    private RecordingChunk nextAudio;
    private Timer scheduledPlayer;
    private Object scheduledPlayerLock = new Object();

    private Object QRLocker = new Object();
    private Map<String,Date> startingQRCodes = new TreeMap<String,Date>();
    private Map<String,Date> currentQRCodes = new TreeMap<String,Date>();


    private EventListener<CodeFoundEvent> codeListener;

    public UserSession(final String userId, final Room room, WebSocket.Out<String> out) throws ServiceException {
        this.out = out;
        this.userId = userId;
        this.room = room;
        playSid = "group";
        initEndpoint();

        System.out.println("new UserSession ---> " + sid.toString());
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


        try {
            InetAddress address = InetAddress.getByName("stun.iptel.org");
            String addr = address.getHostAddress();
            endPoint.setStunServerAddress(addr);
            //endPoint.setStunServerPort(19302);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }



/*
        try {
            InetAddress address = InetAddress.getByName("citysdk.tagus.ist.utl.pt");
            String addr = address.getHostAddress();
            System.out.println(addr);
            endPoint.setStunServerAddress(addr);
            endPoint.setStunServerPort(3478);
            endPoint.setTurnUrl("username:password@"+addr+":3478");

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //    endPoint.setTurnUrl("citysdk.tagus.ist.utl.pt:3478");
*/
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
                final Date currentTime = new Date(new Date().getTime() - timeOffset);

                if (hash != null) {
                    boolean containsQR;
                    synchronized (QRLocker) {
                        containsQR = startingQRCodes.containsKey(hash);

                    }

                    if (!containsQR) {
                        JSONObject msg = new JSONObject();
                        msg.put("cmd", "qrCode");
                        msg.put("data", content);
                        msg.put("hash", hash);
                        room.sendMessage(msg.toString());
                    }

                    synchronized (QRLocker) {
                        currentQRCodes.put(hash,currentTime);
                        if(!startingQRCodes.containsKey(hash)){
                            startingQRCodes.put(hash,currentTime);
                        }
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
                            synchronized (QRLocker) {
                                newestQRCode = currentQRCodes.get(hash);
                            }

                            if (currentTime == newestQRCode) {
                                Date startingTime;
                                synchronized (QRLocker) {
                                    currentQRCodes.remove(hash);
                                    startingTime= startingQRCodes.remove(hash);
                                }
                                JSONObject msg = new JSONObject();
                                msg.put("cmd", "qrCode");
                                msg.put("hash", hash);
                                room.sendMessage(msg.toString());


                                Date endTime = new Date(new Date().getTime() - timeOffset);
                                CreateHyperContentService service = new CreateHyperContentService(userId,
                                        getGroupId(), startingTime, endTime, content);
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
                    System.out.println("REC: "+sid.toString()+"|"+filepath);
                    Date end = new Date();
                    RecordingChunk rec = new RecordingChunk(new ObjectId(getGroupId()),new ObjectId(userId),start,end,getSid(),filepath);
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
            ListHyperContentService service = new ListHyperContentService(getUserId(),
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
                Map<String, RecordingChunk> uniqueAns = new TreeMap<String, RecordingChunk>();
                List<RecordingChunk> recs = service.execute();
                for (RecordingChunk ru : recs) {
                    uniqueAns.put(ru.getSid(),ru);
                }
                for(RecordingChunk ru : uniqueAns.values()) {
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


    private void cancelScheduledPlayers(){
        synchronized (scheduledPlayerLock) {
            if (scheduledPlayer != null) {
                scheduledPlayer.cancel();
                scheduledPlayer.purge();
                scheduledPlayer = null;
            }
        }
    }

    public void setHistoric(String owner, String sessionId) {
        if(owner==null){
            owner = getGroupId();
            sessionId = "group";
        }
        nextVideo = nextAudio = null;
        cancelScheduledPlayers();
        setInternalHistoric(owner,sessionId);
    }

    private void playChunks(String owner, String sessionId,RecordingChunk videoChunk,long seekVideo, RecordingChunk audioChunk,  long seekAudio){
        String videoUrl=videoChunk.getUrl();
        String audioUrl=audioChunk.getUrl();

        playingId = videoChunk.getId();
        synchronized (playerLock) {
            System.out.println("HISTORIC PLAY: " + videoUrl+ " / "+ audioUrl);


            if(ObjectId.isValid(audioUrl)){
                RepositoryItemPlayer item =  KurentoManager.repository.getReadEndpoint(audioUrl);
                audioUrl= item.getUrl();
            }
            System.out.println("URL: "+audioUrl);

            if(ObjectId.isValid(videoUrl)){
                RepositoryItemPlayer item =  KurentoManager.repository.getReadEndpoint(videoUrl);
                videoUrl= item.getUrl();
            }

            PlayerEndpoint tempAudio = new PlayerEndpoint.Builder(room.getMediaPipeline(), audioUrl).build();
            PlayerEndpoint tempVideo = new PlayerEndpoint.Builder(room.getMediaPipeline(), videoUrl).build();

            tempAudio.addErrorListener(new EventListener<ErrorEvent>() {
                @Override
                public void onEvent(ErrorEvent arg0) {
                    System.out.println("FAILURE: " + arg0.getDescription());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    setInternalHistoric(owner,audioChunk.getSid());
                }
            });

            tempAudio.addEndOfStreamListener(new EventListener<EndOfStreamEvent>() {
                @Override
                public void onEvent(EndOfStreamEvent arg0) {
                    setInternalHistoric(owner,audioChunk.getSid());
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
                if(tempVideo.getVideoInfo().getIsSeekable() && tempAudio.getVideoInfo().getIsSeekable()) {
                    // this is the ideal behavior
                    tempVideo.setPosition(seekVideo);
                    tempAudio.setPosition(seekAudio);
                }
                else {
                    // Warning!!! this only works if the block bounds are synchronized
                    System.out.println("not seekable!");
                    JSONObject msg = new JSONObject();
                    msg.put("cmd", "setTime");
                    msg.put("time", Tools.FORMAT.format(audioChunk.getStart()));
                    timeOffset = new Date().getTime()- audioChunk.getStart().getTime();
                    sendMessage(msg.toString());
                }
                try {
                    // prepare next audio
                    if (nextAudio == null) {
                        nextAudio = new GetNextRecordingService(getUserId(), room.getGroupId(), room.getGroupId(), "group", audioChunk.getId()).execute();
                    }
                    // prepare next video
                    if (nextVideo == null) {
                        nextVideo = new GetNextRecordingService(getUserId(), room.getGroupId(), owner, sessionId, videoChunk.getId()).execute();
                    }
                }catch (ServiceException e){
                    e.printStackTrace();
                }
            }
        }
    }


    private void setInternalHistoric(String owner, String sessionId) {
        playSid = sessionId;
        Date currentTime = new Date(new Date().getTime() - timeOffset);
        RecordingChunk videoRec=nextVideo;
        RecordingChunk audioRec=nextAudio;
        nextAudio=nextVideo = null;

        try {
            if(audioRec==null) {
                GetCurrentRecordingService audioService = new GetCurrentRecordingService(getUserId(), room.getGroupId(), room.getGroupId(), "group", currentTime);
                audioRec = audioService.execute();
            }
            if(videoRec==null) {
                if (getGroupId().equals(owner)) {
                    videoRec = audioRec;
                } else {
                    GetCurrentRecordingService videoService = new GetCurrentRecordingService(getUserId(), room.getGroupId(), owner, sessionId, currentTime);
                    videoRec = videoService.execute();
                }
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        if (videoRec!=null && audioRec!=null && !videoRec.getId().equals(playingId)) {
            if(audioRec.contains(currentTime)) {
                Long seekAudio = currentTime.getTime()-audioRec.getStart().getTime();
                Long seekVideo = currentTime.getTime()-videoRec.getStart().getTime();
                if(seekAudio<0){
                    seekAudio = 0l;
                }
                if(seekVideo<0){
                    seekVideo = 0l;
                }
                playChunks(owner,sessionId, videoRec,seekVideo,audioRec,seekAudio);
            }
            else {
                long time = audioRec.getStart().getTime() - currentTime.getTime();
                cancelScheduledPlayers();
                synchronized (scheduledPlayerLock) {
                    scheduledPlayer = new Timer();
                    System.out.print("SCHEDULLED!");
                    scheduledPlayer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            System.out.print("PLAYSCHED!");
                            setInternalHistoric(owner, sessionId);
                        }
                    }, time);
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
        UserSession session = userId!=null? room.getEndPoint(userId,sessionId): null;
        if (session != null) {
            session.endPoint.connect(endPoint,MediaType.VIDEO);
        }
        else {
            compositePort.connect(endPoint, MediaType.VIDEO);
        }
        compositePort.connect(endPoint,MediaType.AUDIO);

        nextVideo = nextAudio = null;
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
     * @param ts the end
     * @param len the len
     */
    public void sendMessages(Long ts, int len) {
        ListMessagesService messagesService = new ListMessagesService(room.getGroupId(), ts, len);
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
    public String getUserId() {
        return userId;
    }

}
