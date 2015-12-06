package services;

import org.bson.types.ObjectId;

import services.Service;
import models.User;


/**
 * The Class AuthenticateUserService.
 */
public class AuthenticateUserService extends Service<User> {

	/** The user. */
	private ObjectId user;

	/** The password. */
	private String password;
	


	/**
	 * Instantiates a new authenticate user service.
	 *
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 */
	public AuthenticateUserService(final String userId, final String password) {
		this.user = new ObjectId(userId);
		this.password = password;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public User dispatch() {
		User u = User.findById(user);
		if(u!=null && u.check(password)){
			return u;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#canExecute()
	 */
	@Override
	public boolean canExecute() {
		return user != null && password!=null;
	}

}
