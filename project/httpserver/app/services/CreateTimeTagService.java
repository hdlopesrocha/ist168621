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
    
    /** The content. */
    private final String content;
    
    /** The time. */
    private final Date time;

    /**
     * Instantiates a new creates the time tag service.
     *
     * @param gid the gid
     * @param time the time
     * @param title the title
     * @param content the content
     */
    public CreateTimeTagService(String gid, Date time, String title, String content) {
        this.gid = new ObjectId(gid);
        this.time = time;
        this.title = title;
        this.content = content;
    }

    /* (non-Javadoc)
     * @see services.Service#dispatch()
     */
    @Override
    public TimeTag dispatch() throws ServiceException {
        TimeTag tag = new TimeTag(gid, time, title, content);
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
