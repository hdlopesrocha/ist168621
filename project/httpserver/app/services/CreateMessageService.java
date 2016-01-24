package services;

import exceptions.ServiceException;
import models.Message;
import org.bson.types.ObjectId;

import java.util.Date;



/**
 * The Class CreateMessageService.
 */
public class CreateMessageService extends Service<Message> {

    /** The target. */
    private final ObjectId target;
    
    /** The source. */
    private final ObjectId source;
    
    /** The time. */
    private final Date time;
    
    /** The text. */
    private final String text;

    /**
     * Instantiates a new creates the message service.
     *
     * @param source the source
     * @param target the target
     * @param text the text
     */
    public CreateMessageService(final String source, final String target, final String text) {
        this.target = new ObjectId(target);
        this.source = new ObjectId(source);
        this.text = text;
        this.time = new Date();
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public Message dispatch() throws ServiceException {
        Message msg = new Message(target, source, time, text);
        msg.save();
        return msg;
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
