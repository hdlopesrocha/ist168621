package services;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import models.Group;
import models.KeyValuePair;
import models.Membership;
import models.User;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class ListGroupMembersService extends Service<List<KeyValuePair<Membership,User>>> {

	private User user;
	private Group group;
	
	public ListGroupMembersService(String uid,String groupId) {
		this.user = User.findById(new ObjectId(uid));
		this.group = Group.findById(new ObjectId(groupId));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public List<KeyValuePair<Membership,User>> dispatch() {
		List<KeyValuePair<Membership,User>> ans = new ArrayList<KeyValuePair<Membership,User>>();
		for(Membership m : Membership.listByGroup(group.getId())){
			ans.add(new KeyValuePair<Membership, User>(m, User.findById(m.getUserId())));
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
