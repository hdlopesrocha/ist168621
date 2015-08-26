package services;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
public class ListRecordingsForTimeService extends Service<List<Recording>> {

	private User caller;
	private ObjectId groupId;
	private long duration;
	private String time;
	
	public ListRecordingsForTimeService(String callerId,String groupId, String time, long duration) {
		this.caller = User.findById(new ObjectId(callerId));
		this.groupId = new ObjectId(groupId);
		this.duration = duration;
		this.time = time;
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public List<Recording> dispatch() throws BadRequestException {
		try {
			System.out.println("\tparsing "+time);
			Date parsedTime = Recording.FORMAT.parse(time);
			Date extendedEnd = new Date();
			extendedEnd.setTime(parsedTime.getTime()+duration);
			//Document present = new Document("start", new Document("$lt", parsedTime)).append("end", new Document("$gt", parsedTime));
			//Document future = new Document("start", new Document("$gt", parsedTime)).append("start", new Document("$lt", extendedEnd));
			
			
			FindIterable<Document> iter = Recording.getCollection().find(new Document("gid", groupId).append("end", new Document("$gt", parsedTime)).append("start", new Document("$lt", extendedEnd)));
			List<Recording> ret = new ArrayList<Recording>();
			for (Document doc : iter) {
				ret.add(Recording.load(doc));
			}		
			return ret;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new BadRequestException();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#canExecute()
	 */
	@Override
	public boolean canExecute() {
		return caller!=null;
	}

}
