package services;

import org.bson.Document;
import org.bson.types.ObjectId;

import models.Group;
import models.Membership;
import models.User;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class PublishService extends Service<Void> {

	private String key;
	private String data;

	public PublishService(String key, String data) {
		this.key = key;
		this.data = data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public Void dispatch() {
		Document first = pubsub.find(new Document("key", key) ).sort(new Document("$natural", -1)).first();
		long ts = 1;
		if (first != null) {
			ts = first.getLong("ts") + 1;
		}

		Document doc = new Document();
		doc.append("ts", ts);
		doc.append("key", key);
		doc.append("data", data);
		
		pubsub.insertOne(doc);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#canExecute()
	 */
	@Override
	public boolean canExecute() {

		return true;
	}

}
