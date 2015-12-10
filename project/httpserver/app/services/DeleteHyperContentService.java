package services;


import org.bson.types.ObjectId;

import models.Group;
import models.HyperContent;
import models.User;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class DeleteHyperContentService extends Service<Void> {

	private User user;
	private Group group;
	private ObjectId contentId = null;

	
	public DeleteHyperContentService(String uid,String gid,String cid) {
		this.user = User.findById(new ObjectId(uid));
		this.group = Group.findById(new ObjectId(gid));
		this.contentId = new ObjectId(cid);
	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public Void dispatch() {
		HyperContent hyperContent = HyperContent.findById(contentId);
		hyperContent.delete();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#canExecute()
	 */
	@Override
	public boolean canExecute() {
		return user!=null;
	}

}
