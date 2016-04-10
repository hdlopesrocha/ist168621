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

    /** The owner. */
    private ObjectId owner;
    private String url;
    private String sessionId;

    /** The end. */
    private Date start, end;


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
    public RecordingChunk(ObjectId groupId, ObjectId owner, Date start, Date end, String sid, String url) {
        this.groupId = groupId;
        this.owner = owner;
        this.start = start;
        this.end = end;
        this.url = url;
        this.sessionId = sid;
    }

    /**
     * Gets the collection.
     *
     * @return the collection
     */
    public static MongoCollection<Document> getCollection() {
        if (collection == null) {
            collection = Service.getDatabase().getCollection(RecordingChunk.class.getName());
            collection.createIndex(new Document("gid",1));
        }
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
        rec.owner = doc.getObjectId("owner");
        rec.end = doc.getDate("end");
        rec.start = doc.getDate("start");
        rec.groupId = doc.getObjectId("gid");
        rec.url = doc.getString("url");
        rec.sessionId = doc.getString("sid");
        return rec;
    }

    /**
     * Count by group.
     *
     * @param owner the owner
     * @return the long
     */
    public static long countByGroup(ObjectId groupId , ObjectId owner, String sid) {
        Document doc = new Document("gid", groupId).append("owner",owner).append("sid",sid);
        return getCollection().count(doc);
    }

/*
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


    public synchronized void setUrl(RecordingUrl ru){
       urls.add(ru.toDocument());
    }

*/


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
        doc.put("owner", owner);
        doc.put("gid", groupId);
        doc.put("start", start);
        doc.put("end", end);
        doc.put("url",url);
        doc.put("sid",sessionId);


        if (id == null) {
            getCollection().insertOne(doc);
        }else {
            getCollection().replaceOne(new Document("_id", id), doc);
        }
        id = doc.getObjectId("_id");

    }

    public boolean contains(Date date){
        return start.getTime()<date.getTime() && date.getTime() < end.getTime();
    }

    /**
     * Gets the start.
     *
     * @return the start
     */
    public Date getStart() {
        return start;
    }


    public String getUrl() {
        return url;
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

    public String getSid() {
        return sessionId;
    }

    public ObjectId getOwner() {
        return owner;
    }
}