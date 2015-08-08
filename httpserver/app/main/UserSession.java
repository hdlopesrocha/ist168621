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
import org.kurento.client.MediaPipeline;
import org.kurento.client.OnIceCandidateEvent;
import org.kurento.client.WebRtcEndpoint;
import org.kurento.jsonrpc.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import play.mvc.WebSocket;

/**
 * 
 * @author Ivan Gracia (izanmail@gmail.com)
 * @since 4.3.1
 */
public class UserSession implements Closeable {

	private static final Logger log = LoggerFactory
			.getLogger(UserSession.class);

	private final String uid;

	private final MediaPipeline pipeline;

	private WebSocket.In<String> in;
	private WebSocket.Out<String> out;
	private final String groupId;
	private final WebRtcEndpoint outEndPoint,inEndPoint=null;
	private final ConcurrentMap<String, WebRtcEndpoint> incomingMedia = new ConcurrentHashMap<>();

	public UserSession(final String uid, String roomName, MediaPipeline pipeline,WebSocket.In<String> in,
			WebSocket.Out<String> out) {
		this.in = in;
		this.out = out;
		this.pipeline = pipeline;
		this.uid = uid;
		this.groupId = roomName;
	//	this.inEndPoint = new WebRtcEndpoint.Builder(pipeline).build();
		this.outEndPoint = new WebRtcEndpoint.Builder(pipeline).build();
		
	//	this.inEndPoint.connect(outEndPoint);
	//	this.outEndPoint.connect(inEndPoint);
		
		
		outEndPoint.setStunServerAddress("173.194.67.127");
		outEndPoint.setStunServerPort(19302);
		outEndPoint.addOnIceCandidateListener(new EventListener<OnIceCandidateEvent>() {

			
			
			@Override
			public void onEvent(OnIceCandidateEvent event) {
				JsonObject response = new JsonObject();
				response.addProperty("id", "iceCandidate");
				response.addProperty("name", uid);
				response.add("candidate",JsonUtils.toJsonObject(event.getCandidate()));
				try {
					synchronized (this) {
						System.out.println(response
								.toString());
						
						sendMessage(response
								.toString());
					}
				} catch (Exception e) {
					log.debug(e.getMessage());
				}
			}
		});				
		
	
		outEndPoint.generateOffer(new Continuation<String>() {

			@Override
			public void onError(Throwable arg0) throws Exception {
				// TODO Auto-generated method stub
				arg0.printStackTrace();
			}

			@Override
			public void onSuccess(String arg0) throws Exception {
				// TODO Auto-generated method stub
				System.out.println(arg0);
				outEndPoint.gatherCandidates();
				
			}
		});
		
		
		/*outgoingMedia.getRemoteSessionDescriptor(new Continuation<String>() {

			@Override
			public void onError(Throwable arg0) throws Exception {
				// TODO Auto-generated method stub
				arg0.printStackTrace();
			}

			@Override
			public void onSuccess(String arg0) throws Exception {
				System.out.println(arg0);		// TODO Auto-generated method stub
				
			}
		});
		*/

	
	}

	private static Continuation<Void> cont =new Continuation<Void>() {

		@Override
		public void onError(Throwable arg0) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSuccess(Void arg0) throws Exception {
			// TODO Auto-generated method stub
			
		}
	};

	
	public WebRtcEndpoint getOutgoingWebRtcPeer() {
		return outEndPoint;
	}

	/**
	 * @return the name
	 */
	public String getUid() {
		return uid;
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
		return this.groupId;
	}

	/**
	 * @param sender
	 * @param sdpOffer
	 * @throws IOException
	 */
	public void receiveVideoFrom(UserSession sender, String sdpOffer)
			throws IOException {
		log.info("USER {}: connecting with {} in room {}", this.uid,
				sender.getUid(), this.groupId);

		log.trace("USER {}: SdpOffer for {} is {}", this.uid,
				sender.getUid(), sdpOffer);

		final String ipSdpAnswer = this.getEndpointForUser(sender)
				.processOffer(sdpOffer);
		final JsonObject scParams = new JsonObject();
		scParams.addProperty("id", "receiveVideoAnswer");
		scParams.addProperty("name", sender.getUid());
		scParams.addProperty("sdpAnswer", ipSdpAnswer);

		log.trace("USER {}: SdpAnswer for {} is {}", this.uid,
				sender.getUid(), ipSdpAnswer);
		this.sendMessage(scParams);
		log.debug("gather candidates");
		this.getEndpointForUser(sender).gatherCandidates();
	}

	/**
	 * @param sender
	 *            the user
	 * @return the endpoint used to receive media from a certain user
	 */
	private WebRtcEndpoint getEndpointForUser(final UserSession sender) {
		if (sender.getUid().equals(uid)) {
			log.debug("PARTICIPANT {}: configuring loopback", this.uid);
			return outEndPoint;
		}

		log.debug("PARTICIPANT {}: receiving video from {}", this.uid,
				sender.getUid());

		WebRtcEndpoint incoming = incomingMedia.get(sender.getUid());
		if (incoming == null) {
			log.debug("PARTICIPANT {}: creating new endpoint for {}",
					this.uid, sender.getUid());
			incoming = new WebRtcEndpoint.Builder(pipeline).build();

			incoming.addOnIceCandidateListener(new EventListener<OnIceCandidateEvent>() {

				@Override
				public void onEvent(OnIceCandidateEvent event) {
					JsonObject response = new JsonObject();
					response.addProperty("id", "iceCandidate");
					response.addProperty("name", sender.getUid());
					response.add("candidate",
							JsonUtils.toJsonObject(event.getCandidate()));
					try {
						synchronized (this) {
							sendMessage(response
									.toString());
						}
					} catch (Exception e) {
						log.debug(e.getMessage());
					}
				}
			});

			incomingMedia.put(sender.getUid(), incoming);
		}

		log.debug("PARTICIPANT {}: obtained endpoint for {}", this.uid,
				sender.getUid());
		sender.getOutgoingWebRtcPeer().connect(incoming);

		return incoming;
	}

	/**
	 * @param sender
	 *            the participant
	 */
	public void cancelVideoFrom(final UserSession sender) {
		this.cancelVideoFrom(sender.getUid());
	}

	/**
	 * @param senderName
	 *            the participant
	 */
	public void cancelVideoFrom(final String senderName) {
		log.debug("PARTICIPANT {}: canceling video reception from {}",
				this.uid, senderName);
		final WebRtcEndpoint incoming = incomingMedia.remove(senderName);

		log.debug("PARTICIPANT {}: removing endpoint for {}", this.uid,
				senderName);
		incoming.release(new Continuation<Void>() {
			@Override
			public void onSuccess(Void result) throws Exception {
				log.trace(
						"PARTICIPANT {}: Released successfully incoming EP for {}",
						UserSession.this.uid, senderName);
			}

			@Override
			public void onError(Throwable cause) throws Exception {
				log.warn(
						"PARTICIPANT {}: Could not release incoming EP for {}",
						UserSession.this.uid, senderName);
			}
		});
	}

	@Override
	public void close() throws IOException {
		log.debug("PARTICIPANT {}: Releasing resources", this.uid);
		for (final String remoteParticipantName : incomingMedia.keySet()) {

			log.trace("PARTICIPANT {}: Released incoming EP for {}", this.uid,
					remoteParticipantName);

			final WebRtcEndpoint ep = this.incomingMedia
					.get(remoteParticipantName);

			ep.release(new Continuation<Void>() {

				@Override
				public void onSuccess(Void result) throws Exception {
					log.trace(
							"PARTICIPANT {}: Released successfully incoming EP for {}",
							UserSession.this.uid, remoteParticipantName);
				}

				@Override
				public void onError(Throwable cause) throws Exception {
					log.warn(
							"PARTICIPANT {}: Could not release incoming EP for {}",
							UserSession.this.uid, remoteParticipantName);
				}
			});
		}

		outEndPoint.release(new Continuation<Void>() {

			@Override
			public void onSuccess(Void result) throws Exception {
				log.trace("PARTICIPANT {}: Released outgoing EP",
						UserSession.this.uid);
			}

			@Override
			public void onError(Throwable cause) throws Exception {
				log.warn("USER {}: Could not release outgoing EP",
						UserSession.this.uid);
			}
		});
	}

	public void sendMessage(JsonObject message) throws IOException {
		log.debug("USER {}: Sending message {}", uid, message);
		synchronized (this) {
			sendMessage(message.toString());
		}
	}

	public void addCandidate(IceCandidate e, String name) {
		if (this.uid.compareTo(name) == 0) {
			outEndPoint.addIceCandidate(e);
		} else {
			WebRtcEndpoint webRtc = incomingMedia.get(name);
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
		boolean eq = uid.equals(other.uid);
		eq &= groupId.equals(other.groupId);
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
		result = 31 * result + uid.hashCode();
		result = 31 * result + groupId.hashCode();
		return result;
	}
}
