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
import org.kurento.client.MediaType;
import org.kurento.client.OnIceCandidateEvent;
import org.kurento.client.PlayerEndpoint;
import org.kurento.client.WebRtcEndpoint;
import org.kurento.jsonrpc.JsonUtils;

import com.google.gson.JsonObject;

import exceptions.ServiceException;
import main.MyRecorder.RecorderHandler;
import models.Interval;
import models.Recording;
import models.User;
import play.mvc.WebSocket;
import services.GetCurrentRecordingService;
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
	private final WebRtcEndpoint compositePoint;
	private final Map<String,WebRtcEndpoint> endPoints = new TreeMap<String,WebRtcEndpoint>();

	private final MyRecorder recorder;
	private HubPort compositePort;
	private String intervalId = null;

	private PlayerEndpoint player;
	private boolean realTime = true;
	private long playOffset = 0l;
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
					SaveRecordingService srs = new SaveRecordingService(null, filepath, getGroupId(),
							getUser().getId().toString(), begin, end, filename, "video/webm", intervalId);
					srs.execute();

					Interval interval = srs.getInterval();
					intervalId = interval.getId().toString();

					JSONArray array = new JSONArray();
					array.put(Recording.FORMAT.format(interval.getStart()));
					array.put(Recording.FORMAT.format(interval.getEnd()));

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
				endPoint.connect(compositePort);
				compositePort.connect(compositePoint);
				endPoint.connect(endPoint);
				recorder.start();	
				/*new MyRecorder(compositePort, new RecorderHandler() {
					@Override
					public void onFileRecorded(Date begin, Date end, String filepath, String filename) {
					}
				}).start();*/
			}
		});
		


		this.endPoint.addConnectionStateChangedListener(new EventListener<ConnectionStateChangedEvent>() {

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

	
	public HubPort getCompositePort(){
		String name = user.getId().toString();
		for (MediaObject port : room.getComposite().getChilds()){
			if(port.getName().equals(name)){
				return (HubPort) port;
			}
		}
		
		HubPort port = new HubPort.Builder(room.getComposite()).build();
		port.setName(name);
		return port;
	}
	
	
	public WebRtcEndpoint getWebRtcEndPoint(String name) {
		WebRtcEndpoint ep = endPoints.get(name);
		if(ep==null){
			ep = new WebRtcEndpoint.Builder(room.getMediaPipeline()).build();
			ep.addOnIceCandidateListener(new EventListener<OnIceCandidateEvent>() {
				@Override
				public void onEvent(OnIceCandidateEvent event) {
					JsonObject response = new JsonObject();
					response.addProperty("id", "iceCandidate");
					response.addProperty("name", name);
					response.add("candidate", JsonUtils.toJsonObject(event.getCandidate()));
					
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
		
		System.out.println("SEND:" + string);
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
		WebRtcEndpoint ep = endPoints.get(name==null? "main" : name);
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
		playOffset = offset;
		playUser = userId;

		if (player != null) {
			player.release();
			realTime = true;
		}

		System.out.println("SET HIST " + userId);
		if (realTime) {
			realTime = false;

			Date currentTime = new Date(new Date().getTime() - playOffset);
			UserSession session = room.getParticipant(playUser);
			try {
				GetCurrentRecordingService service = new GetCurrentRecordingService(user.getId().toString(),
						room.getGroupId(), session.getUser().getId().toString(), currentTime);
				Recording rec = service.execute();

				if (rec != null) {
					System.out.println("PLAY:" + rec.getUrl());

					player = new PlayerEndpoint.Builder(room.getMediaPipeline(), rec.getUrl()).build();

					player.addEndOfStreamListener(new EventListener<EndOfStreamEvent>() {

						@Override
						public void onEvent(EndOfStreamEvent arg0) {
							// TODO Auto-generated method stub
							if (!realTime) {

								player.release();
								player = null;
								realTime = true;
								setHistoric(playUser, playOffset);
							}
						}
					});
					player.connect(compositePoint);
					player.play();

				} else {
					realTime = true;
					System.out.println("No video here!");
				}

			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void setRealtime(String userId) {
		playUser = userId;
		realTime = true;
		UserSession session = room.getParticipant(playUser);
		session.endPoint.connect(endPoint, MediaType.VIDEO);
		// mixerPort.connect(endPoint, MediaType.AUDIO);
	}

	public void setMix() {
		// TODO Auto-generated method stub
		// mixerPort.connect(endPoint, MediaType.AUDIO);
		// mix only audio
	}

	public void processOffer(String description, String name) {
		// XXX [CLIENT_ICE_04] XXX
		WebRtcEndpoint ep = endPoints.get(name==null? "main" : name);
		
		// XXX [CLIENT_OFFER_04] XXX
		// XXX [CLIENT_OFFER_05] XXX
		String arg0 = ep.processOffer(description);
		// XXX [CLIENT_OFFER_06] XXX
		JSONObject msg = new JSONObject().put("id", "answer").put("sdp", arg0).put("type", "answer").put("name",name);
		// XXX [CLIENT_OFFER_07] XXX
		sendMessage(msg.toString());
		ep.gatherCandidates();
	}

}
