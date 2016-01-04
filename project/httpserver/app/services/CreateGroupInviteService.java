package services;

import exceptions.ServiceException;
import models.Group;
import org.bson.types.ObjectId;


// TODO: Auto-generated Javadoc

/**
 * The Class AuthenticateUserService.
 */
public class CreateGroupInviteService extends Service<String> {

    private ObjectId caller;
    private ObjectId groupId;

    public CreateGroupInviteService(final String caller, final String groupId) {
        this.caller = new ObjectId(caller);
        this.groupId = new ObjectId(groupId);

    }


    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public String dispatch() throws ServiceException {
        Group group = Group.findById(groupId);
        if (group != null) {
            group.generateInvite();
            group.save();
            return group.getInvite();
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
        return caller != null && groupId != null;
    }

}
