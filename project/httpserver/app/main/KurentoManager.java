package main;

import models.Group;
import org.bson.types.ObjectId;
import org.kurento.client.KurentoClient;
import org.kurento.client.MediaPipeline;
import org.kurento.repository.RepositoryClient;
import org.kurento.repository.RepositoryClientProvider;


import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * The Class KurentoManager.
 */
public class KurentoManager {


    /** The Constant DEFAULT_KMS_WS_URI. */
    private final static String DEFAULT_KMS_WS_URI = "ws://localhost:8888/kurento";
    
    /** The Constant DEFAULT_REPOSITORY_SERVER_URI. */
    private final static String DEFAULT_REPOSITORY_SERVER_URI = "http://localhost:7676";
    
    /** The repository. */
    public static RepositoryClient repository;
    
    /** The kurento. */
    private static KurentoClient kurento;
    
    /** The rooms. */
    private final ConcurrentMap<String, Room> rooms = new ConcurrentHashMap<>();

    /**
     * Instantiates a new kurento manager.
     */
    public KurentoManager() {
        System.out.println("Connecting to KMS...");
        kurento = KurentoClient.create(DEFAULT_KMS_WS_URI);
        repository = RepositoryClientProvider.create(DEFAULT_REPOSITORY_SERVER_URI);

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
        for (MediaPipeline pipeline : kurento.getServerManager().getPipelines()) {
            System.out.println("Loaded: " + pipeline.getId() + " | " + pipeline.getName());
            rooms.put(pipeline.getName(), new Room(pipeline));
        }
        System.out.println("Ok!");
    }


    /**
     * Gets the room.
     *
     * @param groupId the group id
     * @return the room
     */
    public Room getRoom(String groupId) {
        Group group = Group.findById(new ObjectId(groupId));
        if (group == null) {
            return null;
        }
        Room room = rooms.get(groupId);
        if (room == null) {
            MediaPipeline mp = kurento.createMediaPipeline();
            mp.setName(groupId);
            room = new Room(mp);
            rooms.put(groupId, room);
        }
        return room;
    }


    /**
     * Removes the room.
     *
     * @param room the room
     */
    public void removeRoom(Room room) {
        this.rooms.remove(room.getId());
    }

}
