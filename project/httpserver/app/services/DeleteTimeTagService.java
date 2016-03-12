package services;

import exceptions.ServiceException;
import models.TimeTag;
import org.bson.types.ObjectId;


/**
 * The Class CreateTimeTagService.
 */
public class DeleteTimeTagService extends Service<Void> {

    /** The gid. */
    private final ObjectId gid;


    /** The content. */
    private final String tagId;

    /**
     * Instantiates a new creates the time tag service.
     *
     * @param gid the gid
     */
    public DeleteTimeTagService(String gid, String tagId) {
        this.gid = new ObjectId(gid);
        this.tagId = tagId;
    }

    /* (non-Javadoc)
     * @see services.Service#dispatch()
     */
    @Override
    public Void dispatch() throws ServiceException {
        ObjectId tid = ObjectId.isValid(tagId) ? new ObjectId(tagId):null;
        if(tid!=null) {
            TimeTag tag = TimeTag.findById(tid);
            tag.delete();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {
        return true;
    }

}
