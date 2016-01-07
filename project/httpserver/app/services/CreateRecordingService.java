package services;

import exceptions.ServiceException;
import models.Interval;
import models.Recording;
import org.bson.types.ObjectId;

import java.util.Date;

// TODO: Auto-generated Javadoc

/**
 * The Class SendMessageService.
 */
public class CreateRecordingService extends Service<Recording> {

    private static final Object LOCK = new Object();
    private final ObjectId groupId;
    private final ObjectId owner;
    private ObjectId interval;
    private final Date start;
    private final String name;
    private final String type;
    private String url;
    private Interval inter = null;
    private final Date end;


    public CreateRecordingService(final String url, final String groupId, final String owner,
                                  Date start, Date end, String name, String type, String interval) {
        this.groupId = new ObjectId(groupId);
        this.owner = owner != null ? new ObjectId(owner) : null;
        this.interval = interval != null ? new ObjectId(interval) : null;
        this.start = start;
        this.end = end;
        this.name = name;
        this.type = type;
        this.url = url;
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
    public Recording dispatch() throws ServiceException {
        synchronized (LOCK) {

            if (interval == null) {
                inter = new Interval(groupId, start, end);
                inter.save();
                interval = inter.getId();
            } else {
                inter = Interval.findById(interval);
                if (inter == null) {
                    return null;
                } else if (inter.getStart() == null) {
                    inter.setStart(start);
                }
                inter.setEnd(end);
                inter.save();
            }

            Recording rec = new Recording(groupId, owner, start, end, name, type, url, Recording.countByGroup(groupId),
                    inter.getId());
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
