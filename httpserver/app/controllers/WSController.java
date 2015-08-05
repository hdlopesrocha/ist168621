package controllers;

import org.bson.Document;
import org.json.JSONObject;

import com.bullray.storage.services.SubscribeService;

import main.Global;
import main.Room;
import main.RoomManager;
import main.WebSocketSession;
import play.mvc.Controller;
import play.mvc.WebSocket;

public class WSController extends Controller {

	public WebSocket<String> connectToRoom(String name) {
		return new WebSocket<String>() {
			public void onReady(WebSocket.In<String> in,
					WebSocket.Out<String> out) {
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Room room = Global.manager.getRoom(name);
							room.join(session("uid"), new WebSocketSession(in, out));
							while (true) {
								out.write("hello");						
								Thread.sleep(5000);
							}
						} catch (Exception e) {
							System.out.println("ws close");
						} finally {
							out.close();
						}
					}
				}).start();
			}
		};
	}
}
