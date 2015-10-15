package services;

import org.bson.Document;
import org.bson.types.ObjectId;

import models.User;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class GetUserPhotoService extends Service<String> {

	private User caller;

	
	public GetUserPhotoService(String uid) {
		this.caller = User.findById(new ObjectId(uid));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public String dispatch() {
		Document properties = caller.getPublicProperties();
		System.out.println("PROPS: "+properties.toJson());
		return properties.containsKey("photo")? properties.getString("photo"):null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#canExecute()
	 */
	@Override
	public boolean canExecute() {
		return caller!=null;
	}

}
