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
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PreDestroy;

import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.kurento.client.Continuation;
import org.kurento.client.ElementDisconnectedEvent;
import org.kurento.client.EventListener;
import org.kurento.client.HttpEndpoint;
import org.kurento.client.MediaPipeline;
import org.kurento.client.OnIceCandidateEvent;
import org.kurento.client.PlayerEndpoint;
import org.kurento.client.RecorderEndpoint;
import org.kurento.client.WebRtcEndpoint;
import org.kurento.jsonrpc.JsonUtils;

import com.google.gson.JsonObject;

import models.Group;
import models.User;
import play.mvc.WebSocket;

/**
 * @author Ivan Gracia (izanmail@gmail.com)
 * @since 4.3.1
 */
public class Room implements Closeable {

	private final ConcurrentMap<String, UserSession> participants = new ConcurrentHashMap<>();
	private final MediaPipeline mediaPipeline;
	private final Group group;
	
	public Room(final MediaPipeline mediaPipeline) {
		this.mediaPipeline = mediaPipeline;
		this.group = Group.findById(new ObjectId(mediaPipeline.getName()));
		System.out.println("ROOM "+mediaPipeline.getName()+" has been created");
		
		
		
	}

	@PreDestroy
	private void shutdown() {
		this.close();
	}

	public String getGroupId() {
		return group.getId().toString();
	}

	
	public RecorderEndpoint recordEndpoint(WebRtcEndpoint ep, UserSession session, Long sequence){
		String filepath = "file:///var/www/html/"+group.getId().toString()+"-"+session.getUser().getId().toString()+"-"+sequence+".webm";
		System.out.println("REC: "+ filepath);
		RecorderEndpoint rec = new RecorderEndpoint.Builder(mediaPipeline,filepath).build();
		PlayerEndpoint player = new PlayerEndpoint.Builder(mediaPipeline, filepath).build();
		
		
		rec.addElementDisconnectedListener(new EventListener<ElementDisconnectedEvent>() {

			@Override
			public void onEvent(ElementDisconnectedEvent arg0) {
				System.out.println("STOP: "+ filepath);
			}
		});
		
System.out.println("---------------------------");
		System.out.println(player.getUri());
		System.out.println(player.getGstreamerDot());
System.out.println("---------------------------");
		


		
		rec.record();
		ep.connect(rec);
		return rec;
	}
	
	
	public WebRtcEndpoint createWebRtcEndPoint(UserSession session, String senderId){
		WebRtcEndpoint ep = new WebRtcEndpoint.Builder(mediaPipeline).build();

	
		ep.addOnIceCandidateListener(new EventListener<OnIceCandidateEvent>() {

			@Override
			public void onEvent(OnIceCandidateEvent event) {
				JsonObject response = new JsonObject();
				response.addProperty("id", "iceCandidate");
				if(senderId!=null){
					response.addProperty("uid", senderId);
				}
				response.add("candidate", JsonUtils.toJsonObject(event.getCandidate()));
				try {
					synchronized (this) {
						System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1");
						System.out.println(response.toString());
						session.sendMessage(response.toString());
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
	
	
	public UserSession join(final User user, final WebSocket.Out<String> out )
			throws IOException {
	
		System.out.println(user.getEmail() + " joining " + mediaPipeline.getName());
		final UserSession participant = new UserSession(user, this, out);
		participants.put(participant.getUser().getId().toString(), participant);

		final JSONObject myData = new JSONObject();
		final JSONObject otherData = new JSONObject().put(user.getId().toString(),user.getEmail());
		final JSONObject otherMsg = new JSONObject().put("id", "participants").put("data", otherData);
		for (final UserSession session : participants.values()) {
			if(session!=participant) {
				session.sendMessage(otherMsg.toString());
			}
			User sessionUser = session.getUser();
			myData.put(sessionUser.getId().toString(),sessionUser.getEmail());
		}		
		final JSONObject myMsg = new JSONObject().put("id", "participants").put("data", myData);
		participant.sendMessage(myMsg.toString());
		return participant;
	}

	public void leave(final UserSession user) throws IOException {
		String uid = user.getUser().getId().toString();

		participants.remove(uid);

		final JSONObject participantLeftJson = new JSONObject();
		participantLeftJson.put("id", "participantLeft");
		participantLeftJson.put("uid", uid);
		for (final UserSession participant : participants.values()) {
			
			participant.cancelVideoFrom(user);
			participant.sendMessage(participantLeftJson.toString());
			
		
			
		}
		
		user.close();
	}

	/**
	 * @return a collection with all the participants in the room
	 */
	public Collection<UserSession> getParticipants() {
		return participants.values();
	}

	/**
	 * @param name
	 * @return the participant from this session
	 */
	public UserSession getParticipant(final String uid) {
		return participants.get(uid);
	}

	@Override
	public void close() {
		for (final UserSession user : participants.values()) {
			try {
				user.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		participants.clear();
		mediaPipeline.release(new Continuation<Void>() {

			@Override
			public void onSuccess(Void result) throws Exception {
			}

			@Override
			public void onError(Throwable cause) throws Exception {
			}
		});
	}

	public String getId() {
		return group.getId().toString();
	}

}
