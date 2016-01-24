package services;


import models.HyperContent;
import org.bson.types.ObjectId;



/**
 * The Class DeleteHyperContentService.
 */
public class DeleteHyperContentService extends Service<Void> {

    /** The user. */
    private final ObjectId user;
    
    /** The group. */
    private ObjectId group;
    
    /** The content id. */
    private ObjectId contentId = null;


    /**
     * Instantiates a new delete hyper content service.
     *
     * @param uid the uid
     * @param gid the gid
     * @param cid the cid
     */
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
