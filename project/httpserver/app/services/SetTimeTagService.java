package services;

import exceptions.ServiceException;
import models.TimeAnnotation;
import org.bson.types.ObjectId;

import java.util.Date;


/**
 * The Class CreateTimeTagService.
 */
public class SetTimeTagService extends Service<TimeAnnotation> {

    /** The gid. */
    private final ObjectId gid;
    
    /** The title. */
    private final String title;

    /** The title. */
    private final ObjectId tid;

    /** The time. */
    private final Date time;

    /**
     * Instantiates a new creates the time tag service.
     *
     * @param gid the gid
     * @param time the time
     * @param title the title
     */
    public SetTimeTagService(String gid, String tid, Date time, String title) {
        this.gid = new ObjectId(gid);
        this.time = time;
        this.tid = tid !=null && ObjectId.isValid(tid) ? new ObjectId(tid) : null;
        this.title = title;
    }

    /* (non-Javadoc)
     * @see services.Service#dispatch()
     */
    @Override
    public TimeAnnotation dispatch() throws ServiceException {

        TimeAnnotation tag = null;
        if(tid!=null){
            tag = TimeAnnotation.findById(tid);
        }

        if(tag==null) {
            tag = new TimeAnnotation(gid, time, title);
        }else {
            tag.setTime(time);
        }
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
