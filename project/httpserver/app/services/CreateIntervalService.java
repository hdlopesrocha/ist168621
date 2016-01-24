package services;

import exceptions.ServiceException;
import models.Interval;
import org.bson.types.ObjectId;

import java.util.Date;

/**
 * The Class SendMessageService.
 */
public class CreateIntervalService extends Service<Interval> {

    private static final Object LOCK = new Object();
    private final ObjectId groupId;
    private final Date start;

    private Interval inter = null;


    public CreateIntervalService(final String groupId, Date start) {
        this.groupId = new ObjectId(groupId);
        this.start = start;
    }

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
