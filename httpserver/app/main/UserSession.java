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

import org.kurento.client.Continuation;
import org.kurento.client.EventListener;
import org.kurento.client.IceCandidate;
import org.kurento.client.OnIceCandidateEvent;
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
		this.outEndPoint.addOnIceCandidateListener(new EventListener<OnIceCandidateEvent>() {
			@Override
			public void onEvent(OnIceCandidateEvent arg0) {
				try {
					synchronized (this) {
						System.out.println("onIceCandidate: "+JsonUtils.toJsonObject(arg0.getCandidate()));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}
		});
	}

	

	public void sendMessage(final String string) {
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

	public WebRtcEndpoint getEndpoint() {
		return outEndPoint;
	}

	/**
	 * @param sender
	 *            the user
	 * @return the endpoint used to receive media from a certain user
	 */
	public WebRtcEndpoint getEndpoint(final UserSession sender) {
		//if (sender.equals(this)) {
		//	return outEndPoint;
		//}
		String senderId = sender.getUser().getId().toString();
		WebRtcEndpoint incoming = inEndPoints.get(senderId);
		if (incoming == null) {
			incoming = room.createWebRtcEndPoint();
			incoming.connect(outEndPoint);
			outEndPoint.connect(incoming);

			
			incoming.addOnIceCandidateListener(new EventListener<OnIceCandidateEvent>() {

				@Override
				public void onEvent(OnIceCandidateEvent event) {
					JsonObject response = new JsonObject();
					response.addProperty("id", "iceCandidate");
					response.addProperty("userId", senderId);
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

			inEndPoints.put(senderId, incoming);
		}

		sender.getEndpoint().connect(incoming);

		return incoming;
	}

	/**
	 * @param senderName
	 *            the participant
	 */
	private void cancelVideoFrom(final UserSession sender) {
		final WebRtcEndpoint incoming = inEndPoints.remove(sender.getUser().getId().toString());
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



	public void addCandidate(IceCandidate candidate, String userId) {
		if (userId==null) {
			outEndPoint.addIceCandidate(candidate);
		} else {
			WebRtcEndpoint webRtc = inEndPoints.get(userId);
			if (webRtc != null) {
				webRtc.addIceCandidate(candidate);
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
		boolean eq = user.equals(other.getUser());
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
		result = 31 * result + user.getId().hashCode();
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

	public User getUser() {
		return user;
	}
}
