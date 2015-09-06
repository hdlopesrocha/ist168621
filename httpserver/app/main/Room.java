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
import org.kurento.client.Composite;
import org.kurento.client.DispatcherOneToMany;
import org.kurento.client.ErrorEvent;
import org.kurento.client.EventListener;
import org.kurento.client.Hub;
import org.kurento.client.MediaObject;
import org.kurento.client.MediaPipeline;

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
	private Hub composite;
	
	public Room(final MediaPipeline mediaPipeline) {
		this.mediaPipeline = mediaPipeline;
		
		
		
		
		
		for(MediaObject obj : mediaPipeline.getChilds()){
			if("composite".equals(obj.getName())){
	//			this.composite = (Hub) obj;
			}
		}
		if(this.composite==null){
			this.composite = new Composite.Builder(mediaPipeline).build();
			this.composite.setName("composite");
		}
		
		this.mediaPipeline.addErrorListener(new EventListener<ErrorEvent>() {

			@Override
			public void onEvent(ErrorEvent arg0) {

				System.out.println("PIPELINE ERROR");
				System.out.println(arg0.getDescription());
			}
		});
		
		this.group = Group.findById(new ObjectId(mediaPipeline.getName()));
		System.out.println("ROOM "+mediaPipeline.getName()+" has been created");	
	}

	public void sendMessage(final String string) {
		for(UserSession user : participants.values()){
			user.sendMessage(string);
		}
	}	
	
	public Hub getComposite() {
		return composite;
		
	}



	@PreDestroy
	private void shutdown() {
		this.close();
	}

	public String getGroupId() {
		return group.getId().toString();
	}

	
	
	

	
	public DispatcherOneToMany createDispatcher(UserSession session){

		DispatcherOneToMany dotm = new  DispatcherOneToMany.Builder(mediaPipeline).build();
		//dotm.setSource(session.getEndpoint(null));
		
		return dotm;
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
		System.out.println("------------- ROOM CLOSE --------------");
		for (final UserSession user : participants.values()) {
			try {
				user.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		participants.clear();
		mediaPipeline.release();
	}

	public String getId() {
		return group.getId().toString();
	}

	public MediaPipeline getMediaPipeline() {
		return mediaPipeline;
	}

}
