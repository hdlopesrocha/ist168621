package services;

import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;

import models.IdentityProfile;
import models.Permission;
import models.PublicProfile;


/**
 * The Class AuthenticateUserService.
 */
public class SetIdentityProfileService extends Service<Void> {

	private ObjectId user;
	private ObjectId caller;
	private String data;
	private Document doc;

	public SetIdentityProfileService(String caller, String userId, String data) {
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
		doc = Document.parse(data);
		for(Map.Entry<String, Object> pair : doc.entrySet()){
			IdentityProfile profile = IdentityProfile.find(pair.getKey(),pair.getValue().toString());
			if(profile!=null && !profile.getOwner().equals(user)){
				return false;
			}
		}
		return (user!=null && user==caller) || Permission.find(caller,Permission.PERMISSION_ADMIN,null)!=null;
	}

}