package services;

import exceptions.ServiceException;
import models.Group;
import models.Membership;
import org.bson.types.ObjectId;



/**
 * The Class JoinGroupInviteService.
 */
public class JoinGroupInviteService extends Service<Boolean> {

    /** The caller. */
    private final ObjectId caller;
    
    /** The group id. */
    private final ObjectId groupId;
    
    /** The token. */
    private final String token;

    /**
     * Instantiates a new join group invite service.
     *
     * @param caller the caller
     * @param groupId the group id
     * @param token the token
     */
    public JoinGroupInviteService(final String caller, final String groupId, final String token) {
        this.caller = new ObjectId(caller);
        this.groupId = new ObjectId(groupId);
        this.token = token;
    }


    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public Boolean dispatch() throws ServiceException {
        boolean ans = false;
        Group group = Group.findById(groupId);
        if (group != null) {
            ans = group.matchInvite(token);
            if (ans) {
                if (Membership.findByUserGroup(caller, groupId) == null) {
                    new Membership(caller, groupId).save();
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
        return caller != null && groupId != null;
    }

}
