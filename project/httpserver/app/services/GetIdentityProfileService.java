package services;

import models.IdentityProfile;
import models.Permission;
import org.bson.types.ObjectId;


/**
 * The Class AuthenticateUserService.
 */
public class GetIdentityProfileService extends Service<IdentityProfile> {

    private ObjectId user, caller;

    public GetIdentityProfileService(String caller, String userId) {
        this.user = new ObjectId(userId);
        this.caller = new ObjectId(caller);
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public IdentityProfile dispatch() {
        return IdentityProfile.findByOwner(user);
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {
        return (user != null && user == caller) || Permission.find(caller, Permission.PERMISSION_ADMIN, null) != null;
    }

}