package controllers;

import java.io.IOException;

import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.kurento.client.IceCandidate;

import main.Global;
import main.Room;
import main.UserSession;
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
								System.out.println("onMessage: " + event);
								JSONObject args = new JSONObject(event);
								String id = args.getString("id");
								String userId = args.has("uid") ? args.getString("uid") : null;

								switch (id) {

							
								case "offer":
								{	
									JSONObject data = args.getJSONObject("data");
									String description = data.getString("sdp");
									usession.processOffer(description,userId);
								}
								break;
								case "iceCandidate":
								{
									JSONObject jCand = args.getJSONObject("candidate");
									IceCandidate candidate = new IceCandidate(jCand.getString("candidate"),
											jCand.getString("sdpMid"), jCand.getInt("sdpMLineIndex"));
									usession.addCandidate(candidate, userId);
								}
								break;
								case "answer":
									System.out.println("----------");
									String answer=args.getString("answer");
									usession.processAnswer(answer, userId);
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
