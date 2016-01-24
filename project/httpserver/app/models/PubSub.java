package models;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CreateCollectionOptions;
import org.bson.Document;
import services.Service;


/**
 * The Class PubSub.
 */
public class PubSub {
    
    /** The Constant keyCollection. */
    private static final MongoCollection<Document> keyCollection;
    
    /** The collection. */
    private static MongoCollection<Document> collection;

    static {
        collection = Service.getDatabase().getCollection("pubsub");
        keyCollection = Service.getDatabase().getCollection("pubsubkeys");

        boolean hasCollection = false;
        for (String str : Service.getDatabase().listCollectionNames()) {
            if (str.equals("pubsub")) {
                hasCollection = true;
                break;
            }
        }

        if (!hasCollection) {
            System.out.println("init pubsub");
            CreateCollectionOptions options = new CreateCollectionOptions();
            options.capped(true);
            options.sizeInBytes(20971520);
            Service.getDatabase().createCollection("pubsub", options);
            collection = Service.getDatabase().getCollection("pubsub");
        }

    }

    /**
     * Gets the key collection.
     *
     * @return the key collection
     */
    public static MongoCollection<Document> getKeyCollection() {
        return keyCollection;
    }

    /**
     * Gets the collection.
     *
     * @return the collection
     */
    public static MongoCollection<Document> getCollection() {
        return collection;
    }
}
