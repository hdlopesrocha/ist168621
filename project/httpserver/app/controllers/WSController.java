package controllers;

import exceptions.ServiceException;
import main.Global;
import main.Room;
import main.Tools;
import main.UserSession;
import models.Interval;
import models.Message;
import models.TimeTag;
import models.User;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kurento.client.IceCandidate;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.mvc.Controller;
import play.mvc.WebSocket;
import services.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class WSController extends Controller {

    public WebSocket<String> connectToRoom(String groupId) {
        final String userId = session("uid");
        return new WebSocket<String>() {
            
            /* (non-Javadoc)
             * @see play.mvc.WebSocket#onReady(play.mvc.WebSocket.In, play.mvc.WebSocket.Out)
             */
            public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {
                System.out.println("Join room " + groupId);
                Room room = Global.manager.getRoom(groupId);
                User user = User.findById(new ObjectId(userId));
                if (room != null) {
                    // Join room
                    final UserSession userSession = room.join(user, out);
                    {
                        List<Interval> intervals = Interval.listByGroup(new ObjectId(groupId));
                        JSONObject msg = new JSONObject();
                        msg.put("id", "rec");
                        for (Interval interval : intervals) {
                            Date start = interval.getStart();
                            Date end = interval.getEnd();

                            if (start != null && end != null) {

                                JSONArray rec = new JSONArray();
                                rec.put(Tools.FORMAT.format(start));
                                rec.put(Tools.FORMAT.format(end));
                                msg.put(interval.getId().toString(), rec);

                            }
                        }
                        room.sendMessage(msg.toString());
                    }
                    userSession.sendMessages(null, 1);

                    try {
                        ListTimeTagsService service = new ListTimeTagsService(userId, groupId);
                        List<TimeTag> tags = service.execute();
                        for (TimeTag tag : tags) {
                            JSONObject msg = new JSONObject();
                            msg.put("id", "tag");
                            msg.put("data", tag.toJson());
                            room.sendMessage(msg.toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ServiceException e) {
                        e.printStackTrace();
                    }

                    userSession.sendMessage(userSession.getContent());

                    {
                        JSONObject serverTime = new JSONObject();
                        serverTime.put("id","time");
                        JSONObject data = new JSONObject();
                        data.put("time", Tools.FORMAT.format(new Date()));
                        serverTime.put("data",data);
                        userSession.sendMessage(serverTime.toString());
                    }



                    {
                        UserSession coordinator = room.getCoordinator(userSession);
                        if(coordinator!=null){
                            JSONObject obj = new JSONObject();
                            obj.put("id","coordinate");
                            obj.put("sid",userSession.getSid().toString());
                            coordinator.sendMessage(obj.toString());
                        }
                    }

                    // When the socket is closed.
                    in.onClose(new Callback0() {
                        public void invoke() {
                            try {
                                room.leave(userSession);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            System.out.println("Disconnected");
                        }
                    });

                    // For each event received on the socket,
                    in.onMessage(new Callback<String>() {
                        public void invoke(String event) {

                            JSONObject args = new JSONObject(event);
                            String id = args.getString("id");


                            if(!"talk".equals(id))
                                System.out.println("\nRECV: " + event);
                            switch (id) {
                                case "createMessage": {
                                    String data = args.getString("data");
                                    CreateMessageService messageService = new CreateMessageService(
                                            user.getId().toString(), groupId, data);
                                    try {
                                        Message message = messageService.execute();
                                        JSONArray messagesArray = new JSONArray();
                                        JSONObject messageObj = message.toJsonObject();
                                        messagesArray.put(messageObj);
                                        JSONObject msg = new JSONObject();
                                        msg.put("id", "msg");
                                        msg.put("data", messagesArray);
                                        room.sendMessage(msg.toString());

                                    } catch (ServiceException e) {
                                        e.printStackTrace();
                                    }
                                    // send msg
                                }
                                break;
                                case "getMessages": {
                                    int len = args.getInt("len");
                                    Long end = args.getLong("end");
                                    userSession.sendMessages(end, len);
                                }
                                break;
                                case "offer": {
                                    JSONObject data = args.getJSONObject("data");
                                    String rsd = data.getString("sdp");
                                    userSession.processOffer(rsd);
                                }
                                break;
                                case "iceCandidate": {
                                    JSONObject jCand = args.getJSONObject("candidate");
                                    IceCandidate candidate = new IceCandidate(jCand.getString("candidate"),
                                            jCand.getString("sdpMid"), jCand.getInt("sdpMLineIndex"));
                                    userSession.addCandidate(candidate);
                                }
                                break;
                                case "setRealTime": {
                                    String userId = args.optString("uid", null);
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            userSession.setRealTime(userId);
                                            userSession.sendMessage(userSession.getContent());
                                        }
                                    }).start();
                                }
                                break;
                                case "removeUser": {
                                    String uid = args.optString("uid", null);
                                    RemoveGroupMemberService service = new RemoveGroupMemberService(userId, groupId, uid);
                                    try {
                                        service.execute();
                                    } catch (ServiceException e) {
                                        e.printStackTrace();
                                    }
                                    final JSONObject myAdvertise = new JSONObject().put("id", "removedUser").put("uid", uid);
                                    room.sendMessage(myAdvertise.toString());
                                }
                                break;
                                case "addUser": {
                                    String uid = args.optString("uid", null);
                                    AddGroupMemberService service = new AddGroupMemberService(userId, groupId, uid);
                                    try {
                                        service.execute();
                                        Document attributes = new ListOwnerAttributesService(userId, uid, null).execute();
                                        JSONObject result = new JSONObject(attributes.toJson());
                                        result.put("id", uid);
                                        final JSONObject myAdvertise = new JSONObject().put("id", "participants").put("data",
                                                new JSONArray().put(result.put("online", false)));
                                        room.sendMessage(myAdvertise.toString());
                                    } catch (ServiceException e) {
                                        e.printStackTrace();
                                    }
                                }
                                break;
                                case "play": {
                                    boolean play = args.getBoolean("data");
                                    userSession.setPlay(play);
                                }
                                break;
                                case "getContent": {
                                    userSession.sendMessage(userSession.getContent());
                                }
                                break;
                                case "talk": {
                                    Boolean value = args.optBoolean("value", false);
                                    JSONObject msg = new JSONObject();
                                    msg.put("id", "talk");
                                    JSONObject data = new JSONObject();
                                    data.put("uid", user.getId().toString());
                                    data.put("value", value);
                                    msg.put("data", data);
                                    room.sendMessage(msg.toString());
                                }
                                break;
                                case "setHistoric": {
                                    String userId = args.optString("uid", null);
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            userSession.setOffset(args.getLong("offset"));
                                            userSession.setHistoric(userId);
                                            userSession.sendMessage(userSession.getContent());
                                        }
                                    }).start();
                                }
                                break;
                                case "createTag": {
                                    try {
                                        Date time = Tools.FORMAT.parse(args.getString("time"));
                                        String title = args.getString("title");
                                        String content = args.getString("content");
                                        CreateTimeTagService service = new CreateTimeTagService(groupId, time, title, content);
                                        TimeTag tag = service.execute();
                                        JSONObject msg = new JSONObject();
                                        msg.put("id", "tag");
                                        msg.put("data", tag.toJson());
                                        room.sendMessage(msg.toString());
                                    } catch (ServiceException | JSONException | ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                                break;
                                case "createContent": {
                                    try {
                                        Date start = Tools.FORMAT.parse(args.getString("start"));
                                        Date end = Tools.FORMAT.parse(args.getString("end"));
                                        String content = args.getString("content");
                                        CreateHyperContentService service = new CreateHyperContentService(userId,
                                                groupId, start, end, content);
                                        service.execute();
                                    } catch (ServiceException | ParseException e) {
                                        e.printStackTrace();
                                    }
                                    // System.out.println("content");
                                    room.sendContents();

                                }
                                break;
                                case "deleteContent": {
                                    String contentId = args.getString("cid");
                                    try {
                                        DeleteHyperContentService service = new DeleteHyperContentService(userId,
                                                groupId, contentId);
                                        service.execute();
                                    } catch (ServiceException e) {
                                        e.printStackTrace();
                                    }
                                    // System.out.println("content");
                                   room.sendContents();
                                }
                                case "operation" : {
                                    if (args.has("sid")) {
                                        UserSession sess = room.getUser(UUID.fromString(args.getString("sid")));
                                        sess.sendMessage(event);
                                    } else {
                                        room.sendMessage(userSession, event);
                                    }
                                }
                                break;
                                default:
                                    break;
                            }
                        }
                    });
                } else {
                    out.close();
                }
            }
        };
    }

    private Set<WSPair> wspairs = new HashSet<WSPair>();

    private class WSPair implements Comparable<WSPair> {
        private WebSocket.In<String> in ;
        private WebSocket.Out<String> out ;
        private UUID uuid;

        public WSPair(WebSocket.In<String> in, WebSocket.Out<String> out) {
            this.in = in;
            this.out = out;
            this.uuid = UUID.randomUUID();
        }

        @Override
        public int compareTo(WSPair o) {
            return uuid.compareTo(o.uuid);
        }



    }


    public WebSocket<String> together(String groupId) {
        final String userId = session("uid");
        return new WebSocket<String>() {

            public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {
                final WSPair wsPair = new WSPair(in, out);

                synchronized (wspairs){
                    wspairs.add(wsPair);
                }
                // When the socket is closed.
                in.onClose(new Callback0() {
                    public void invoke() {
                        synchronized (wspairs){
                            wspairs.remove(wsPair);
                        }

                        System.out.println("Disconnected");
                    }
                });
                // For each event received on the socket,
                in.onMessage(new Callback<String>() {
                    public void invoke(String event) {
                        System.out.println("\nRECV: " + event);
                        synchronized (wspairs){
                            for(WSPair wsp : wspairs){
                                if(!wsp.equals(wsPair)){
                                    wsp.out.write(event);
                                }
                            }
                        }
                    }
                });
            }
        };
    }
}
