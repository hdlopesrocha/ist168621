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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import models.User;
import play.mvc.WebSocket;

/**
 * @author Ivan Gracia (izanmail@gmail.com)
 * @since 4.3.1
 */
public class Room implements Closeable {
	private final Logger log = LoggerFactory.getLogger(Room.class);

	private final ConcurrentMap<String, UserSession> participants = new ConcurrentHashMap<>();
	private final MediaPipeline pipeline;


	public Room(MediaPipeline pipeline) {
		this.pipeline = pipeline;
		System.out.println("ROOM "+pipeline.getName()+" has been created");
	}

	@PreDestroy
	private void shutdown() {
		this.close();
	}

	public UserSession join(String uid,WebSocket.In<String> in,
			WebSocket.Out<String> out )
			throws IOException {
		System.out.println(uid + " joining " + pipeline.getName());
		final UserSession participant = new UserSession(uid, pipeline.getName(), this.pipeline,in ,out);
		joinRoom(participant);
		participants.put(participant.getUid(), participant);
		System.out.println("send participant names");
		sendParticipantNames(participant);
		return participant;
	}

	public void leave(UserSession user) throws IOException {
		log.debug("PARTICIPANT {}: Leaving room {}", user.getUid(), pipeline.getName());
		this.removeParticipant(user.getUid());
		user.close();
	}

	/**
	 * @param participant
	 * @throws IOException
	 */
	private Collection<String> joinRoom(UserSession participant)
			throws IOException {
		JSONObject data = new JSONObject();
		
		User user = User.findById(new ObjectId(participant.getUid()));

		
		data.put(user.getId().toString(),user.getEmail());

		final JSONObject msg = new JSONObject();
		msg.put("id", "newParticipantArrived");
		msg.put("data", data);

		final List<String> participantsList = new ArrayList<>(participants
				.values().size());
	

		for (final UserSession session : participants.values()) {
			session.sendMessage(msg.toString());
			participantsList.add(session.getUid());
		}

		return participantsList;
	}

	private void removeParticipant(String uid) throws IOException {
		participants.remove(uid);

		final List<String> unnotifiedParticipants = new ArrayList<>();
		final JsonObject participantLeftJson = new JsonObject();
		participantLeftJson.addProperty("id", "participantLeft");
		participantLeftJson.addProperty("uid", uid);
		for (final UserSession participant : participants.values()) {
			try {
				participant.cancelVideoFrom(uid);
				participant.sendMessage(participantLeftJson);
			} catch (final IOException e) {
				unnotifiedParticipants.add(participant.getUid());
			}
		}

		if (!unnotifiedParticipants.isEmpty()) {
			log.debug(
					"ROOM {}: The users {} could not be notified that {} left the room",
					pipeline.getName(), unnotifiedParticipants, uid);
		}

	}

	public void sendParticipantNames(UserSession session) throws IOException {

		final JSONObject data = new JSONObject();
		for (final UserSession participant : this.getParticipants()) {
			User user = User.findById(new ObjectId(participant.getUid()));
			
			//if (!participant.equals(user)) {
			data.put(user.getId().toString(),user.getEmail());
			//}
		}

		final JSONObject msg = new JSONObject();
		msg.put("id", "existingParticipants");
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
	public UserSession getParticipant(String uid) {
		return participants.get(uid);
	}

	@Override
	public void close() {
		for (final UserSession user : participants.values()) {
			try {
				user.close();
			} catch (IOException e) {
				log.debug("ROOM {}: Could not invoke close on participant {}",
						pipeline.getName(), user.getUid(), e);
			}
		}

		participants.clear();

		pipeline.release(new Continuation<Void>() {

			@Override
			public void onSuccess(Void result) throws Exception {
				log.trace("ROOM {}: Released Pipeline", pipeline.getName());
			}

			@Override
			public void onError(Throwable cause) throws Exception {
				log.warn("PARTICIPANT {}: Could not release Pipeline",pipeline.getName());
			}
		});

		log.debug("Room {} closed", pipeline.getName());
	}

	public String getId() {
		return pipeline.getName();
	}

}
