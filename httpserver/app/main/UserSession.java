/*
 * (C) Copyright 2014 Kurento (http://kurento.org/)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package main;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.json.JSONObject;
import org.kurento.client.ConnectionStateChangedEvent;
import org.kurento.client.Continuation;
import org.kurento.client.ErrorEvent;
import org.kurento.client.EventListener;
import org.kurento.client.IceCandidate;
import org.kurento.client.OnIceCandidateEvent;
import org.kurento.client.OnIceComponentStateChangedEvent;
import org.kurento.client.WebRtcEndpoint;
import org.kurento.jsonrpc.JsonUtils;

import com.google.gson.JsonObject;

import models.User;
import play.mvc.WebSocket;

/**
 * 
 * @author Ivan Gracia (izanmail@gmail.com)
 * @since 4.3.1
 */
public class UserSession implements Closeable {

	private final WebSocket.Out<String> out;
	private final User user;
	private final Room room;
	private final WebRtcEndpoint outEndPoint;
	private final ConcurrentMap<String, WebRtcEndpoint> inEndPoints = new ConcurrentHashMap<>();

	public UserSession(final User user, final Room room, WebSocket.Out<String> out) {
		this.out = out;
		this.user = user;
		this.room = room;

		// XXX [ICE_01] XXX
		this.outEndPoint =room.createWebRtcEndPoint();
		// this.inEndPoint.connect(outEndPoint);
		// this.outEndPoint.connect(inEndPoint);
		
		outEndPoint.setStunServerAddress("173.194.67.127");
		outEndPoint.setStunServerPort(19302);
		outEndPoint.addOnIceCandidateListener(new EventListener<OnIceCandidateEvent>() {

			@Override
			public void onEvent(OnIceCandidateEvent arg0) {
				try {
					synchronized (this) {
						System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1");
						System.out.println(JsonUtils.toJsonObject(arg0.getCandidate()));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}
		});
		
		outEndPoint.addConnectionStateChangedListener(new EventListener<ConnectionStateChangedEvent>() {

			@Override
			public void onEvent(ConnectionStateChangedEvent arg0) {
				System.out.println("ConnectionState: "+ arg0.getNewState().toString());
				
			}
		});
		
		outEndPoint.addErrorListener(new EventListener<ErrorEvent>() {
			
			@Override
			public void onEvent(ErrorEvent arg0) {
				System.out.println("ERRRRRRRRRRRRRROR");
				System.out.println(arg0.getDescription());
			}
		});
	
		
		outEndPoint.addOnIceComponentStateChangedListener(new EventListener<OnIceComponentStateChangedEvent>() {

			@Override
			public void onEvent(OnIceComponentStateChangedEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println("ICE changed !!!!");
				System.out.println(arg0.getState().toString());
			}
		});
	}

	public WebRtcEndpoint getOutgoingWebRtcPeer() {
		return outEndPoint;
	}

	/**
	 * @return the name
	 */
	public String getUserId() {
		return user.getId().toString();
	}

	public void sendMessage(String string) {
		out.write(string);
	}

	/**
	 * The room to which the user is currently attending
	 * 
	 * @return The room
	 */
	public String getGroupId() {
		return room.getGroupId();
	}



	/**
	 * @param sender
	 *            the user
	 * @return the endpoint used to receive media from a certain user
	 */
	private WebRtcEndpoint getEndpointForUser(final UserSession sender) {
		if (sender.getUserId().equals(getUserId())) {
			return outEndPoint;
		}

		WebRtcEndpoint incoming = inEndPoints.get(sender.getUserId());
		if (incoming == null) {
			incoming = room.createWebRtcEndPoint();
			incoming.connect(outEndPoint);
			outEndPoint.connect(incoming);

			
			incoming.addOnIceCandidateListener(new EventListener<OnIceCandidateEvent>() {

				@Override
				public void onEvent(OnIceCandidateEvent event) {
					JsonObject response = new JsonObject();
					response.addProperty("id", "iceCandidate");
					response.addProperty("userId", sender.getUserId());
					response.add("candidate", JsonUtils.toJsonObject(event.getCandidate()));
					try {
						synchronized (this) {
							System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1");
							System.out.println(response.toString());
							sendMessage(response.toString());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			inEndPoints.put(sender.getUserId(), incoming);
		}

		sender.getOutgoingWebRtcPeer().connect(incoming);

		return incoming;
	}

	/**
	 * @param sender
	 *            the participant
	 */
private void cancelVideoFrom(final UserSession sender) {
		this.cancelVideoFrom(sender.getUserId());
	}

	/**
	 * @param senderName
	 *            the participant
	 */
	private void cancelVideoFrom(final String senderName) {
		final WebRtcEndpoint incoming = inEndPoints.remove(senderName);

		incoming.release(EMPTY_CONTINUATION);
	}

	@Override
	public void close() throws IOException {
		for (final String remoteParticipantName : inEndPoints.keySet()) {
			final WebRtcEndpoint ep = this.inEndPoints.get(remoteParticipantName);

			ep.release(EMPTY_CONTINUATION);
		}

		outEndPoint.release();
	}



	private void addCandidate(IceCandidate e, String name) {
		if (getUserId().compareTo(name) == 0) {
			outEndPoint.addIceCandidate(e);
		} else {
			WebRtcEndpoint webRtc = inEndPoints.get(name);
			if (webRtc != null) {
				webRtc.addIceCandidate(e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof UserSession)) {
			return false;
		}
		UserSession other = (UserSession) obj;
		boolean eq = getUserId().equals(other.getUserId());
		eq &= getGroupId().equals(other.getGroupId());
		return eq;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + getUserId().hashCode();
		result = 31 * result + getGroupId().hashCode();
		return result;
	}
	
	Continuation<Void> EMPTY_CONTINUATION = new Continuation<Void>() {

		@Override
		public void onSuccess(Void result) throws Exception {
		}

		@Override
		public void onError(Throwable cause) throws Exception {
		}
	};
}
