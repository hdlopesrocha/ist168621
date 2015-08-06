package controllers;

import main.Global;
import main.Room;
import main.WebSocketSession;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.mvc.Controller;
import play.mvc.Http.Session;
import play.mvc.WebSocket;

public class WSController extends Controller {

	public WebSocket<String> connectToRoom(String name) {
		final String uid = session("uid");
		
		System.out.println("connectToRoom(" + name + ")");
		return new WebSocket<String>() {
			public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {
				System.out.println("\tonReady(in,out)");

				// When the socket is closed.
				in.onClose(new Callback0() {
					public void invoke() {
						System.out.println("Disconnected");
					}
				});
				
			

				// For each event received on the socket,
				in.onMessage(new Callback<String>() {
					public void invoke(String event) {

						// Log events to the console
						System.out.println("onMessage: " + event);

					}
				});

				try {
					System.out.println("\tgetRoom(name)");
					Room room = Global.manager.getRoom(name);
					System.out.println("\troom.join()");
					room.join(uid, new WebSocketSession(in, out, uid));

					while (true) {
						System.out.println("\thello()");

						out.write("hello");
						Thread.sleep(5000);
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("ws close");
				} finally {
					out.close();
				}
			}
		};
	}
}
