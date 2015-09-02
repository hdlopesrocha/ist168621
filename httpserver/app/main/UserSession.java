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

import org.json.JSONObject;
import org.kurento.client.ConnectionState;
import org.kurento.client.ConnectionStateChangedEvent;
import org.kurento.client.Continuation;
import org.kurento.client.EventListener;
import org.kurento.client.Hub;
import org.kurento.client.HubPort;
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
	private final WebRtcEndpoint endPoint;

	private boolean realTime = true;
	private RecorderEndpoint recorder;	
	
	private HubPort audioMixerPort;
	private long playOffset = 0l;
	private String playUser = "";
	private Long sequence = 0l;
	private boolean recording = true;

	public UserSession(final User user, final Room room, WebSocket.Out<String> out) {
		this.out = out;
		this.user = user;
		this.room = room;

		// XXX [ICE_01] XXX
		endPoint = createWebRtcEndPoint();
		endPoint.connect(endPoint);
		
		audioMixerPort = new HubPort.Builder(room.getAudioMixer()).build();
	//	audioMixerPort.connect(endPoint);
	//	endPoint.connect(audioMixerPort);
		
		
		
		this.endPoint.addConnectionStateChangedListener(new EventListener<ConnectionStateChangedEvent>() {

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
				Date begin = new Date();

				while (recording) {

					String filename = interval.getId().toString() + "-" + sequence + ".webm";
					String filepath = "file:///rec/" + filename;
					recorder = recordEndpoint(endPoint, filepath);

					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					recorder.stop();

					try {
						Date end = new Date();

						SaveRecordingService srs = new SaveRecordingService(null, filepath, getGroupId(),
								getUser().getId().toString(), begin, end, filename, "video/webm",
								interval.getId().toString());
						Recording rec = srs.execute();
						PublishService publishService = new PublishService("rec:" + getGroupId());
						publishService.execute();

						System.out.println("STOP: " + filepath);
						// continuous parts (although not true)
						begin = end;
					} catch (ServiceException e) {
						e.printStackTrace();
					}

					// outgoing.disconnect(recEndPoint);
					// recEndPoint.release();
					++sequence;
				}
			}
		}).start();

	}

	
	
	public RecorderEndpoint recordEndpoint(WebRtcEndpoint endPoint, String filepath){

		System.out.println("REC: "+ filepath);
		RecorderEndpoint recorder = new RecorderEndpoint.Builder(room.getMediaPipeline(),filepath).build();
		
		recorder.record();
		endPoint.connect(recorder);
		return recorder;
	}
	
	public WebRtcEndpoint createWebRtcEndPoint() {
		WebRtcEndpoint ep = new WebRtcEndpoint.Builder(room.getMediaPipeline()).build();

		ep.addOnIceCandidateListener(new EventListener<OnIceCandidateEvent>() {

			@Override
			public void onEvent(OnIceCandidateEvent event) {
				JsonObject response = new JsonObject();
				response.addProperty("id", "iceCandidate");

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
		endPoint.release();
	}

	@Override
	public void close() throws IOException {


		endPoint.release();
	}

	public void processOffer(String description) {
		// XXX [CLIENT_OFFER_04] XXX
		// XXX [CLIENT_OFFER_05] XXX


		String arg0 = endPoint.processOffer(description);
		// XXX [CLIENT_OFFER_06] XXX
		JSONObject msg = new JSONObject().put("id",  "description" ).put("sdp", arg0)
				.put("type", "answer");
		// XXX [CLIENT_OFFER_07] XXX
		sendMessage(msg.toString());
		endPoint.gatherCandidates();
	}

	public void addCandidate(IceCandidate candidate) {
		// XXX [CLIENT_ICE_04] XXX
		endPoint.addIceCandidate(candidate);
		
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

	public void processAnswer(String answer) {

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

	public void setHistoric(String userId, long offset) {
		playOffset = offset;
		playUser = userId;

		System.out.println("SET HIST "+ userId);
		if (realTime) {
			realTime = false;

			while (!realTime) {
				Date currentTime = new Date(new Date().getTime() - playOffset);
				UserSession session = room.getParticipant(playUser);
				try {
					GetCurrentRecordingService service = new GetCurrentRecordingService(user.getId().toString(),
							room.getGroupId(), session.getUser().getId().toString(), currentTime);
					Recording rec = service.execute();

					if (rec != null) {
						System.out.println("PLAY:" + rec.getUrl());

						PlayerEndpoint player = new PlayerEndpoint.Builder(room.getMediaPipeline(), rec.getUrl())
								.build();

						player.connect(endPoint);
						player.play();
					} else {
						System.out.println("No video here!");
					}

				} catch (ServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void setRealtime(String userId) {
		playUser = userId;
		realTime = true;
		UserSession session = room.getParticipant(playUser);
		session.endPoint.connect(endPoint);
	}

}
