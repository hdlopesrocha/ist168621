package models;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import dtos.KeyValue;
import org.bson.Document;
import org.bson.types.ObjectId;
import services.Service;

import javax.print.Doc;
import java.util.*;


/**
 * The Class Recording.
 */
public class RecordingChunk {


    /** The collection. */
    private static MongoCollection<Document> collection;
    
    /** The owner. */
    private ObjectId groupId;

    private List<Document> urls;
    /** The end. */
    private Date start, end;

    private Long sequence = null;

    private ObjectId interval;
    /** The id. */
    private ObjectId id = null;

    /**
     * Instantiates a new recording.
     */
    private RecordingChunk() {

    }



    /**
     * Instantiates a new recording.
     *
     * @param groupId the group id
     * @param start the start
     */
    public RecordingChunk(ObjectId groupId, ObjectId interval, Date start, Long sequence) {
        this.groupId = groupId;
        this.start = start;
        this.sequence = sequence;
        this.urls = new ArrayList<Document>();
        this.interval = interval;
    }

    /**
     * Gets the collection.
     *
     * @return the collection
     */
    public static MongoCollection<Document> getCollection() {
        if (collection == null)
            collection = Service.getDatabase().getCollection(RecordingChunk.class.getName());
        return collection;
    }

    /**
     * Load.
     *
     * @param doc the doc
     * @return the recording
     */
    public static RecordingChunk load(Document doc) {
        RecordingChunk rec = new RecordingChunk();
        rec.id = doc.getObjectId("_id");
        rec.sequence = doc.getLong("seq");
        rec.interval = doc.getObjectId("int");

        rec.end = doc.getDate("end");
        rec.start = doc.getDate("start");
        rec.groupId = doc.getObjectId("gid");
        rec.urls = (List<Document>) doc.get("urls");
        return rec;
    }

    /**
     * Count by group.
     *
     * @param owner the owner
     * @return the long
     */
    public static long countByGroup(ObjectId owner) {
        Document doc = new Document("gid", owner);
        return getCollection().count(doc);
    }


    public List<RecordingUrl> getUrls(){
        List<RecordingUrl> ans = new ArrayList<RecordingUrl>();
        for(Document doc : urls){
            RecordingUrl ru = new RecordingUrl(doc);
            ans.add(ru);
        }

        return ans;
    }


    public List<RecordingUrl> getUrl(String owner, String sid){
        List<RecordingUrl> ans = new ArrayList<RecordingUrl>();
        for(Document doc : urls){
            RecordingUrl ru = new RecordingUrl(doc);
            if(ru.getUid()!=null && ru.getUid().equals(owner)){
                if(ru.getSid().equals(sid)) {
                    ans.add(0, ru);
                }else {
                    ans.add(ru);
                }
            }
        }

        return ans;
    }

    /**
     * List by group.
     *
     * @param groupId the group id
     * @param sequence the sequence
     * @return the list
     */
    public static List<RecordingChunk> listByGroup(ObjectId groupId, long sequence) {
        FindIterable<Document> iter = getCollection()
                .find(new Document("gid", groupId).append("seq", new Document("$gt", sequence)));
        List<RecordingChunk> ret = new ArrayList<RecordingChunk>();
        for (Document doc : iter) {
            ret.add(RecordingChunk.load(doc));
        }
        return ret;
    }

    public synchronized void setUrl(RecordingUrl ru){
       urls.add(ru.toDocument());
    }



    /**
     * Find by id.
     *
     * @param id the id
     * @return the recording
     */
    public static RecordingChunk findById(ObjectId id) {
        Document doc = new Document("_id", id);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? load(doc) : null;
    }

    /**
     * Save.
     */
    public synchronized void save() {
        Document doc = new Document();
        if (id != null)
            doc.put("_id", id);

        doc.put("gid", groupId);
        doc.put("start", start);
        doc.put("end", end);
        doc.put("urls", urls);
        doc.put("seq",sequence);
        doc.put("int",interval);

        if (id == null)
            getCollection().insertOne(doc);
        else
            getCollection().replaceOne(new Document("_id", id), doc);

        id = doc.getObjectId("_id");

    }

    /**
     * Gets the start.
     *
     * @return the start
     */
    public Date getStart() {
        return start;
    }


    /**
     * Gets the end.
     *
     * @return the end
     */
    public Date getEnd() {
        return end;
    }

    /**
     * Sets the end.
     *
     * @param end the new end
     */
    public void setEnd(Date end) {
        this.end = end;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public ObjectId getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(ObjectId id) {
        this.id = id;
    }

    /**
     * Delete.
     */
    public void delete() {
        if (id != null) {
            getCollection().deleteOne(new Document("_id", id));
        }
    }

    public Long getSequence() {
        return sequence;
    }

    public ObjectId getInterval() {
        return interval;
    }
}