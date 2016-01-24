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
 * The Class HyperContent.
 */
public class HyperContent {

    /** The collection. */
    private static MongoCollection<Document> collection;
    
    /** The end. */
    private Date start, end;
    
    /** The content. */
    private String content = null;
    
    /** The group id. */
    private ObjectId id, groupId = null;

    /**
     * Instantiates a new hyper content.
     *
     * @param gid the gid
     * @param start the start
     * @param end the end
     * @param content the content
     */
    public HyperContent(ObjectId gid, Date start, Date end, String content) {
        this.start = start;
        this.end = end;
        this.groupId = gid;
        this.content = content;
    }

    /**
     * Instantiates a new hyper content.
     */
    private HyperContent() {
    }

    /**
     * Gets the collection.
     *
     * @return the collection
     */
    public static MongoCollection<Document> getCollection() {
        if (collection == null)
            collection = Service.getDatabase().getCollection(HyperContent.class.getName());
        return collection;
    }

    /**
     * Load.
     *
     * @param doc the doc
     * @return the hyper content
     */
    public static HyperContent load(Document doc) {
        HyperContent content = new HyperContent();
        content.id = doc.getObjectId("_id");
        content.start = doc.getDate("start");
        content.end = doc.getDate("end");
        content.groupId = doc.getObjectId("gid");
        content.content = doc.getString("content");

        return content;
    }

    /**
     * Find by id.
     *
     * @param id the id
     * @return the hyper content
     */
    public static HyperContent findById(ObjectId id) {
        Document doc = new Document("_id", id);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? load(doc) : null;
    }

    /**
     * List all.
     *
     * @return the list
     */
    public static List<HyperContent> listAll() {
        FindIterable<Document> iter = getCollection().find(new Document());
        List<HyperContent> ret = new ArrayList<HyperContent>();
        for (Document doc : iter) {
            ret.add(load(doc));
        }
        return ret;
    }

    /**
     * Search.
     *
     * @param gid the gid
     * @param query the query
     * @return the list
     */
    public static List<HyperContent> search(ObjectId gid, String query) {
        Pattern regex = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
        Document doc = new Document("gid", gid).append("content", regex);
        FindIterable<Document> iter = getCollection().find(doc);
        Iterator<Document> i = iter.iterator();
        List<HyperContent> ret = new ArrayList<HyperContent>();
        while (i.hasNext()) {
            ret.add(HyperContent.load(i.next()));
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

        doc.put("start", start);
        doc.put("end", end);
        doc.put("gid", groupId);
        doc.put("content", content);

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
     * Gets the group id.
     *
     * @return the group id
     */
    public ObjectId getGroupId() {
        return groupId;
    }

    /**
     * Gets the content.
     *
     * @return the content
     */
    public String getContent() {
        return content;
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
        if (id != null)
            getCollection().deleteOne(new Document("_id", id));
    }

    /**
     * To json.
     *
     * @return the JSON object
     */
    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        obj.put("id", id.toString());
        obj.put("content", content);
        obj.put("time", Tools.FORMAT.format(start));
        return obj;
    }
}