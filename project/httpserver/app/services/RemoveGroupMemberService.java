package services;

import models.Group;
import models.GroupMembership;
import models.User;
import org.bson.types.ObjectId;



/**
 * The Class RemoveGroupMemberService.
 */
public class RemoveGroupMemberService extends Service<Void> {

    /** The user. */
    private final User user;
    
    /** The member. */
    private final User member;
    
    /** The group. */
    private final Group group;

    /**
     * Instantiates a new removes the group member service.
     *
     * @param callerId the caller id
     * @param groupId the group id
     * @param memberId the member id
     */
    public RemoveGroupMemberService(String callerId, String groupId, String memberId) {
        this.user = User.findById(new ObjectId(callerId));
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
        GroupMembership m = GroupMembership.findByUserGroup(member.getId(), group.getId());
        if (m != null) {
            m.delete();
        }
        if (GroupMembership.listByGroup(group.getId()).size() == 0) {
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
        boolean ret = user != null && group != null && member != null;
        if (ret) {
            return GroupMembership.findByUserGroup(user.getId(), group.getId()) != null;
        }

        return false;

    }

}
