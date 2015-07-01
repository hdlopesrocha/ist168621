package services;

import org.bson.Document;

import models.User;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class GetUserPhotoService extends Service<String> {

	private User caller;
	private String callerEmail;

	
	public GetUserPhotoService(String email) {
		this.callerEmail = email;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public String dispatch() {
		Document properties = caller.getPublicProperties();
		return properties.getString("photo");
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
