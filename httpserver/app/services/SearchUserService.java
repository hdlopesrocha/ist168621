package services;

import java.util.List;

import org.json.JSONObject;

import exceptions.ServiceException;
import models.User;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class SearchUserService extends Service<List<User>> {

	private User caller;
	private String callerEmail;
	private String query;

	
	public SearchUserService(String email, String query) {
		this.callerEmail = email;
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
		if(callerEmail==null)
			return false;
		
		this.caller = User.findByEmail(callerEmail);

		return caller!=null;
	}

}
