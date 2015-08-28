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
import org.kurento.client.ConnectionState;
import org.kurento.client.ConnectionStateChangedEvent;
import org.kurento.client.Continuation;
import org.kurento.client.EventListener;
import org.kurento.client.IceCandidate;
import org.kurento.client.OnIceCandidateEvent;
import org.kurento.client.RecorderEndpoint;
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
	private RecorderEndpoint recEndPoint;
	
	private Long sequence=0l;
	private boolean recording = true;
	private final ConcurrentMap<String, WebRtcEndpoint> inEndPoints = new ConcurrentHashMap<>();

	public UserSession(final User user, final Room room, WebSocket.Out<String> out) {
		this.out = out;
		this.user = user;
		this.room = room;

		// XXX [ICE_01] XXX
		this.outEndPoint =room.createWebRtcEndPoint(this,null);
		
		this.outEndPoint.addConnectionStateChangedListener(new EventListener<ConnectionStateChangedEvent>() {

			@Override
			public void onEvent(ConnectionStateChangedEvent arg0) {
				// TODO Auto-generated method stub
				if(arg0.getOldState().equals(ConnectionState.CONNECTED) &&
						arg0.getNewState().equals(ConnectionState.DISCONNECTED)){
					recording = false;
				}
			}
		});
		/*
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				UserSession session = UserSession.this;
				while(recording){
					// TODO Auto-generated method stub
					if(session.recEndPoint!=null){
						session.recEndPoint.stop();
						session.recEndPoint.disconnect(outEndPoint);
						session.recEndPoint.release();
					}
					
					session.recEndPoint = room.recordEndpoint(outEndPoint, session,sequence);
					++sequence;
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
		*/
	
		
		
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


	/**
	 * @param sender
	 *            the user
	 * @return the endpoint used to receive media from a certain user
	 */
	public WebRtcEndpoint getEndpoint(final UserSession sender) {
		if (sender == null) {
			return outEndPoint;
		}
		String senderId = sender.getUser().getId().toString();
		WebRtcEndpoint incoming = inEndPoints.get(senderId);
		if (incoming == null) {
			incoming = room.createWebRtcEndPoint(this,senderId);
			incoming.connect(outEndPoint);
			outEndPoint.connect(incoming);

			
			

			inEndPoints.put(senderId, incoming);
		}

		sender.getEndpoint(null).connect(incoming);

		return incoming;
	}

	/**
	 * @param senderName
	 *            the participant
	 */
	public void cancelVideoFrom(final UserSession sender) {
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

	

	
	public void processOffer(String description, String userId) {
		// XXX [CLIENT_OFFER_04] XXX
		// XXX [CLIENT_OFFER_05] XXX

		WebRtcEndpoint endPoint = null;
		if(userId!=null){
			UserSession otherSession = room.getParticipant(userId);
			endPoint = getEndpoint(otherSession);
		}
		else{
			endPoint = getEndpoint(null);
		}
		
		String arg0 = endPoint.processOffer(description);
		// XXX [CLIENT_OFFER_06] XXX
		JSONObject msg = new JSONObject().put("id", userId==null? "description":"description2").put("sdp", arg0)
				.put("type", "answer").put("uid", userId);
		// XXX [CLIENT_OFFER_07] XXX
		sendMessage(msg.toString());
		endPoint.gatherCandidates();		
	}
	

	public void addCandidate(IceCandidate candidate, String userId) {
		// XXX [CLIENT_ICE_04] XXX
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



	public void processAnswer(String answer, String userId) {
		WebRtcEndpoint endPoint = getEndpoint(null);

		
		endPoint.processAnswer(answer,
				new Continuation<String>() {
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
		endPoint.getRemoteSessionDescriptor(new Continuation<String>() {

			@Override
			public void onSuccess(String arg0) throws Exception {
				// TODO Auto-generated method stub
				System.out.println("%%%%%%%%%%%%" + arg0);

			}

			@Override
			public void onError(Throwable arg0) throws Exception {
				// TODO Auto-generated method stub

			}
		});		
	}




}
