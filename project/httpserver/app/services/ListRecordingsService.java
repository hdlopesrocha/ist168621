package services;

import java.util.List;

import org.bson.types.ObjectId;

import models.Recording;
import models.User;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class ListRecordingsService extends Service<List<Recording>> {

	private User caller;
	private ObjectId groupId;
	private long sequence;
	
	public ListRecordingsService(String callerId,String groupId, long sequence) {
		this.caller = User.findById(new ObjectId(callerId));
		this.groupId = new ObjectId(groupId);
		this.sequence = sequence;
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public List<Recording> dispatch() {
		return Recording.listByGroup(groupId, sequence);
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
