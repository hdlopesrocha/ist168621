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
    private final ObjectId caller;

    /** The group id. */
    private final ObjectId owner;

    /** The group id. */

    private final ObjectId groupId;

    /** The time. */
    private final Date time;
    private final String sid;

    private Long sequence;

    /**
     * Instantiates a new gets the current recording service.
     *
     * @param callerId the caller id
     * @param groupId the group id
     * @param time the time
     */
    public GetCurrentRecordingService(String callerId, String groupId, String owner, String sid, Date time, Long sequence) {
        this.caller = new ObjectId(callerId);
        this.groupId = new ObjectId(groupId);

        System.out.println(owner+"|"+sid);
        this.owner = new ObjectId(owner);
        this.sid = sid;
        this.time = time;
        this.sequence = sequence;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public RecordingChunk dispatch() throws BadRequestException {

        RecordingChunk ans = getResult1();
        if(ans==null){
            // try different session
            ans = getResult2();
        }
        if(ans==null){
            // try group
            ans = getResult3();
        }
        return ans;

    }

    private RecordingChunk getResult1(){
        Document query = new Document("gid", groupId).append("owner",owner).append("sid",sid);
        if(sequence!=null){
            query.append("seq",sequence);
        }else {
            query.append("end", new Document("$gte", time)).append("start", new Document("$lt", time));
        }

        FindIterable<Document> iter = RecordingChunk.getCollection().find(query);
        Document first = iter.first();
        return first != null ? RecordingChunk.load(first) : null;
    }

    private RecordingChunk getResult2(){
        Document query = new Document("gid", groupId).append("owner",owner).append("end", new Document("$gte", time)).append("start", new Document("$lt", time));
        FindIterable<Document> iter = RecordingChunk.getCollection().find(query);
        Document first = iter.first();
        return first != null ? RecordingChunk.load(first) : null;
    }

    private RecordingChunk getResult3(){
        Document query = new Document("gid", groupId).append("owner",groupId).append("end", new Document("$gte", time)).append("start", new Document("$lt", time));
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
