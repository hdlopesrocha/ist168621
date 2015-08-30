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
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.json.JSONObject;
import org.kurento.client.ConnectionState;
import org.kurento.client.ConnectionStateChangedEvent;
import org.kurento.client.Continuation;
import org.kurento.client.EndOfStreamEvent;
import org.kurento.client.EventListener;
import org.kurento.client.IceCandidate;
import org.kurento.client.OnIceCandidateEvent;
import org.kurento.client.PlayerEndpoint;
import org.kurento.client.RecorderEndpoint;
import org.kurento.client.WebRtcEndpoint;
import org.kurento.jsonrpc.JsonUtils;

import com.google.gson.JsonObject;

import exceptions.ServiceException;
import models.Interval;
import models.Recording;
import models.User;
import play.mvc.WebSocket;
import services.GetCurrentRecordingService;
import services.PublishService;
import services.SaveRecordingService;

/**
 * 
 * @author Ivan Gracia (izanmail@gmail.com)
 * @since 4.3.1
 */
public class UserSession implements Closeable, Comparable<UserSession> {

	private final WebSocket.Out<String> out;
	private final User user;
	private final Room room;
	private boolean realTime = true;
	private WebRtcEndpoint outgoing;
	private RecorderEndpoint recEndPoint;

	private Long sequence = 0l;
	private boolean recording = true;
	private final ConcurrentMap<String, WebRtcEndpoint> incomings = new ConcurrentHashMap<String, WebRtcEndpoint>();

	public UserSession(final User user, final Room room, WebSocket.Out<String> out) {
		this.out = out;
		this.user = user;
		this.room = room;

		// XXX [ICE_01] XXX
		this.outgoing = getEndpoint(null);

		this.outgoing.addConnectionStateChangedListener(new EventListener<ConnectionStateChangedEvent>() {

			@Override
			public void onEvent(ConnectionStateChangedEvent arg0) {
				// TODO Auto-generated method stub
				if (arg0.getOldState().equals(ConnectionState.CONNECTED)
						&& arg0.getNewState().equals(ConnectionState.DISCONNECTED)) {
					recording = false;
				}
			}
		});

		final Interval interval = new Interval();
		interval.save();

		new Thread(new Runnable() {

			@Override
			public void run() {
				UserSession session = UserSession.this;
				while (recording) {

					String filename = interval.getId().toString() + "-" + sequence + ".webm";
					String filepath = "file:///rec/" + filename;
					session.recEndPoint = room.recordEndpoint(outgoing, session, filepath);

					Date begin = new Date();

					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					session.recEndPoint.stop();

					try {
						Date end = new Date();

						SaveRecordingService srs = new SaveRecordingService(null, filepath, getGroupId(),
								session.getUser().getId().toString(), begin, end, filename, "video/webm",
								interval.getId().toString());
						Recording rec = srs.execute();
						PublishService publishService = new PublishService("rec:" + getGroupId());
						publishService.execute();

						System.out.println("STOP: " + filepath);
					} catch (ServiceException e) {
						e.printStackTrace();
					}

					outgoing.disconnect(session.recEndPoint);

					++sequence;
				}
			}
		}).start();

	}

	public WebRtcEndpoint createWebRtcEndPoint(String senderId) {
		WebRtcEndpoint ep = new WebRtcEndpoint.Builder(room.getMediaPipeline()).build();

		ep.addOnIceCandidateListener(new EventListener<OnIceCandidateEvent>() {

			@Override
			public void onEvent(OnIceCandidateEvent event) {
				JsonObject response = new JsonObject();
				response.addProperty("id", "iceCandidate");
				if (senderId != null) {
					response.addProperty("uid", senderId);
				}
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

		ep.setStunServerAddress("173.194.67.127");
		ep.setStunServerPort(19302);

		return ep;
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
	 * @param senderName
	 *            the participant
	 */
	public void cancelVideoFrom(final UserSession sender) {
		final WebRtcEndpoint incoming = incomings.remove(sender.getUser().getId().toString());
		incoming.release(EMPTY_CONTINUATION);
	}

	@Override
	public void close() throws IOException {
		for (final String remoteParticipantName : incomings.keySet()) {
			final WebRtcEndpoint ep = this.incomings.get(remoteParticipantName);

			ep.release(EMPTY_CONTINUATION);
		}

		outgoing.release();
	}

	/**
	 * @param sender
	 *            the user
	 * @return the endpoint used to receive media from a certain user
	 */
	private WebRtcEndpoint getEndpoint(final UserSession sender) {
		if (sender == null) {
			if (outgoing == null) {
				outgoing = createWebRtcEndPoint(null);
			}
			return outgoing;
		} else {

			String senderId = sender.getUser().getId().toString();
			WebRtcEndpoint incoming = incomings.get(senderId);
			if (incoming == null) {
				incoming = createWebRtcEndPoint(senderId);
				incomings.put(senderId, incoming);
			}
			return incoming;
		}
	}

	public void processOffer(String description, String userId) {
		// XXX [CLIENT_OFFER_04] XXX
		// XXX [CLIENT_OFFER_05] XXX

		WebRtcEndpoint incoming = null;
		if (userId != null) {
			UserSession sender = room.getParticipant(userId);
			incoming = getEndpoint(sender);
			incoming.connect(outgoing);
			// outgoing.connect(incoming);
			sender.outgoing.connect(incoming);
		} else {
			incoming = outgoing;
		}

		String arg0 = incoming.processOffer(description);
		// XXX [CLIENT_OFFER_06] XXX
		JSONObject msg = new JSONObject().put("id", userId == null ? "description" : "description2").put("sdp", arg0)
				.put("type", "answer").put("uid", userId);
		// XXX [CLIENT_OFFER_07] XXX
		sendMessage(msg.toString());
		incoming.gatherCandidates();
	}

	public void addCandidate(IceCandidate candidate, String userId) {
		// XXX [CLIENT_ICE_04] XXX
		if (userId == null) {
			outgoing.addIceCandidate(candidate);
		} else {
			WebRtcEndpoint webRtc = incomings.get(userId);
			if (webRtc != null) {
				webRtc.addIceCandidate(candidate);
			}
		}
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

		endPoint.processAnswer(answer, new Continuation<String>() {
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

	@Override
	public int compareTo(UserSession o) {
		return getUser().compareTo(o.getUser());
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

	public void setHistoric(long offset) {
		realTime = false;

		Date currentTime = new Date(new Date().getTime() - offset);

		for (UserSession session : room.getParticipants()) {
			try {
				GetCurrentRecordingService service = new GetCurrentRecordingService(user.getId().toString(),
						room.getGroupId(), session.getUser().getId().toString(), currentTime);
				Recording rec = service.execute();

				WebRtcEndpoint ep = getEndpoint(session);
				if (ep != null) {
					System.out.println("SET PLAYER TO " + session.getUser().getEmail());
					PlayerEndpoint player = new PlayerEndpoint.Builder(room.getMediaPipeline(), rec.getUrl()).build();
					// ep.connect(player);
					player.connect(ep);
					player.addEndOfStreamListener(new EventListener<EndOfStreamEvent>() {

						@Override
						public void onEvent(EndOfStreamEvent arg0) {
							// TODO Auto-generated method stub
							System.out.println("END OF STREAM");
							if (!realTime) {
								setHistoric(offset);
							}
						}
					});
					player.play();
				}

			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void setRealtime() {
		realTime = true;
		System.out.println("0 - setRealtime");

		for (UserSession session : room.getParticipants()) {
			System.out.println("1 - participant");
			WebRtcEndpoint ep = getEndpoint(session);
			if (ep != null) {
				System.out.println("2 - connect");
				session.getEndpoint(null).connect(ep);
			}
		}
	}

}
