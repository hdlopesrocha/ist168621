package services;

import org.bson.Document;
import org.bson.types.ObjectId;

import models.PublicProfile;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class GetUserPhotoService extends Service<String> {

	private ObjectId callerId;

	
	public GetUserPhotoService(String uid) {
		this.callerId = new ObjectId(uid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public String dispatch() {
		PublicProfile profile = PublicProfile.findByOwner(callerId);
		if(profile!=null){
			Document properties = profile.getData();
			System.out.println("PROPS: "+properties.toJson());
			return properties.containsKey("photo")? properties.getString("photo"):null;
		
		}
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#canExecute()
	 */
	@Override
	public boolean canExecute() {
		return callerId!=null;
	}

}
