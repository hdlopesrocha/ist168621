package controllers;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kurento.client.IceCandidate;

import main.Global;
import main.Room;
import main.UserSession;
import models.Interval;
import models.Recording;
import models.User;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.mvc.Controller;
import play.mvc.WebSocket;

public class WSController extends Controller {

	public WebSocket<String> connectToRoom(String groupId) {
		final String uid = session("uid");

		System.out.println("connectToRoom(" + groupId + ")");
		return new WebSocket<String>() {
			public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {
				Room room = Global.manager.getRoom(groupId);
				User user = User.findById(new ObjectId(uid));
				try {
					if (room != null) {
						// Join room
						final UserSession usession = room.join(user, out);

						List<Interval> intervals = Interval.listByGroup(new ObjectId(groupId));
						JSONObject msg = new JSONObject();
						msg.put("id", "rec");
						for (Interval interval : intervals) {
							Date start = interval.getStart();
							Date end = interval.getEnd();
							
							
							if (start != null && end != null) {

								JSONArray rec = new JSONArray();
								rec.put(Recording.FORMAT.format(start));
								rec.put(Recording.FORMAT.format(end));
								msg.put(interval.getId().toString(), rec);

							}
						}
						room.sendMessage(msg.toString());

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
								String userId = args.has("uid") ? args.getString("uid") : null;

								switch (id) {

								case "offer": {
									JSONObject data = args.getJSONObject("data");
									String name = args.optString("name", null);

									String rsd = data.getString("sdp");
									usession.processOffer(rsd,name);
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
								case "realtime":
									System.out.println("REALTIME");
									new Thread(new Runnable() {
										@Override
										public void run() {
											usession.setRealtime(userId);
										}
									}).start();

									break;
								case "historic":
									System.out.println("HISTORIC");
									new Thread(new Runnable() {
										@Override
										public void run() {
											usession.setHistoric(userId, args.getLong("offset"));
										}
									}).start();
									break;
								case "mix":
									System.out.println("HISTORIC");
									new Thread(new Runnable() {
										@Override
										public void run() {
											usession.setMix();
										}
									}).start();
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
