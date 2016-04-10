package services;

import com.mongodb.client.FindIterable;
import exceptions.BadRequestException;
import models.RecordingChunk;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Date;


/**
 * The Class GetCurrentRecordingService.
 */
public class GetNextRecordingService extends Service<RecordingChunk> {

    /** The caller. */
    private final ObjectId caller;

    /** The group id. */
    private final ObjectId owner;

    /** The group id. */

    private final ObjectId groupId;

    /** The time. */
    private final ObjectId recId;
    private final String sid;


    /**
     * Instantiates a new gets the current recording service.
     *
     * @param callerId the caller id
     * @param groupId the group id
     */
    public GetNextRecordingService(String callerId, String groupId, String owner, String sid, ObjectId recId) {
        this.caller = new ObjectId(callerId);
        this.groupId = new ObjectId(groupId);

        System.out.println(owner+"|"+sid);
        this.owner = new ObjectId(owner);
        this.sid = sid;
        this.recId = recId;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public RecordingChunk dispatch() throws BadRequestException {

        RecordingChunk ans = getResult1();

        return ans;

    }

    private RecordingChunk getResult1(){
        Document query = new Document("gid", groupId).append("owner",owner).append("sid",sid).append("_id", new Document("$gt", recId));
        FindIterable<Document> iter = RecordingChunk.getCollection().find(query).limit(1);
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
