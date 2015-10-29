package services;

import org.bson.types.ObjectId;

import main.Global;
import models.Group;
import models.Membership;
import models.User;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class CreateGroupService extends Service<Group> {

	private User user;
	private String name;
	
	public CreateGroupService(String uid,String name) {
		this.user = User.findById(new ObjectId(uid));
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public Group dispatch() {
		Group group =new Group(name);
		group.save();
		Membership membership = new Membership(user.getId(),group.getId());
		membership.save();
		Global.manager.getRoom(membership.getId().toString());

		return group;
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
