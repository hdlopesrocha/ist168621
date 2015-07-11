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

	
	public GetUserProfileService(String callerId, String userId) {
		this.caller = User.findByEmail(callerId);
		if(caller!=null && callerId.equals(userId)){
			this.user = this.caller;
		}
		else {
			this.user = User.findByEmail(userId);
		}
		
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
		if(caller==null || user==null)
			return false;
		
		

		try {
			return new HasPermissionService(caller.getId().toString(),"ADMIN").execute();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
