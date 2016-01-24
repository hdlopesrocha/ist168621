package controllers;

import exceptions.ServiceException;
import main.Global;
import main.Room;
import main.Tools;
import main.UserSession;
import models.*;
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

public class WSController extends Controller {

    public WebSocket<String> connectToRoom(String groupId) {
        final String userId = session("uid");
        return new WebSocket<String>() {
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
                                System.out.println("\nRECV: " + event);
                                JSONObject args = new JSONObject(event);
                                String id = args.getString("id");

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
                                        String name = args.optString("name", null);

                                        String rsd = data.getString("sdp");
                                        userSession.processOffer(rsd, name);
                                    }
                                    break;
                                    case "iceCandidate": {
                                        JSONObject jCand = args.getJSONObject("candidate");
                                        String name = args.optString("name", null);

                                        IceCandidate candidate = new IceCandidate(jCand.getString("candidate"),
                                                jCand.getString("sdpMid"), jCand.getInt("sdpMLineIndex"));
                                        userSession.addCandidate(candidate, name);
                                    }
                                    break;
                                    case "setRealtime": {
                                        String userId = args.optString("uid", null);

                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                userSession.setRealtime(userId);
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

                                        final JSONObject myAdvertise = new JSONObject().put("id", "removedUser").put("uid",uid);

                                        room.sendMessage(myAdvertise.toString());
                                    }


                                    break;



                                    case "addUser": {
                                        String uid = args.optString("uid", null);
                                        AddGroupMemberService service = new AddGroupMemberService(userId, groupId, uid);
                                        try {
                                            service.execute();

                                            Document attributes = new ListOwnerAttributesService(userId,uid,null).execute();
                                            JSONObject result = new JSONObject(attributes.toJson());
                                            result.put("id",uid);


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
                                                userSession.setHistoric(userId, args.getLong("offset"));
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
                                        for (UserSession us : room.getParticipants()) {
                                            us.sendMessage(us.getContent());
                                        }

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
                                        for (UserSession us : room.getParticipants()) {
                                            us.sendMessage(us.getContent());
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
}
