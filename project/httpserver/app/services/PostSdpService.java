package services;

import models.Membership;
import models.User;
import org.bson.Document;
import org.bson.types.ObjectId;

// TODO: Auto-generated Javadoc

/**
 * The Class AuthenticateUserService.
 */
public class PostSdpService extends Service<Void> {

    private final User user;
    private final Membership membership;
    private final String data;

    public PostSdpService(String uid, String groupId, String data) {
        this.user = User.findById(new ObjectId(uid));
        this.membership = Membership.findByUserGroup(user.getId(), new ObjectId(groupId));
        this.data = data;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public Void dispatch() {

        Document obj = Document.parse(data);
        Document doc = membership.getProperties();
        doc.put("sdp", obj);
        membership.save();


        return null;
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
