package services;

import com.mongodb.client.FindIterable;
import exceptions.BadRequestException;
import models.RecordingChunk;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


/**
 * The Class GetCurrentRecordingService.
 */
public class ListCurrentRecordingsService extends Service<List<RecordingChunk>> {

    /** The caller. */
    private final ObjectId caller;

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
    public ListCurrentRecordingsService(String callerId, String groupId, Date time) {
        this.caller = new ObjectId(callerId);
        this.groupId = new ObjectId(groupId);
        this.time = time;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public List<RecordingChunk> dispatch() throws BadRequestException {
        List<RecordingChunk> ans = new ArrayList<>();
        Document query = new Document("gid", groupId).append("start", new Document("$lt", time)).append("end", new Document("$gte", time));


        FindIterable<Document> findIter = RecordingChunk.getCollection().find(query);
        Iterator<Document> iter = findIter.iterator();
        while (iter.hasNext()){
            Document doc = iter.next();
            ans.add(RecordingChunk.load(doc));

        }



        return ans;
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
