package services;

import exceptions.ServiceException;
import models.Interval;
import org.bson.types.ObjectId;

import java.util.Date;


/**
 * The Class CreateIntervalService.
 */
public class CreateIntervalService extends Service<Interval> {

    /** The Constant LOCK. */
    private static final Object LOCK = new Object();
    
    /** The group id. */
    private final ObjectId groupId;
    
    /** The start. */
    private final Date start;

    /** The inter. */
    private Interval inter = null;


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
    public Interval getInterval() {
        return inter;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public Interval dispatch() throws ServiceException {
        synchronized (LOCK) {
            inter = new Interval(groupId, start, start);
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
