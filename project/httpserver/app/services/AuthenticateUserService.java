package services;

import models.User;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class AuthenticateUserService extends Service<Boolean> {

	/** The user. */
	private User user;

	/** The password. */
	private String password;
	
	private String token;

	/**
	 * Instantiates a new authenticate user service.
	 *
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 */
	public AuthenticateUserService(final String email, final String password) {
		if (email != null && email.length()>0) {
			this.user = User.findByEmail(email);
		}
		this.password = password;
	}

	public String getToken() {
		// TODO Auto-generated method stub
		return token;
	}
	public String getUserId(){
		return this.user.getId().toString();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public Boolean dispatch() {
		boolean ret = user.check(password);
		if (ret) {
			this.token = user.getToken();
			
			
			
		}
		return ret;
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
