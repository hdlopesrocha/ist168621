package services;

import models.User;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class GetUserNameService extends Service<String> {

	private User caller;
	private String callerEmail;

	
	public GetUserNameService(String email) {
		this.callerEmail = email;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public String dispatch() {
		
		return caller.getName();
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
