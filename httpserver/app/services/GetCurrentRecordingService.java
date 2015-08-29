package services;

import java.util.Date;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;

import exceptions.BadRequestException;
import models.Recording;
import models.User;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class GetCurrentRecordingService extends Service<Recording> {

	private User caller;
	private ObjectId groupId;
	private ObjectId userId;
	private Date time;

	public GetCurrentRecordingService(String callerId, String groupId, String userId, Date time) {
		this.caller = User.findById(new ObjectId(callerId));
		this.userId = new ObjectId(userId);
		this.groupId = new ObjectId(groupId);
		this.time = time;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public Recording dispatch() throws BadRequestException {

		FindIterable<Document> iter = Recording.getCollection().find(new Document("gid", groupId).append("uid", userId)
				.append("end", new Document("$gt", time)).append("start", new Document("$lt", time)));
		
		Document first = iter.first();
		
		return first!=null? Recording.load(first) : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#canExecute()
	 */
	@Override
	public boolean canExecute() {
		return caller != null && userId != null;
	}

}
