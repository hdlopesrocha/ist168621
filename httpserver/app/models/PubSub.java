package models;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CreateCollectionOptions;

import services.Service;

public class PubSub {
	private static MongoCollection<Document> collection, keyCollection;

	public static MongoCollection<Document> getKeyCollection() {
		return keyCollection;
	}



	static {
		collection = Service.getDatabase().getCollection("pubsub");
		keyCollection = Service.getDatabase().getCollection("pubsubkeys");

		boolean hasCollection = false;
		for (String str :Service.getDatabase().listCollectionNames()){
			if(str.equals("pubsub")){
				hasCollection = true;
				break;
			}
		}
		
	
		if(!hasCollection){
			System.out.println("init pubsub");
			CreateCollectionOptions options = new CreateCollectionOptions();
			options.capped(true);
			options.sizeInBytes(20971520);
			Service.getDatabase().createCollection("pubsub", options);
			collection = Service.getDatabase().getCollection("pubsub");
		}
	
	}

	
	
	public static MongoCollection<Document> getCollection() {
		return collection;
	}
}
