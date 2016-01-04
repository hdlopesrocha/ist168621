package services;

import models.Group;
import models.Membership;
import models.User;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc

/**
 * The Class AuthenticateUserService.
 */
public class ListGroupsService extends Service<List<Group>> {

    private User user;

    public ListGroupsService(String uid) {
        this.user = User.findById(new ObjectId(uid));
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public List<Group> dispatch() {
        List<Group> ans = new ArrayList<Group>();
        for (Membership m : Membership.listByUser(user.getId())) {
            ans.add(Group.findById(m.getGroupId()));
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
        return user != null;
    }

}
