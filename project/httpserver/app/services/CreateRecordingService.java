package services;

import exceptions.ServiceException;
import models.Recording;
import org.bson.types.ObjectId;

import java.util.Date;



/**
 * The Class CreateRecordingService.
 */
public class CreateRecordingService extends Service<Recording> {

    /** The Constant LOCK. */
    private static final Object LOCK = new Object();
    
    /** The group id. */
    private final ObjectId groupId;
    
    /** The owner. */
    private final ObjectId owner;
    
    /** The start. */
    private final Date start;
    
    /** The end. */
    private final Date end;
    
    /** The url. */
    private String url;


    /**
     * Instantiates a new creates the recording service.
     *
     * @param url the url
     * @param groupId the group id
     * @param owner the owner
     * @param start the start
     * @param end the end
     */
    public CreateRecordingService(final String url, final String groupId, final String owner,
                                  Date start, Date end) {
        this.groupId = new ObjectId(groupId);
        this.owner = owner != null ? new ObjectId(owner) : null;
        this.start = start;
        this.end = end;
        this.url = url;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public Recording dispatch() throws ServiceException {
        synchronized (LOCK) {
            Recording rec = new Recording(groupId, owner, start, end, url);
            rec.save();
            return rec;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {
        return true;
    }

}
