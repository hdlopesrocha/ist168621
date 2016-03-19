package services;

import com.mongodb.client.FindIterable;
import exceptions.BadRequestException;
import models.RecordingChunk;
import models.User;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Date;



/**
 * The Class GetCurrentRecordingService.
 */
public class GetCurrentRecordingService extends Service<RecordingChunk> {

    /** The caller. */
    private final User caller;
    
    /** The group id. */
    private final ObjectId groupId;

    /** The time. */
    private final Date time;

    private Long sequence;
    private ObjectId interval;

    /**
     * Instantiates a new gets the current recording service.
     *
     * @param callerId the caller id
     * @param groupId the group id
     * @param time the time
     */
    public GetCurrentRecordingService(String callerId, String groupId, ObjectId interval, Date time, Long sequence) {
        this.caller = User.findById(new ObjectId(callerId));
        this.groupId = new ObjectId(groupId);
        this.time = time;
        this.sequence = sequence;
        this.interval = interval;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public RecordingChunk dispatch() throws BadRequestException {
        Document query = new Document("gid", groupId);
        if(sequence!=null && interval!=null){
            query.append("seq",sequence).append("int",interval);
        }else {
            query.append("end", new Document("$gte", time)).append("start", new Document("$lt", time));
        }


        FindIterable<Document> iter = RecordingChunk.getCollection().find(query);

        Document first = iter.first();

        return first != null ? RecordingChunk.load(first) : null;
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
