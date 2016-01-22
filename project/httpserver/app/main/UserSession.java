package main;

import exceptions.ServiceException;
import models.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kurento.client.*;
import org.kurento.jsonrpc.JsonUtils;
import org.kurento.repository.service.pojo.RepositoryItemPlayer;
import play.mvc.WebSocket;
import services.*;

import java.io.Closeable;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class UserSession implements Closeable, Comparable<UserSession> {

    private final WebSocket.Out<String> out;
    private final User user;
    private final Room room;
    private final WebRtcEndpoint endPoint;
    private final WebRtcEndpoint compositePoint;
    private final Map<String, WebRtcEndpoint> endPoints = new TreeMap<String, WebRtcEndpoint>();
    private final HubPort compositePort;
    private PlayerEndpoint player;
    private final Object playerLock = new Object();
    private boolean play = true;
    private long timeOffset = 0L;
    private String playUser = "";

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
              //  record(10000);
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


    public void record(int duration) {
        MyRecorder.record(endPoint,new Date(),duration,new MyRecorder.RecorderHandler() {
            @Override
            public void onFileRecorded(Date begin, Date end, String filepath, String filename) {
                try {
                    CreateRecordingService srs = new CreateRecordingService(filepath, getGroupId(), user.getId().toString(), begin,
                            end, filename, "video/webm");
                    Recording rec = srs.execute();
                    if (rec != null) {
                        System.out.println("REC: " + filepath);
                    }
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private long getTimeOffset() {
        return timeOffset;
    }

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

    public synchronized void sendMessage(final String string) {

        System.out.println("\nSEND:" + string);
        out.write(string);
    }

    /**
     * The room to which the user is currently attending
     *
     * @return The room
     */
    private String getGroupId() {
        return room.getGroupId();
    }

    @Override
    public void close() throws IOException {
        System.out.println("!!!!!!!!!!!!!!!!! CLOSING SESSION !!!!!!!!!!!!!!!!!");
        endPoint.disconnect(compositePort);
        compositePort.disconnect(compositePoint);
        compositePort.release();
        compositePoint.release();
        endPoint.release();
    }

    public void addCandidate(IceCandidate candidate, String name) {
        WebRtcEndpoint ep = endPoints.get(name == null ? "main" : name);
        // XXX [CLIENT_ICE_04] XXX
        ep.addIceCandidate(candidate);
    }

    public User getUser() {
        return user;
    }

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

    public void setHistoric(String userId, long offset) {
        timeOffset = offset;
        playUser = userId;
        Date currentTime = new Date(new Date().getTime() - timeOffset + 500);
        UserSession session = room.getParticipant(playUser);
        String owner = session != null ? session.getUser().getId().toString() : room.getGroupId();

        try {
            // saying "no video here!", for group video
            GetCurrentRecordingService service = new GetCurrentRecordingService(user.getId().toString(),
                    room.getGroupId(), owner, currentTime);
            Recording rec = service.execute();

            if (rec != null) {
                synchronized (playerLock) {
                    if (player != null) {
                        player.stop();
                        player.release();
                        player = null;
                    }

                    // WEBM
                    System.out.println("HISTORIC PLAY: " + rec.getUrl());
                    RepositoryItemPlayer item = KurentoManager.repository.getReadEndpoint(rec.getName());
                    player = new PlayerEndpoint.Builder(room.getMediaPipeline(), item.getUrl()).build();
                    // player = new
                    // PlayerEndpoint.Builder(room.getMediaPipeline(),
                    // uri).build();
                    player.addErrorListener(new EventListener<ErrorEvent>() {
                        @Override
                        public void onEvent(ErrorEvent arg0) {
                            System.out.println("FAILURE: " + arg0.getDescription());
                            setHistoric(playUser, timeOffset);
                        }
                    });

                    player.addEndOfStreamListener(new EventListener<EndOfStreamEvent>() {
                        @Override
                        public void onEvent(EndOfStreamEvent arg0) {
                            setHistoric(playUser, timeOffset);

                        }
                    });
                    player.connect(endPoint/* , MediaType.VIDEO */);
                    // player.connect(compositePoint , MediaType.AUDIO);
                    if (play) {
                        player.play();
                        JSONObject msg = new JSONObject();
                        msg.put("id","setTime");
                        msg.put("time",Tools.FORMAT.format(rec.getStart()));
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

    public void setRealtime(String userId) {
        System.out.println("REALTIME");
        synchronized (playerLock) {
            if (player != null) {
                player.stop();
                player.release();
                player = null;
            }
        }
        timeOffset = 0L;
        playUser = userId;
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

    public void processOffer(String description, String name) {
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
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        JSONObject msg = new JSONObject();
        msg.put("id", "msg");
        msg.put("data", messagesArray);
        sendMessage(msg.toString());
    }

}
