package services;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import models.Group;
import models.Membership;
import models.User;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class ListGroupMembersService extends Service<List<User>> {

	private User user;
	private Group group;
	
	public ListGroupMembersService(String email,String groupId) {
		this.user = User.findByEmail(email);
		this.group = Group.findById(new ObjectId(groupId));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public List<User> dispatch() {
		List<User> ans = new ArrayList<User>();
		for(Membership m : Membership.listByGroup(group.getId())){
			ans.add(User.findById(m.getUserId()));
		}
		return ans;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#canExecute()
	 */
	@Override
	public boolean canExecute() {
		return user!=null && group!=null;
	}

}
