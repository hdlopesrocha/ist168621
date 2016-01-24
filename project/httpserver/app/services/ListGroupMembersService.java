package services;

import models.Group;
import models.KeyValuePair;
import models.Membership;
import models.User;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;



/**
 * The Class ListGroupMembersService.
 */
public class ListGroupMembersService extends Service<List<KeyValuePair<Membership, User>>> {

    /** The user. */
    private final User user;
    
    /** The group. */
    private final Group group;

    /**
     * Instantiates a new list group members service.
     *
     * @param uid the uid
     * @param groupId the group id
     */
    public ListGroupMembersService(String uid, String groupId) {
        this.user = User.findById(new ObjectId(uid));
        this.group = Group.findById(new ObjectId(groupId));
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public List<KeyValuePair<Membership, User>> dispatch() {
        List<KeyValuePair<Membership, User>> ans = new ArrayList<KeyValuePair<Membership, User>>();
        for (Membership m : Membership.listByGroup(group.getId())) {
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
        return user != null && group != null;
    }

}
