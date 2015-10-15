package services;

import models.User;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class AuthenticateTokenService extends Service<Boolean> {

	/** The user. */
	private User user = null;

	/** The password. */
	private String token = null;


	/**
	 * Instantiates a new authenticate user service.
	 *
	 * @param username
	 *          the username
	 * @param password
	 *          the password
	 */
	public AuthenticateTokenService(final String token) {
		this.token = token;
		if (token != null && token.length() > 0) {
			this.user = User.findByToken(token);
		}
	}

	public boolean userExists() {
		return user != null;
	}

	public String getEmail() {
		return this.user.getEmail();
	}
	
	public String getToken() {
		return this.user.getToken();
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


		return user.getToken().equals(token);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#canExecute()
	 */
	@Override
	public boolean canExecute() {
		return this.token != null && user != null;
	}

}
