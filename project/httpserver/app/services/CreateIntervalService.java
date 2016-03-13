package services;

import exceptions.ServiceException;
import models.RecordingInterval;
import org.bson.types.ObjectId;

import java.util.Date;


/**
 * The Class CreateIntervalService.
 */
public class CreateIntervalService extends Service<RecordingInterval> {

    /** The Constant LOCK. */
    private static final Object LOCK = new Object();
    
    /** The group id. */
    private final ObjectId groupId;
    
    /** The start. */
    private final Date start;

    /** The inter. */
    private RecordingInterval inter = null;


    /**
     * Instantiates a new creates the interval service.
     *
     * @param groupId the group id
     * @param start the start
     */
    public CreateIntervalService(final String groupId, Date start) {
        this.groupId = new ObjectId(groupId);
        this.start = start;
    }

    /**
     * Gets the interval.
     *
     * @return the interval
     */
    public RecordingInterval getInterval() {
        return inter;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public RecordingInterval dispatch() throws ServiceException {
        synchronized (LOCK) {
            inter = new RecordingInterval(groupId, start, start);
            inter.save();
            return inter;
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
