package services;

import models.IdentityProfile;
import models.PublicProfile;
import models.User;
import org.bson.types.ObjectId;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO: Auto-generated Javadoc

/**
 * The Class AuthenticateUserService.
 */
public class SearchUserService extends Service<Set<ObjectId>> {

    private User caller;
    private String query;


    public SearchUserService(String uid, String query) {
        this.caller = User.findById(new ObjectId(uid));
        this.query = query;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public Set<ObjectId> dispatch() {
        List<PublicProfile> publics = PublicProfile.search("name", query);
        List<IdentityProfile> identities = IdentityProfile.search("email", query);
        Set<ObjectId> ret = new HashSet<ObjectId>();
        for (PublicProfile p : publics) {
            ret.add(p.getOwner());
        }
        for (IdentityProfile p : identities) {
            ret.add(p.getOwner());
        }

        return ret;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {
        return caller != null;
    }

}
