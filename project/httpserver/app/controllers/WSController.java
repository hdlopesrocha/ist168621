package controllers;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kurento.client.IceCandidate;

import exceptions.ServiceException;
import main.Global;
import main.Room;
import main.Tools;
import main.UserSession;
import models.Interval;
import models.Message;
import models.TimeTag;
import models.User;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.mvc.Controller;
import play.mvc.WebSocket;
import services.CreateHyperContentService;
import services.CreateMessageService;
import services.CreateTagService;
import services.ListTagsService;

public class WSController extends Controller {

	public WebSocket<String> connectToRoom(String groupId) {
		final String userId = session("uid");
		return new WebSocket<String>() {
			public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {
				Room room = Global.manager.getRoom(groupId);
				User user = User.findById(new ObjectId(userId));
				try {
					if (room != null) {
						// Join room
						final UserSession usession = room.join(user, out);
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
						usession.sendMessages(null, 1);

						try {
							ListTagsService service = new ListTagsService(userId, groupId);
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

						usession.sendMessage(usession.getContent());
						

						// When the socket is closed.
						in.onClose(new Callback0() {
							public void invoke() {
								try {
									room.leave(usession);
								} catch (IOException e) {
									// TODO Auto-generated catch block
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

									CreateMessageService messageService = new CreateMessageService(groupId,
											user.getId().toString(), data, null);
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
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									// send msg
								}
									break;
								case "getMessages": {
									int len = args.getInt("len");
									Long end = args.getLong("end");
									usession.sendMessages(end, len);
								}
									break;
								case "offer": {
									JSONObject data = args.getJSONObject("data");
									String name = args.optString("name", null);

									String rsd = data.getString("sdp");
									usession.processOffer(rsd, name);
								}
									break;
								case "iceCandidate": {
									JSONObject jCand = args.getJSONObject("candidate");
									String name = args.optString("name", null);

									IceCandidate candidate = new IceCandidate(jCand.getString("candidate"),
											jCand.getString("sdpMid"), jCand.getInt("sdpMLineIndex"));
									usession.addCandidate(candidate, name);
								}
									break;
								case "setRealtime": {
									System.out.println("REALTIME");
									String userId = args.has("uid") ? args.getString("uid") : null;

									new Thread(new Runnable() {
										@Override
										public void run() {
											usession.setRealtime(userId);
											usession.sendMessage(usession.getContent());
										}
									}).start();
								}
									break;
								case "play": {
									boolean play = args.getBoolean("data");
									usession.setPlay(play);
								}
									break;
								case "getContent": {
									usession.sendMessage(usession.getContent());
								}
									break;

								case "setHistoric": {
									System.out.println("HISTORIC");
									String userId = args.has("uid") ? args.getString("uid") : null;

									new Thread(new Runnable() {
										@Override
										public void run() {
											usession.setHistoric(userId, args.getLong("offset"));
											usession.sendMessage(usession.getContent());
										}
									}).start();
								}
									break;
								case "createTag": {
									try {
										Date time = Tools.FORMAT.parse(args.getString("time"));
										String title = args.getString("title");
										String content = args.getString("content");
										CreateTagService service = new CreateTagService(groupId, time, title, content);
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
								default:
									break;
								}
							}
						});
					} else {
						out.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
					out.close();
				}
			}
		};
	}
}
