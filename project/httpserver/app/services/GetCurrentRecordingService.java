package services;

import com.mongodb.client.FindIterable;
import exceptions.BadRequestException;
import models.Recording;
import models.User;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Date;



/**
 * The Class GetCurrentRecordingService.
 */
public class GetCurrentRecordingService extends Service<Recording> {

    /** The caller. */
    private final User caller;
    
    /** The group id. */
    private final ObjectId groupId;

    /** The time. */
    private final Date time;

    /**
     * Instantiates a new gets the current recording service.
     *
     * @param callerId the caller id
     * @param groupId the group id
     * @param time the time
     */
    public GetCurrentRecordingService(String callerId, String groupId, Date time) {
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
    public Recording dispatch() throws BadRequestException {

        FindIterable<Document> iter = Recording.getCollection().find(new Document("gid", groupId).append("end", new Document("$gte", time)).append("start", new Document("$lt", time)));

        Document first = iter.first();

        return first != null ? Recording.load(first) : null;
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
