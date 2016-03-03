package models;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import main.Tools;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import services.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;


/**
 * The Class TimeTag.
 */
public class TimeTag {

    /** The collection. */
    private static MongoCollection<Document> collection;
    
    /** The time. */
    private Date time;
    
    /** The title. */
    private String title;

    /** The gid. */
    private ObjectId id, gid = null;

    /**
     * Instantiates a new time tag.
     */
    private TimeTag() {

    }

    /**
     * Instantiates a new time tag.
     *
     * @param gid the gid
     * @param time the time
     * @param title the title
     */
    public TimeTag(ObjectId gid, Date time, String title) {
        this.time = time;
        this.gid = gid;
        this.title = title;
    }

    /**
     * Gets the collection.
     *
     * @return the collection
     */
    public static MongoCollection<Document> getCollection() {
        if (collection == null)
            collection = Service.getDatabase().getCollection(TimeTag.class.getName());
        return collection;
    }

    /**
     * Load.
     *
     * @param doc the doc
     * @return the time tag
     */
    public static TimeTag load(Document doc) {
        TimeTag rec = new TimeTag();
        rec.id = doc.getObjectId("_id");
        rec.time = doc.getDate("time");
        rec.gid = doc.getObjectId("gid");
        rec.title = doc.getString("title");
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
    public static List<TimeTag> listByGroup(ObjectId groupId) {
        FindIterable<Document> iter = getCollection()
                .find(new Document("gid", groupId));
        List<TimeTag> ret = new ArrayList<TimeTag>();
        for (Document doc : iter) {
            ret.add(TimeTag.load(doc));
        }
        return ret;
    }

    /**
     * Find by id.
     *
     * @param id the id
     * @return the time tag
     */
    public static TimeTag findById(ObjectId id) {
        Document doc = new Document("_id", id);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? load(doc) : null;
    }

    /**
     * Search.
     *
     * @param gid the gid
     * @param query the query
     * @return the list
     */
    public static List<TimeTag> search(ObjectId gid, String query) {
        Pattern regex = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
        Document doc = new Document("gid", gid).append("title", regex);
        FindIterable<Document> iter = getCollection().find(doc);
        Iterator<Document> i = iter.iterator();
        List<TimeTag> ret = new ArrayList<TimeTag>();
        while (i.hasNext()) {
            ret.add(TimeTag.load(i.next()));
        }
        return ret;
    }

    /**
     * Save.
     */
    public void save() {
        Document doc = new Document();
        if (id != null)
            doc.put("_id", id);

        doc.put("gid", gid);
        doc.put("time", time);
        doc.put("title", title);

        if (id == null)
            getCollection().insertOne(doc);
        else
            getCollection().replaceOne(new Document("_id", id), doc);

        id = doc.getObjectId("_id");

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

    /**
     * To json.
     *
     * @return the JSON object
     */
    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        obj.put("id", id.toString());
        obj.put("title", title);
        obj.put("time", Tools.FORMAT.format(time));
        return obj;
    }

}