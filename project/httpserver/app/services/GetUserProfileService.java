package services;

import org.bson.types.ObjectId;
import org.json.JSONObject;

import models.User;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class GetUserProfileService extends Service<String> {

	private User user, caller;

	
	public GetUserProfileService(String callerId, String userId) {
		this.caller = User.findById(new ObjectId(callerId));
		if(caller!=null && callerId.equals(userId)){
			this.user = this.caller;
		}
		else {
			this.user = User.findById(new ObjectId(userId));
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
		return true;
	}

}
