package services;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;

import exceptions.BadRequestException;
import models.HyperContent;
import models.User;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class GetCurrentHyperContentService extends Service<List<HyperContent>> {

	private User caller;
	private ObjectId groupId;
	private Date time;
	private boolean hasMore;
	private static final int PRELOAD_SIZE = 5;

	public GetCurrentHyperContentService(String callerId, String groupId, Date time) {
		this.caller = User.findById(new ObjectId(callerId));
		this.groupId = new ObjectId(groupId);
		this.time = time;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public List<HyperContent> dispatch() throws BadRequestException {
		List<HyperContent> ret = new ArrayList<HyperContent>();
		
		
	
		FindIterable<Document> iter = HyperContent.getCollection().find(new Document("gid", groupId)
				.append("end", new Document("$gte", time)).append("start", new Document("$lt", time)));

		Iterator<Document> it = iter.iterator();

		while (it.hasNext()) {
			Document doc = it.next();
			ret.add(HyperContent.load(doc));
		}
		
		FindIterable<Document> iter2 = HyperContent.getCollection().find(new Document("gid", groupId).append("start", new Document("$gte", time))).limit(PRELOAD_SIZE);

		
		
		Iterator<Document> it2 = iter2.iterator();
		int preloaded = 0;
		while (it2.hasNext()) {
			Document doc = it2.next();
			ret.add(HyperContent.load(doc));
			++preloaded;
		}
		hasMore = PRELOAD_SIZE==preloaded;
			
		
		
		
		return ret;
	}

	
	public boolean hasMore() {
		return hasMore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#canExecute()
	 */
	@Override
	public boolean canExecute() {
		return caller != null;
	}

}
