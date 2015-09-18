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

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.bson.types.ObjectId;
import org.kurento.client.KurentoClient;
import org.kurento.client.MediaPipeline;

import models.Group;

/**
 * @author Ivan Gracia (izanmail@gmail.com)
 * @since 4.3.1
 */
public class KurentoManager {
	public final KurentoClient kurento;

	public KurentoManager (){
		System.out.println("Connecting to KMS...");
		kurento = KurentoClient.create("ws://localhost:8888/kurento");		
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
		room.close();
	}

}
