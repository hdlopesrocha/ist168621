package services;

import org.bson.types.ObjectId;

import models.Group;
import models.Membership;
import models.User;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class RemoveGroupMemberService extends Service<Void> {

	private User user, member;
	private Group group;

	public RemoveGroupMemberService(String email, String groupId, String memberId) {
		this.user = User.findByEmail(email);
		this.group = Group.findById(new ObjectId(groupId));
		this.member = User.findById(new ObjectId(memberId));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public Void dispatch() {
		Membership m =Membership.findByUserGroup(member.getId(), group.getId()); 
		if(m!=null){
			m.delete();
		}
		if(Membership.listByGroup(group.getId()).size()==0){
			group.delete();
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
