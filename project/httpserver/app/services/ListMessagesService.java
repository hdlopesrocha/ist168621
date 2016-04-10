package services;

import exceptions.ServiceException;
import models.Message;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;



/**
 * The Class ListMessagesService.
 */
public class ListMessagesService extends Service<List<Message>> {

    /** The target. */
    private final ObjectId target;
    
    /** The end. */
    private final Long ts;
    
    /** The len. */
    private final int len;


    /**
     * Instantiates a new list messages service.
     *
     * @param target the target
     * @param ts the end
     * @param len the len
     */
    public ListMessagesService(final String target, final Long ts, final int len) {
        this.target = new ObjectId(target);
        this.ts = ts;
        this.len = len;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public List<Message> dispatch() throws ServiceException {
        return Message.listByTarget(target, ts, len);
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
