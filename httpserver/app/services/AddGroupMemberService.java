package services;

import org.bson.types.ObjectId;

import models.Group;
import models.Membership;
import models.User;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class AddGroupMemberService extends Service<Void> {

	private User user, member;
	private Group group;

	public AddGroupMemberService(String email, String groupId, String member) {
		this.user = User.findByEmail(email);
		this.group = Group.findById(new ObjectId(groupId));
		this.member = User.findById(new ObjectId(member));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public Void dispatch() {
		if(Membership.findByUserGroup(member.getId(), group.getId())==null){
			new Membership(member.getId(), group.getId()).save();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#canExecute()
	 */
	@Override
	public boolean canExecute() {
		boolean ret = user != null && group != null && member!=null;
		if(ret){
			return Membership.findByUserGroup(user.getId(), group.getId()) !=null;			
		}
		
		return false;
		
	}

}
