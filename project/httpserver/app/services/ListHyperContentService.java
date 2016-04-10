package services;

import com.mongodb.client.FindIterable;
import exceptions.BadRequestException;
import models.HyperContent;
import models.User;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;



/**
 * The Class GetCurrentHyperContentService.
 */
public class ListHyperContentService extends Service<List<HyperContent>> {

    /** The Constant PRELOAD_SIZE. */
    private static final int PRELOAD_SIZE = 5;
    
    /** The caller. */
    private final User caller;
    
    /** The group id. */
    private final ObjectId groupId;
    
    /** The time. */
    private final Date time;
    
    /** The has more. */
    private boolean hasMore;

    /**
     * Instantiates a new gets the current hyper content service.
     *
     * @param callerId the caller id
     * @param groupId the group id
     * @param time the time
     */
    public ListHyperContentService(String callerId, String groupId, Date time) {
        this.caller = User.findById(new ObjectId(callerId));
        this.groupId = new ObjectId(groupId);
        this.time = time;

    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public List<HyperContent> dispatch() throws BadRequestException {
        List<HyperContent> ret = HyperContent.listIntersections(groupId,time);
        List<HyperContent> fut = HyperContent.listFutureIntersections(groupId,time,PRELOAD_SIZE);
        hasMore = PRELOAD_SIZE == fut.size();
        ret.addAll(fut);
        return ret;
    }


    /**
     * Checks for more.
     *
     * @return true, if successful
     */
    public boolean hasMore() {
        return hasMore;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {
        return caller != null;
    }

}
