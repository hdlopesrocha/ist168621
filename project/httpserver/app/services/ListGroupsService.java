package services;

import models.Group;
import models.GroupMembership;
import models.Relation;
import models.User;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



/**
 * The Class ListGroupsService.
 */
public class ListGroupsService extends Service<List<Group>> {

    /** The user. */
    private final User user;

    /**
     * Instantiates a new list groups service.
     *
     * @param uid the uid
     */
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
        Set<ObjectId> uniqueGroups = new HashSet<ObjectId>();

        List<Group> ans = new ArrayList<Group>();
        for (GroupMembership m : GroupMembership.listByUser(user.getId())) {
            Group g = Group.findById(m.getGroupId());
            ans.add(g);
            uniqueGroups.add(g.getId());
        }

        for (Relation relA : Relation.listFrom(user.getId())) {
            Relation relB = Relation.findByEndpoint(relA.getTo(), relA.getFrom());
            if (relB != null) {
                for (GroupMembership m : GroupMembership.listByUser(relA.getTo())) {
                    Group g = Group.findById(m.getGroupId());
                    if (g.getVisibility().equals(Group.Visibility.PUBLIC)) {
                        if (!uniqueGroups.contains(g.getId())) {
                            uniqueGroups.add(g.getId());
                            ans.add(g);
                        }
                    }
                }
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
        return user != null;
    }

}
