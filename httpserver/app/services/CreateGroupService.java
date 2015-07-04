package services;

import models.Group;
import models.Membership;
import models.Relation;
import models.User;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class CreateGroupService extends Service<Void> {

	private User user;
	private String name;
	
	public CreateGroupService(String email,String name) {
		this.user = User.findByEmail(email);
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public Void dispatch() {
		Group group =new Group(name);
		group.save();
		Membership membership = new Membership(user.getId(),group.getId());
		membership.save();
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
