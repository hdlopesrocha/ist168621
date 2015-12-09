
package main;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.bson.types.ObjectId;
import org.kurento.client.KurentoClient;
import org.kurento.client.MediaPipeline;
import org.kurento.repository.rest.RepositoryRestApi;
import org.kurento.repository.rest.RepositoryRestApiProvider;

import models.Group;

public class KurentoManager {
	

	final static String DEFAULT_KMS_WS_URI = "ws://localhost:8888/kurento";
	final static String DEFAULT_REPOSITORY_SERVER_URI = "http://localhost:7676";
	
	public static KurentoClient kurento;
	public static RepositoryRestApi repository;
	
	public KurentoManager (){
		System.out.println("Connecting to KMS...");
		kurento = KurentoClient.create(DEFAULT_KMS_WS_URI);		
		repository = RepositoryRestApiProvider.create(DEFAULT_REPOSITORY_SERVER_URI);
		
		if (repository != null) {
			try {
				Map<String, String> metadata = Collections.emptyMap();
				repository.createRepositoryItem(metadata);
			} catch (Exception e) {
				System.out.println("Unable to create kurento repository items");
			}
		}
		
		
		System.out.println("Ok!");

		System.out.println("Creating rooms...");
		for(MediaPipeline pipeline : kurento.getServerManager().getPipelines()) {
			System.out.println("Loaded: " + pipeline.getId() + " | " +pipeline.getName());
			rooms.put(pipeline.getName(), new Room(pipeline));
		}	
		System.out.println("Ok!");
	}

	private final ConcurrentMap<String, Room> rooms = new ConcurrentHashMap<>();

	/**
	 * @param roomName
	 *            the name of the room
	 * @return the room if it was already created, or a new one if it is the
	 *         first time this room is accessed
	 */
	public Room getRoom(String groupId) {
		Group group = Group.findById(new ObjectId(groupId));
		if(group==null){
			return null;
		}
		Room room = rooms.get(groupId);
		if (room == null) {
			MediaPipeline mp = kurento.createMediaPipeline();
			mp.setName( groupId );
			room = new Room(mp);
			rooms.put(groupId, room);
		}
		return room;
	}

	/**
	 * Removes a room from the list of available rooms
	 *
	 * @param room
	 * @throws IOException
	 */
	public void removeRoom(Room room) {
		this.rooms.remove(room.getId());
	}

}
