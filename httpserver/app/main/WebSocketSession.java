package main;

import play.mvc.WebSocket;

public class WebSocketSession {

	
	private WebSocket.In<String> in;
	private WebSocket.Out<String> out;
	private String uid;
	
	public WebSocketSession(WebSocket.In<String> in,
					WebSocket.Out<String> out, String uid){
		this.in = in;
		this.out = out;
		this.uid = uid;
	}
	
	public String getUid() {
		return uid;
	}

	public void sendMessage(String string) {
		out.write(string);
	}

}
