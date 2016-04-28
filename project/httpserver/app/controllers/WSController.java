package controllers;

import exceptions.ServiceException;
import main.Global;
import main.Room;
import main.Tools;
import main.UserSession;
import models.Message;
import models.RecordingInterval;
import models.TimeAnnotation;
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
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
                if (room != null) {
                    // Join room
                    final UserSession userSession = room.join(userId, out);
                    {
                        List<RecordingInterval> intervals = RecordingInterval.listByGroup(new ObjectId(groupId));
                        JSONObject msg = new JSONObject();
                        msg.put("cmd", "rec");
                        for (RecordingInterval interval : intervals) {
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
                        List<TimeAnnotation> tags = service.execute();
                        for (TimeAnnotation tag : tags) {
                            JSONObject msg = new JSONObject();
                            msg.put("cmd", "tag");
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
                        serverTime.put("cmd","time");
                        JSONObject data = new JSONObject();
                        data.put("time", Tools.FORMAT.format(new Date()));
                        serverTime.put("data",data);
                        userSession.sendMessage(serverTime.toString());
                    }



                    {
                        UserSession coordinator = room.getCoordinator(userSession);
                        if(coordinator!=null){
                            JSONObject obj = new JSONObject();
                            obj.put("cmd","coordinate");
                            obj.put("sid",userSession.getSid());
                            coordinator.sendMessage(obj.toString());
                        }else {
                            JSONObject obj = new JSONObject();
                            obj.put("cmd","operation");
                            JSONArray data = new JSONArray();
                            try {
                                data.put(new GetCollaborativeContentService(userId,groupId).execute());
                            } catch (ServiceException e) {
                                data.put("");
                            }
                            obj.put("data",data);
                            userSession.sendMessage(obj.toString());
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
                        public void invoke(final String event) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println("\nRECV: " + event);
                                    JSONObject args = new JSONObject(event);
                                    String cmd = args.has("cmd")? args.getString("cmd") : "";
                                    switch (cmd) {
                                        case "createMessage":
                                            if(args.has("data")) {
                                                String data = args.getString("data");
                                                CreateMessageService messageService = new CreateMessageService(
                                                        userId, groupId, data);
                                                try {
                                                    Message message = messageService.execute();
                                                    JSONArray messagesArray = new JSONArray();
                                                    JSONObject messageObj = message.toJsonObject();
                                                    messagesArray.put(messageObj);
                                                    JSONObject msg = new JSONObject();
                                                    msg.put("cmd", "msg");
                                                    msg.put("data", messagesArray);
                                                    room.sendMessage(msg.toString());
                                                } catch (ServiceException e) {
                                                    e.printStackTrace();
                                                }
                                                return;
                                            }
                                            // send msg

                                        break;
                                        case "getMessages":
                                            if (args.has("len") && args.has("ts")) {
                                                int len = args.getInt("len");
                                                Long ts = args.getLong("ts");
                                                userSession.sendMessages(ts, len);
                                                return;
                                            }
                                        break;
                                        case "offer":
                                            if(args.has("data")) {
                                                JSONObject data = args.getJSONObject("data");
                                                String rsd = data.getString("sdp");
                                                userSession.processOffer(rsd);
                                                return;
                                            }
                                        break;
                                        case "iceCandidate":
                                            if(args.has("candidate")){
                                                JSONObject jCand = args.getJSONObject("candidate");
                                                IceCandidate candidate = new IceCandidate(jCand.getString("candidate"),
                                                        jCand.getString("sdpMid"), jCand.getInt("sdpMLineIndex"));
                                                userSession.addCandidate(candidate);
                                                return;
                                            }
                                        break;
                                        case "setRealTime":
                                            if(args.has("uid")) {
                                                String owner = args.getString("uid");
                                                String sessionId = args.optString("sid", null);
                                                userSession.setRealTime(owner, sessionId);
                                                userSession.sendMessage(userSession.getContent());
                                                return;
                                            }

                                        break;
                                        case "removeUser":
                                            if (args.has("uid")) {
                                                String uid = args.getString("uid");
                                                RemoveGroupMemberService service = new RemoveGroupMemberService(userId, groupId, uid);
                                                try {
                                                    service.execute();
                                                } catch (ServiceException e) {
                                                    e.printStackTrace();
                                                }
                                                final JSONObject myAdvertise = new JSONObject().put("cmd", "removedUser").put("uid", uid);
                                                room.sendMessage(myAdvertise.toString());
                                                return;
                                            }

                                        break;
                                        case "addUser":
                                            if(args.has("uid")) {
                                                String uid = args.getString("uid");
                                                AddGroupMemberService service = new AddGroupMemberService(userId, groupId, uid);
                                                try {
                                                    service.execute();
                                                    Document attributes = new ListOwnerAttributesService(userId, uid, null).execute();
                                                    JSONObject result = new JSONObject(attributes.toJson());
                                                    result.put("cmd", uid);
                                                    final JSONObject myAdvertise = new JSONObject().put("cmd", "participants").put("data",
                                                            new JSONArray().put(result.put("online", false)));
                                                    room.sendMessage(myAdvertise.toString());
                                                } catch (ServiceException e) {
                                                    e.printStackTrace();
                                                }
                                                return;
                                            }
                                        break;
                                        case "play": {
                                            if(args.has("data")) {
                                                boolean play = args.getBoolean("data");
                                                userSession.setPlay(play);
                                                return;
                                            }
                                        }
                                        break;
                                        case "getContent":
                                            if(true){
                                                userSession.sendMessage(userSession.getContent());
                                            }
                                            return;
                                        case "talk":
                                            if(args.has("value")){
                                                Boolean value = args.getBoolean("value");
                                                JSONObject msg = new JSONObject();
                                                msg.put("cmd", "talk");
                                                JSONObject data = new JSONObject();
                                                data.put("uid", userId);
                                                data.put("value", value);
                                                msg.put("data", data);
                                                room.sendMessage(msg.toString());
                                                return;
                                            }
                                        break;
                                        case "setHistoric":
                                            if(args.has("uid")){
                                                String owner = args.getString("uid");
                                                String sessionId = args.optString("sid", null);
                                                if(owner==null){
                                                    owner = room.getGroupId();
                                                    sessionId = "group";
                                                }
                                                userSession.setOffset(args.getLong("offset"));
                                                userSession.setHistoric(owner,sessionId);
                                                userSession.sendMessage(userSession.getContent());
                                                return;
                                            }
                                        break;
                                        case "createTag":
                                            if(args.has("time") && args.has("title")){
                                                try {
                                                    Date time = Tools.FORMAT.parse(args.getString("time"));
                                                    String title = args.getString("title");
                                                    String tid = args.optString("id",null);
                                                    SetTimeTagService service = new SetTimeTagService(groupId,tid, time, title);
                                                    TimeAnnotation tag = service.execute();
                                                    JSONObject msg = new JSONObject();
                                                    msg.put("cmd", "tag");
                                                    msg.put("data", tag.toJson());
                                                    room.sendMessage(msg.toString());
                                                } catch (ServiceException | JSONException | ParseException e) {
                                                    e.printStackTrace();
                                                }
                                                return;
                                            }
                                        break;
                                        case "removeTag":
                                            if(args.has("tid")){
                                                String tagId = args.getString("tid");
                                                try {
                                                    new DeleteTimeTagService(groupId,tagId).execute();
                                                    JSONObject msg = new JSONObject();
                                                    msg.put("cmd", "removeTag");
                                                    msg.put("tid", tagId);
                                                    room.sendMessage(msg.toString());
                                                } catch (ServiceException e) {
                                                    e.printStackTrace();
                                                }
                                                return;
                                            }
                                        break;
                                        case "createContent":
                                            if(args.has("start") && args.has("end") && args.has("content")){
                                                try {
                                                    Date start = Tools.FORMAT.parse(args.getString("start"));
                                                    Date end = Tools.FORMAT.parse(args.getString("end"));
                                                    String content = args.getString("content");
                                                    CreateHyperContentService service = new CreateHyperContentService(userId,
                                                            groupId, start, end, content);
                                                    service.execute();
                                                    room.sendContents();
                                                } catch (ServiceException | ParseException e) {
                                                    e.printStackTrace();
                                                }
                                                return;
                                            }
                                        break;
                                        case "deleteContent":
                                            if(args.has("cid")){
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
                                                return;
                                            }
                                        case "operation" : {
                                            synchronized (room.getOperationLock()) {
                                                if (args.has("sid")) {
                                                    UserSession sess = room.getUser(args.getString("sid"));
                                                    sess.sendMessage(event);
                                                } else {
                                                    room.sendMessage(userSession, event);
                                                }
                                                return;
                                            }
                                        }
                                        case "saveCollab": if(args.has("data")){
                                            synchronized (room.getOperationLock()) {
                                                String data = args.getString("data");
                                                try {
                                                    new SetCollaborativeContentService(userId, groupId, data).execute();
                                                } catch (ServiceException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            return;
                                        }
                                        break;
                                        default:
                                            break;


                                    }
                                    System.out.println("============== INVALID CMD! ===============");

                                }
                            }).start();



                        }
                    });
                } else {
                    out.close();
                }
            }
        };
    }



}
