package controllers;

import java.io.IOException;

import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.kurento.client.Continuation;
import org.kurento.client.IceCandidate;
import org.kurento.client.MediaPipeline;

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
						final UserSession usession = room.join(user,out);

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
								switch (id) {
								
								
								case "description":
									// XXX [CLIENT_OFFER_04] XXX
									// XXX [CLIENT_OFFER_05] XXX
									
									String description = args.getJSONObject("data").getString("sdp").toString();
									System.out.println(description);
									
									usession.getOutgoingWebRtcPeer().gatherCandidates(new Continuation<Void>() {

										@Override
										public void onError(Throwable arg0) throws Exception {
											// TODO Auto-generated method stub
											
										}

										@Override
										public void onSuccess(Void arg0) throws Exception {
											// TODO Auto-generated method stub
											
										}
									});

									
									usession.getOutgoingWebRtcPeer().processOffer(description,new Continuation<String>() {
										
										@Override
										public void onSuccess(String arg0) throws Exception {											
											// XXX [CLIENT_OFFER_06] XXX
											JSONObject msg = new JSONObject().put("id", "description").put("sdp", arg0).put("type", "answer");
											// XXX [CLIENT_OFFER_07] XXX
											usession.sendMessage(msg.toString());
										}
										
										@Override
										public void onError(Throwable arg0) throws Exception {
											// TODO Auto-generated method stub
											arg0.printStackTrace();
										}
									});
								
									
									
									
									break;
								case "iceCandidate":
									// XXX [CLIENT_ICE_04] XXX		
									JSONObject jCand = args.getJSONObject("candidate");
									IceCandidate candidate = new IceCandidate(jCand.getString("candidate"), jCand.getString("sdpMid"), jCand.getInt("sdpMLineIndex"));
									
									
									usession.getOutgoingWebRtcPeer().addIceCandidate(candidate,new Continuation<Void>() {

										@Override
										public void onError(Throwable arg0) throws Exception {
											arg0.printStackTrace();
										}

										@Override
										public void onSuccess(Void arg0) throws Exception {
											System.out.println("Add Client ICE Candidate");
										}
									});
					
									
									
									
									
									
									
									break;

								case "answer": 
									System.out.println("----------");
									usession.getOutgoingWebRtcPeer().processAnswer(args.getString("answer"), new Continuation<String>() {
										@Override
										public void onSuccess(String arg0) throws Exception {
											// TODO Auto-generated method stub
											System.out.println("================");

											System.out.println(arg0);
										}
										
										@Override
										public void onError(Throwable arg0) throws Exception {
											// TODO Auto-generated method stub
											arg0.printStackTrace();
										}
									});
									usession.getOutgoingWebRtcPeer().getRemoteSessionDescriptor(new Continuation<String>() {
										
										@Override
										public void onSuccess(String arg0) throws Exception {
											// TODO Auto-generated method stub
											System.out.println("%%%%%%%%%%%%"+arg0);
											
										}
										
										@Override
										public void onError(Throwable arg0) throws Exception {
											// TODO Auto-generated method stub
											
										}
									}
									);
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
