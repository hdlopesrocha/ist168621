package services;

import org.bson.types.ObjectId;

import models.Permission;
import services.Service;
import models.PrivateProfile;


/**
 * The Class AuthenticateUserService.
 */
public class GetPrivateProfileService extends Service<PrivateProfile> {

	private ObjectId user;
	private ObjectId caller;

	public GetPrivateProfileService(String caller, String userId) {
		this.caller =  new ObjectId(caller);
		this.user =  new ObjectId(userId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public PrivateProfile dispatch() {
		return PrivateProfile.findByOwner(user);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#canExecute()
	 */
	@Override
	public boolean canExecute() {
		return (user!=null && user==caller) || Permission.find(caller,Permission.PERMISSION_ADMIN,null)!=null;
	}

}