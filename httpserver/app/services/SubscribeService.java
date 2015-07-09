package services;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.CursorType;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;

import models.Group;
import models.Membership;
import models.User;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class SubscribeService extends Service<Document> {

	private String key;
	private Long ts;

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
		if(ts.equals(0l)){
			Document first = pubsub.find(new Document("key", key)).sort(new Document("$natural", -1)).first();
			if (first != null) {
				ts = first.getLong("ts") - 1;
			}
		}

		final Document query = new Document("ts", new Document("$gt", ts)).append("key", key);
		MongoCursor<Document> cursor = pubsub.find(query).sort(new Document("$natural", 1))
				.cursorType(CursorType.TailableAwait).noCursorTimeout(true).iterator();

		return cursor.next();
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
