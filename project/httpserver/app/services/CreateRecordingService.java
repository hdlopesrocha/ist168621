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
    private final Date start;
    private final String name;
    private final String type;
    private String url;
    private final Date end;


    public CreateRecordingService(final String url, final String groupId, final String owner,
                                  Date start, Date end, String name, String type) {
        this.groupId = new ObjectId(groupId);
        this.owner = owner != null ? new ObjectId(owner) : null;
        this.start = start;
        this.end = end;
        this.name = name;
        this.type = type;
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



            Recording rec = new Recording(groupId, owner, start, end, name, type, url, Recording.countByGroup(groupId));
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
