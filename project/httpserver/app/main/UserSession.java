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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kurento.client.ConnectionState;
import org.kurento.client.ConnectionStateChangedEvent;
import org.kurento.client.EndOfStreamEvent;
import org.kurento.client.ErrorEvent;
import org.kurento.client.EventListener;
import org.kurento.client.HubPort;
import org.kurento.client.IceCandidate;
import org.kurento.client.MediaObject;
import org.kurento.client.MediaSessionStartedEvent;
import org.kurento.client.OnIceCandidateEvent;
import org.kurento.client.PlayerEndpoint;
import org.kurento.client.WebRtcEndpoint;
import org.kurento.jsonrpc.JsonUtils;
import org.kurento.repository.service.pojo.RepositoryItemPlayer;

import exceptions.ServiceException;
import models.Interval;
import models.Message;
import models.Recording;
import models.User;
import play.mvc.WebSocket;
import services.CreateRecordingService;
import services.GetCurrentRecordingService;
import services.ListMessagesService;

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
	private final WebRtcEndpoint compositePoint;
	private final Map<String, WebRtcEndpoint> endPoints = new TreeMap<String, WebRtcEndpoint>();
	private final MyRecorder recorder;
	private HubPort compositePort;
	private String intervalId = null;
	private PlayerEndpoint player;
	private Object playerLock = new Object();
	private boolean play = true;
	private long timeOffset = 0l;
	
	public long getTimeOffset() {
		return timeOffset;
	}

	private String playUser = "";

	public UserSession(final User user, final Room room, WebSocket.Out<String> out) {
		this.out = out;
		this.user = user;
		this.room = room;

		// XXX [ICE_01] XXX
		endPoint = getWebRtcEndPoint("main");
		compositePoint = getWebRtcEndPoint("mixer");

		compositePort = getCompositePort();

		recorder = new MyRecorder(endPoint, new MyRecorder.RecorderHandler() {
			@Override
			public void onFileRecorded(Date begin, Date end, String filepath, String filename) {

				try {
					CreateRecordingService srs = new CreateRecordingService(null, filepath, getGroupId(),
							getUser().getId().toString(), begin, end, filename, "video/webm", intervalId);
					srs.execute();

					Interval interval = srs.getInterval();
					intervalId = interval.getId().toString();

					JSONArray array = new JSONArray();
					array.put(Tools.FORMAT.format(interval.getStart()));
					array.put(Tools.FORMAT.format(interval.getEnd()));

					JSONObject msg = new JSONObject();
					msg.put("id", "rec");
					msg.put(interval.getId().toString(), array);

					room.sendMessage(msg.toString());

					System.out.println("REC: " + filepath);

				} catch (ServiceException e) {
					e.printStackTrace();
				}
			}
		});

		endPoint.addMediaSessionStartedListener(new EventListener<MediaSessionStartedEvent>() {
			@Override
			public void onEvent(MediaSessionStartedEvent arg0) {
				endPoint.connect(endPoint);
				recorder.start();
			}
		});

		compositePoint.addMediaSessionStartedListener(new EventListener<MediaSessionStartedEvent>() {
			@Override
			public void onEvent(MediaSessionStartedEvent arg0) {
				endPoint.connect(compositePort/*, MediaType.AUDIO*/);
				compositePort.connect(compositePoint/*, MediaType.AUDIO*/);
			}
		});

		endPoint.addConnectionStateChangedListener(new EventListener<ConnectionStateChangedEvent>() {
			@Override
			public void onEvent(ConnectionStateChangedEvent arg0) {
				// TODO Auto-generated method stub
				if (arg0.getOldState().equals(ConnectionState.CONNECTED)
						&& arg0.getNewState().equals(ConnectionState.DISCONNECTED)) {
					recorder.stop();
				}
			}
		});

	}

	public HubPort getCompositePort() {
		String name = user.getId().toString();
		for (MediaObject port : room.getComposite().getChilds()) {
			if (port.getName().equals(name)) {
				return (HubPort) port;
			}
		}

		HubPort port = new HubPort.Builder(room.getComposite()).build();
		port.setName(name);
		return port;
	}

	public WebRtcEndpoint getWebRtcEndPoint(String name) {
		WebRtcEndpoint ep = endPoints.get(name);
		if (ep == null) {
			ep = new WebRtcEndpoint.Builder(room.getMediaPipeline()).build();
			ep.addOnIceCandidateListener(new EventListener<OnIceCandidateEvent>() {
				@Override
				public void onEvent(OnIceCandidateEvent event) {
					JSONObject response = new JSONObject();
					response.put("id", "iceCandidate");
					response.put("name", name);
					response.put("data", new JSONObject(JsonUtils.toJson(event.getCandidate())));

					try {
						sendMessage(response.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			ep.addErrorListener(new EventListener<ErrorEvent>() {
				@Override
				public void onEvent(ErrorEvent arg0) {
					System.out.println("ERROR: " + arg0.getDescription());
				}
			});

			ep.setStunServerAddress("173.194.67.127");
			ep.setStunServerPort(19302);
			endPoints.put(name, ep);
		}
		return ep;
	}

	public synchronized void sendMessage(final String string) {

		System.out.println("\nSEND:" + string);
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

	@Override
	public void close() throws IOException {
		System.out.println("!!!!!!!!!!!!!!!!! CLOSING SESSION !!!!!!!!!!!!!!!!!");

		endPoint.disconnect(compositePort);
		compositePort.disconnect(compositePoint);

		compositePort.release();
		compositePoint.release();
		endPoint.release();
		recorder.close();
	}

	public void addCandidate(IceCandidate candidate, String name) {
		WebRtcEndpoint ep = endPoints.get(name == null ? "main" : name);
		// XXX [CLIENT_ICE_04] XXX
		ep.addIceCandidate(candidate);
	}

	public User getUser() {
		return user;
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
		timeOffset = offset;
		playUser = userId;

		System.out.println("SET HIST " + userId);

		Date currentTime = new Date(new Date().getTime() - timeOffset);
		UserSession session = room.getParticipant(playUser);
		try {
			GetCurrentRecordingService service = new GetCurrentRecordingService(user.getId().toString(),
					room.getGroupId(), session.getUser().getId().toString(), currentTime);
			Recording rec = service.execute();

			if (rec != null) {
				synchronized (playerLock) {
					if (player != null) {
						player.stop();
						player.release();
						player = null;
					}

					// WEBM

					System.out.println("PLAY: " + rec.getUrl());

					RepositoryItemPlayer item = KurentoManager.repository.getReadEndpoint(rec.getName());

					
					player = new PlayerEndpoint.Builder(room.getMediaPipeline(),item.getUrl()).build();
					// player = new
					// PlayerEndpoint.Builder(room.getMediaPipeline(),
					// uri).build();

					player.addErrorListener(new EventListener<ErrorEvent>() {

						@Override
						public void onEvent(ErrorEvent arg0) {
							System.out.println("FAILURE: " + arg0.getDescription());
							setHistoric(playUser, timeOffset);

						}
					});

					player.addEndOfStreamListener(new EventListener<EndOfStreamEvent>() {
						@Override
						public void onEvent(EndOfStreamEvent arg0) {
							setHistoric(playUser, timeOffset);
						}
					});
					player.connect(endPoint/*, MediaType.VIDEO*/);
				//	player.connect(compositePoint /*, MediaType.AUDIO*/);
					if (play) {
						player.play();
					}
				}

			} else {
				System.out.println("No video here!");
			}

		} catch (

		ServiceException e)

		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setRealtime(String userId) {

		synchronized (playerLock) {
			if (player != null) {
				player.stop();
				player.release();
				player = null;
			}
		}

		playUser = userId;
		UserSession session = room.getParticipant(playUser);
		session.endPoint.connect(endPoint);
//		compositePort.connect(compositePoint, MediaType.AUDIO);

		// mixerPort.connect(endPoint, MediaType.AUDIO);
	}

	public void processOffer(String description, String name) {
		// XXX [CLIENT_ICE_04] XXX
		WebRtcEndpoint ep = endPoints.get(name == null ? "main" : name);

		// XXX [CLIENT_OFFER_04] XXX
		// XXX [CLIENT_OFFER_05] XXX
		String lsd = ep.processOffer(description);
		// XXX [CLIENT_OFFER_06] XXX
		JSONObject data = new JSONObject().put("sdp", lsd).put("type", "answer");

		JSONObject msg = new JSONObject().put("id", "answer").put("data", data).put("name", name);
		// XXX [CLIENT_OFFER_07] XXX
		sendMessage(msg.toString());
		ep.gatherCandidates();
	}

	public void setPlay(boolean play) {
		synchronized (playerLock) {

			if (player != null) {
				if (play) {
					player.play();
				} else {
					player.pause();
				}
			}
			this.play = play;
		}
	}

	public void sendMessages(Long end , int len){
		ListMessagesService messagesService = new ListMessagesService(room.getGroupId(),end,len);
		JSONArray messagesArray = new JSONArray();
		try {
			List<Message> messages = messagesService.execute();
			for(Message message : messages){
				JSONObject messageObj = message.toJsonObject();
				
				messagesArray.put(messageObj);
			}
			
		} catch (ServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		JSONObject msg = new JSONObject();
		msg.put("id", "msg");
		msg.put("data", messagesArray);
		sendMessage(msg.toString());
	}
	
}
