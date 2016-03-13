package services;

import models.Group;
import models.KeyValuePair;
import models.GroupMembership;
import models.User;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;



/**
 * The Class ListGroupMembersService.
 */
public class ListGroupMembersService extends Service<List<KeyValuePair<GroupMembership, User>>> {

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
    public List<KeyValuePair<GroupMembership, User>> dispatch() {
        List<KeyValuePair<GroupMembership, User>> ans = new ArrayList<KeyValuePair<GroupMembership, User>>();
        for (GroupMembership m : GroupMembership.listByGroup(group.getId())) {
            ans.add(new KeyValuePair<GroupMembership, User>(m, User.findById(m.getUserId())));
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
