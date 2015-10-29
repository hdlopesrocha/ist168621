package services;

import java.util.concurrent.TimeUnit;

import org.bson.Document;

import com.mongodb.CursorType;
import com.mongodb.MongoExecutionTimeoutException;
import com.mongodb.client.MongoCursor;

import models.PubSub;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class SubscribeService extends Service<Document> {

	private String key;
	private long ts;

	public SubscribeService(String key, Long ts) {
		this.key = key;
		this.ts = ts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public Document dispatch() {
		if(ts == -1l){
			Document find = PubSub.getKeyCollection().find(new Document("key",key)).first();
			if(find!=null){
				return find;
			}
		}

		final Document query = new Document("ts", new Document("$gt", ts)).append("key", key);
		MongoCursor<Document> cursor = PubSub.getCollection().find(query).sort(new Document("$natural", 1)).cursorType(CursorType.TailableAwait).maxTime(10, TimeUnit.SECONDS).iterator();
		try {
			System.out.println("\tsub wait ts > "+ts);
			Document doc= cursor.next();
			System.out.println("\tsub return");
			return doc;
		}
		catch(MongoExecutionTimeoutException e){
			System.out.println("\tsub timeout key = "+key);
			return null;
		}
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
