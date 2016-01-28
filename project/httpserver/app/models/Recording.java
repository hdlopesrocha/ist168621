package models;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import services.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * The Class Recording.
 */
public class Recording {


    /** The collection. */
    private static MongoCollection<Document> collection;
    
    /** The owner. */
    private ObjectId groupId;

    private Document urls;
    /** The end. */
    private Date start, end;

    /** The id. */
    private ObjectId id = null;

    /**
     * Instantiates a new recording.
     */
    private Recording() {

    }

    /**
     * Instantiates a new recording.
     *
     * @param groupId the group id
     * @param start the start
     */
    public Recording(ObjectId groupId, Date start) {
        this.groupId = groupId;
        this.start = start;

        this.urls = new Document();
    }

    /**
     * Gets the collection.
     *
     * @return the collection
     */
    public static MongoCollection<Document> getCollection() {
        if (collection == null)
            collection = Service.getDatabase().getCollection(Recording.class.getName());
        return collection;
    }

    /**
     * Load.
     *
     * @param doc the doc
     * @return the recording
     */
    public static Recording load(Document doc) {
        Recording rec = new Recording();
        rec.id = doc.getObjectId("_id");
        rec.end = doc.getDate("end");
        rec.start = doc.getDate("start");
        rec.groupId = doc.getObjectId("gid");
        rec.urls = (Document) doc.get("urls");
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

    public String getUrl(String owner){
       return urls.getString(owner);
    }

    /**
     * List by group.
     *
     * @param groupId the group id
     * @param sequence the sequence
     * @return the list
     */
    public static List<Recording> listByGroup(ObjectId groupId, long sequence) {
        FindIterable<Document> iter = getCollection()
                .find(new Document("gid", groupId).append("seq", new Document("$gt", sequence)));
        List<Recording> ret = new ArrayList<Recording>();
        for (Document doc : iter) {
            ret.add(Recording.load(doc));
        }
        return ret;
    }

    public synchronized void setUrl(String owner, String url){
       urls.append(owner,url);
    }



    /**
     * Find by id.
     *
     * @param id the id
     * @return the recording
     */
    public static Recording findById(ObjectId id) {
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

}