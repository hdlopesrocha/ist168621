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
 * The Class Interval.
 */
public class Interval {

    /** The collection. */
    private static MongoCollection<Document> collection;
    
    /** The end. */
    private Date start, end;
    
    /** The gid. */
    private ObjectId id, gid = null;

    /**
     * Instantiates a new interval.
     */
    private Interval() {

    }

    /**
     * Instantiates a new interval.
     *
     * @param gid the gid
     */
    public Interval(ObjectId gid) {
        this.gid = gid;

    }

    /**
     * Instantiates a new interval.
     *
     * @param gid the gid
     * @param start the start
     * @param end the end
     */
    public Interval(ObjectId gid, Date start, Date end) {
        this.start = start;
        this.gid = gid;
        this.end = end;
    }

    /**
     * Gets the collection.
     *
     * @return the collection
     */
    private static MongoCollection<Document> getCollection() {
        if (collection == null)
            collection = Service.getDatabase().getCollection(Interval.class.getName());
        return collection;
    }

    /**
     * Load.
     *
     * @param doc the doc
     * @return the interval
     */
    private static Interval load(Document doc) {
        Interval rec = new Interval();
        rec.id = doc.getObjectId("_id");
        rec.end = doc.getDate("end");
        rec.gid = doc.getObjectId("gid");
        rec.start = doc.getDate("start");
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

    /**
     * List by group.
     *
     * @param groupId the group id
     * @return the list
     */
    public static List<Interval> listByGroup(ObjectId groupId) {
        FindIterable<Document> iter = getCollection()
                .find(new Document("gid", groupId));
        List<Interval> ret = new ArrayList<Interval>();
        for (Document doc : iter) {
            ret.add(Interval.load(doc));
        }
        return ret;
    }

    /**
     * Find by id.
     *
     * @param id the id
     * @return the interval
     */
    public static Interval findById(ObjectId id) {
        Document doc = new Document("_id", id);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? load(doc) : null;
    }

    /**
     * Save.
     */
    public void save() {
        Document doc = new Document();
        if (id != null)
            doc.put("_id", id);

        doc.put("gid", gid);
        doc.put("start", start);
        doc.put("end", end);

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
     * Sets the start.
     *
     * @param start the new start
     */
    public void setStart(Date start) {
        this.start = start;
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