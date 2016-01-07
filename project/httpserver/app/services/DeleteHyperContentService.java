package services;


import models.HyperContent;
import org.bson.types.ObjectId;

// TODO: Auto-generated Javadoc

/**
 * The Class AuthenticateUserService.
 */
public class DeleteHyperContentService extends Service<Void> {

    private final ObjectId user;
    private ObjectId group;
    private ObjectId contentId = null;


    public DeleteHyperContentService(String uid, String gid, String cid) {
        this.user = new ObjectId(uid);
        this.group = new ObjectId(gid);
        this.contentId = new ObjectId(cid);

    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public Void dispatch() {
        HyperContent hyperContent = HyperContent.findById(contentId);
        hyperContent.delete();
        return null;
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
