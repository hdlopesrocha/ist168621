package services;

import models.Membership;
import models.User;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc

/**
 * The Class AuthenticateUserService.
 */
public class GetSdpService extends Service<List<Document>> {

    private User user;
    private Membership membership;

    public GetSdpService(String uid, String membershipId) {
        this.user = User.findById(new ObjectId(uid));
        this.membership = Membership.findById(new ObjectId(membershipId));
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public List<Document> dispatch() {
        Document doc = membership.getProperties();
        List<?> list = (List<?>) doc.get("sdps");
        if (list == null) {
            list = new ArrayList<Object>();
        }

        List<Document> ret = new ArrayList<Document>();
        for (Object obj : list) {
            ret.add((Document) obj);
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
        return user != null && membership != null;
    }

}
