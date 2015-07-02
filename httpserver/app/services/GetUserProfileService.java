package services;

import models.User;

import org.json.JSONObject;

import exceptions.ServiceException;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class GetUserProfileService extends Service<String> {

	private User user, caller;
	private String callerEmail;
	private String userEmail;

	
	public GetUserProfileService(String email, String user) {
		this.callerEmail = email;
		this.userEmail = user;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public String dispatch() {
		JSONObject properties = new JSONObject(user.getPublicProperties().toJson());
		properties.put("email", user.getEmail());
		return properties.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#canExecute()
	 */
	@Override
	public boolean canExecute() {
		if(callerEmail==null || userEmail==null)
			return false;
		
		this.caller = User.findByEmail(callerEmail);
		if(caller!=null && callerEmail.equals(userEmail)){
			this.user = this.caller;
		}
		else {
			this.user = User.findByEmail(userEmail);
		}
		

		try {
			return new HasPermissionService(callerEmail,"ADMIN").execute();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
