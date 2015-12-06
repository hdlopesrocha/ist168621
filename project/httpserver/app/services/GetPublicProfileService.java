package services;

import org.bson.types.ObjectId;

import services.Service;
import models.PublicProfile;


/**
 * The Class AuthenticateUserService.
 */
public class GetPublicProfileService extends Service<PublicProfile> {

	private ObjectId user;

	public GetPublicProfileService(String userId) {
		this.user =  new ObjectId(userId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public PublicProfile dispatch() {
		return PublicProfile.findByOwner(user);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#canExecute()
	 */
	@Override
	public boolean canExecute() {

		return true;
	}

}