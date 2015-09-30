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
public class SearchGroupCandidatesService extends Service<List<User>> {

	private User user;
	private Group group;
	private String query;

	public SearchGroupCandidatesService(String userId, String groupId, String query) {
		this.user = User.findById(new ObjectId(userId));
		this.group = Group.findById(new ObjectId(groupId));
		this.query = query;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public List<User> dispatch() {
		List<User> ans = new ArrayList<User>();
		for (User u : user.getRelations()) {
			if (u.getEmail().contains(query) && Membership.findByUserGroup(u.getId(), group.getId()) == null) {
				ans.add(u);
			}
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
		return user != null && group != null;
	}

}
