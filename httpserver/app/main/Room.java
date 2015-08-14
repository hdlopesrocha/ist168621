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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PreDestroy;

import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.kurento.client.Continuation;
import org.kurento.client.MediaPipeline;
import org.kurento.client.WebRtcEndpoint;

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

	public WebRtcEndpoint createWebRtcEndPoint(){
		WebRtcEndpoint ep = new WebRtcEndpoint.Builder(mediaPipeline).build();
		ep.setStunServerAddress("173.194.67.127");
		ep.setStunServerPort(19302);
		return ep;
	}
	
	
	public UserSession join(final User user, final WebSocket.Out<String> out )
			throws IOException {
	
		System.out.println(user.getEmail() + " joining " + mediaPipeline.getName());
		final UserSession participant = new UserSession(user, this, out);
		participants.put(participant.getUser().getId().toString(), participant);

		final JSONObject data = new JSONObject().put(user.getId().toString(),user.getEmail());
		final JSONObject msg = new JSONObject().put("id", "participants").put("data", data);
		for (final UserSession session : participants.values()) {
			session.sendMessage(msg.toString());
		}		
		sendParticipantNames(participant);
		return participant;
	}

	public void leave(final UserSession user) throws IOException {
		this.removeParticipant(user.getUser().getId().toString());
		user.close();
	}

	private void removeParticipant(final String uid) throws IOException {
		participants.remove(uid);

		final List<String> unnotifiedParticipants = new ArrayList<>();
		final JSONObject participantLeftJson = new JSONObject();
		participantLeftJson.put("id", "participantLeft");
		participantLeftJson.put("uid", uid);
		for (final UserSession participant : participants.values()) {
			//try {
			//	participant.cancelVideoFrom(uid);
			//	participant.sendMessage(participantLeftJson.toString());
			//} catch (final IOException e) {
			//	unnotifiedParticipants.add(participant.getUserId());
			//}
		}
		if (!unnotifiedParticipants.isEmpty()) {

		}
	}

	private void sendParticipantNames(final UserSession session) throws IOException {
		final JSONObject data = new JSONObject();
		for (final UserSession participant : this.getParticipants()) {
			User user = participant.getUser();
			//if (!participant.equals(user)) {
			data.put(user.getId().toString(),user.getEmail());
			//}
		}

		final JSONObject msg = new JSONObject();
		msg.put("id", "participants");
		msg.put("data", data);
		session.sendMessage(msg.toString());
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
