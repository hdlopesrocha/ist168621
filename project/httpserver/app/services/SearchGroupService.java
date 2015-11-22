package services;

import java.util.List;

import org.bson.types.ObjectId;

import models.Group;
import models.User;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class SearchGroupService extends Service<List<Group>> {

	private User caller;
	private String query;

	
	public SearchGroupService(String uid, String query) {
		this.caller = User.findById(new ObjectId(uid));
		this.query = query;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public List<Group> dispatch() {
		return Group.search(caller,query);
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
