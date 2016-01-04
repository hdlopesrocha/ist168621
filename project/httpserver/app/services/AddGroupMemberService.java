package services;

import models.Membership;
import org.bson.types.ObjectId;

// TODO: Auto-generated Javadoc

/**
 * The Class AuthenticateUserService.
 */
public class AddGroupMemberService extends Service<Void> {

    private ObjectId user, member;
    private ObjectId group;

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
