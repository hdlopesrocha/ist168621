package services;

import org.bson.Document;
import org.bson.types.ObjectId;

import models.Permission;
import services.Service;
import models.PublicProfile;


/**
 * The Class AuthenticateUserService.
 */
public class SetPublicProfileService extends Service<Void> {

	private ObjectId user;
	private ObjectId caller;
	private String data;

	public SetPublicProfileService(String caller, String userId, String data) {
		this.caller =  new ObjectId(caller);
		this.user =  new ObjectId(userId);
		this.data = data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public Void dispatch() {
		PublicProfile profile = PublicProfile.findByOwner(user);
		Document doc = Document.parse(data);
		if(profile==null){
			profile = new PublicProfile(user, doc);
		}else {
			profile.setData(doc);
		}
		profile.save();
		return null;
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