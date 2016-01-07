package services;

import exceptions.ServiceException;
import models.Message;
import org.bson.types.ObjectId;

import java.util.List;

// TODO: Auto-generated Javadoc

/**
 * The Class SendMessageService.
 */
public class ListMessagesService extends Service<List<Message>> {

    private final ObjectId target;
    private final Long end;
    private final int len;


    public ListMessagesService(final String target, final Long end, final int len) {
        this.target = new ObjectId(target);
        this.end = end;
        this.len = len;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public List<Message> dispatch() throws ServiceException {
        return Message.listByTarget(target, end, len);
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
