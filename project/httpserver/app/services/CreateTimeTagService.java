package services;

import exceptions.ServiceException;
import models.TimeTag;
import org.bson.types.ObjectId;

import java.util.Date;


/**
 * The Class CreateTimeTagService.
 */
public class CreateTimeTagService extends Service<TimeTag> {

    /** The gid. */
    private final ObjectId gid;
    
    /** The title. */
    private final String title;

    /** The time. */
    private final Date time;

    /**
     * Instantiates a new creates the time tag service.
     *
     * @param gid the gid
     * @param time the time
     * @param title the title
     */
    public CreateTimeTagService(String gid, Date time, String title) {
        this.gid = new ObjectId(gid);
        this.time = time;
        this.title = title;
    }

    /* (non-Javadoc)
     * @see services.Service#dispatch()
     */
    @Override
    public TimeTag dispatch() throws ServiceException {
        TimeTag tag = new TimeTag(gid, time, title);
        tag.save();
        return tag;
    }

    /* (non-Javadoc)
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {
        return true;
    }

}
