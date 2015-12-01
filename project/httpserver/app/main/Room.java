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
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PreDestroy;

import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kurento.client.Composite;
import org.kurento.client.ErrorEvent;
import org.kurento.client.EventListener;
import org.kurento.client.Hub;
import org.kurento.client.HubPort;
import org.kurento.client.MediaElement;
import org.kurento.client.MediaObject;
import org.kurento.client.MediaPipeline;

import exceptions.ServiceException;
import models.Group;
import models.Interval;
import models.User;
import play.mvc.WebSocket;
import services.CreateRecordingService;


public class Room implements Closeable {

	private final ConcurrentMap<String, UserSession> participants = new ConcurrentHashMap<String, UserSession>();
	private final MediaPipeline mediaPipeline;
	private final Group group;
	private Hub composite= null;
	private MyRecorder recorder;
	private HubPort hubPort;

	public Room(final MediaPipeline mediaPipeline) {
		this.mediaPipeline = mediaPipeline;
		
		this.mediaPipeline.addErrorListener(new EventListener<ErrorEvent>() {

			@Override
			public void onEvent(ErrorEvent arg0) {

				System.out.println("PIPELINE ERROR");
				System.out.println(arg0.getDescription());
			}
		});
		
		for(MediaObject obj : mediaPipeline.getChilds()){
			if(obj.getName().equals("composite")){
				this.composite = (Hub) obj;
				for(MediaObject child : obj.getChilds()){
					child.release();
				}
			}
		}
		this.group = Group.findById(new ObjectId(mediaPipeline.getName()));
		
		if(this.composite==null){
			this.composite = new Composite.Builder(mediaPipeline).build();
			this.composite.setName("composite");
		}
		this.hubPort = getCompositePort("this");
		this.recorder = record(hubPort,group.getId().toString());
		this.recorder.start();
		System.out.println("ROOM "+mediaPipeline.getName()+" has been created");	
	}

	
	public HubPort getHubPort() {
		return hubPort;
	}


	public MyRecorder record(final MediaElement endPoint,final String id){

		return new MyRecorder(endPoint, new MyRecorder.RecorderHandler() {
			@Override
			public String onFileRecorded(Date begin, Date end, String filepath, String filename, String intervalId) {

				try {
					CreateRecordingService srs = new CreateRecordingService(null, filepath, getGroupId(),
							id, begin, end, filename, "video/webm", intervalId);
					srs.execute();

					Interval interval = srs.getInterval();
					intervalId = interval.getId().toString();

			
					JSONArray array = new JSONArray();
					array.put(Tools.FORMAT.format(interval.getStart()));
					array.put(Tools.FORMAT.format(interval.getEnd()));

					JSONObject msg = new JSONObject();
					msg.put("id", "rec");
					msg.put(intervalId, array);
					sendMessage(msg.toString());
					
					System.out.println("REC: " + filepath);

				} catch (ServiceException e) {
					e.printStackTrace();
				}
				return intervalId;
			}
		});
	}
	
	public HubPort getCompositePort(String id) {
		for (MediaObject port : getComposite().getChilds()) {
			if (port.getName().equals(id)) {
				return (HubPort) port;
			}
		}

		HubPort port = new HubPort.Builder(getComposite()).build();
		port.setName(id);
		return port;
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
		this.recorder.close();
		this.close();
	}

	public String getGroupId() {
		return group.getId().toString();
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
		return uid!=null ? participants.get(uid) : null;
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
