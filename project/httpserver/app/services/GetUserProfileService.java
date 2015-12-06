package services;

import org.bson.types.ObjectId;
import org.json.JSONObject;

import models.IdentityProfile;
import models.PublicProfile;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class GetUserProfileService extends Service<JSONObject> {

	private ObjectId user, caller;

	
	public GetUserProfileService(String callerId, String userId) {
		this.caller = new ObjectId(callerId);
		this.user = new ObjectId(userId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public JSONObject dispatch() {
		PublicProfile publicProfile = PublicProfile.findByOwner(user);
		IdentityProfile identityProfile = IdentityProfile.findByOwner(user);
		
		JSONObject props = new JSONObject(publicProfile.getData().toJson());
		JSONObject identityProperties = new JSONObject(identityProfile.getData().toJson());
		for(Object k: identityProperties.keySet()){
			String key = (String) k;
			props.put(key, identityProperties.get(key));
		}
		
		props.put("id", user.toString());
		
		return props;
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
