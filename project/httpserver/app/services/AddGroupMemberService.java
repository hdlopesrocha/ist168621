package services;

import models.Membership;
import org.bson.types.ObjectId;


/**
 * The Class AddGroupMemberService.
 */
public class AddGroupMemberService extends Service<Void> {

    /** The user. */
    private final ObjectId user;
    
    /** The member. */
    private final ObjectId member;
    
    /** The group. */
    private final ObjectId group;

    /**
     * Instantiates a new adds the group member service.
     *
     * @param uid the uid
     * @param groupId the group id
     * @param member the member
     */
    public AddGroupMemberService(String uid, String groupId, String member) {
        this.user = new ObjectId(uid);
        this.group = new ObjectId(groupId);
        this.member = new ObjectId(member);
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public Void dispatch() {
        if (Membership.findByUserGroup(member, group) == null) {
            new Membership(member, group).save();
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
            return Membership.findByUserGroup(user, group) != null;
        }
        return false;
    }

}
