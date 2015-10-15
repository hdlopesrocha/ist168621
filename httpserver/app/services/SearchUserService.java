package services;

import java.util.List;

import org.bson.types.ObjectId;

import models.User;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class SearchUserService extends Service<List<User>> {

	private User caller;
	private String query;

	
	public SearchUserService(String uid, String query) {
		this.caller = User.findById(new ObjectId(uid));
		this.query = query;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public List<User> dispatch() {
		return User.search(query);
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
