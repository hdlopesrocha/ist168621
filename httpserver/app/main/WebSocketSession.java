package main;

import play.mvc.WebSocket;

public class WebSocketSession {

	
	private WebSocket.In<String> in;
	private WebSocket.Out<String> out;
	
	public WebSocketSession(WebSocket.In<String> in,
					WebSocket.Out<String> out){
		this.in = in;
		this.out = out;
	}
	
	public void sendMessage(String string) {
		out.write(string);
	}

}
