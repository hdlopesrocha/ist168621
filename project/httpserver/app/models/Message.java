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
import java.util.List;


/**
 * The Class Message.
 */
public class Message {

    /** The collection. */
    private static MongoCollection<Document> collection;
    
    /** The source. */
    private ObjectId target, source;
    
    /** The time. */
    private Date time;
    
    /** The text. */
    private String content;
    
    /** The id. */
    private ObjectId id = null;
    

    /**
     * Instantiates a new message.
     */
    private Message() {
    }

    /**
     * Instantiates a new message.
     *
     * @param groupId the group id
     * @param userId the user id
     * @param time the time
     * @param text the text
     */
    public Message(ObjectId groupId, ObjectId userId, Date time, String text) {
        this.target = groupId;
        this.source = userId;
        this.content = text;
        this.time = time;
    }

    /**
     * Gets the collection.
     *
     * @return the collection
     */
    private static MongoCollection<Document> getCollection() {
        if (collection == null) {
            collection = Service.getDatabase().getCollection(Message.class.getName());
            collection.createIndex(new Document("target",1));
            collection.createIndex(new Document("target",1).append("_id",1));
        }
        return collection;
    }

    /**
     * Load.
     *
     * @param doc the doc
     * @return the message
     */
    private static Message load(Document doc) {
        Message rec = new Message();
        rec.id = doc.getObjectId("_id");
        rec.source = doc.getObjectId("source");
        rec.target = doc.getObjectId("target");
        rec.time = doc.getDate("time");
        rec.content = doc.getString("text");
        return rec;
    }

    /**
     * List by target.
     *
     * @param groupId the group id
     * @param len the len
     * @return the list
     */
    public static List<Message> listByTarget(ObjectId groupId, Long ts, int len) {
        Document query = new Document("target", groupId);
        if (ts != null) {
            query.append("time", new Document("$lt", new Date(ts)));
        }
        FindIterable<Document> iter = getCollection().find(query).sort(new Document("_id", -1)).limit(len);
        List<Message> ret = new ArrayList<Message>();
        for (Document doc : iter) {
            ret.add(Message.load(doc));
        }
        return ret;
    }

    /**
     * Find by id.
     *
     * @param id the id
     * @return the message
     */
    public static Message findById(ObjectId id) {
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
        if (id != null) {
            doc.put("_id", id);
        }
        doc.put("target", target);
        doc.put("source", source);
        doc.put("time", time);
        doc.put("text", content);
        if (id == null) {
            getCollection().insertOne(doc);
        } else
            getCollection().replaceOne(new Document("_id", id), doc);
        id = doc.getObjectId("_id");
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    private ObjectId getId() {
        return id;
    }

    /**
     * To json object.
     *
     * @return the JSON object
     */
    public JSONObject toJsonObject() {
        JSONObject messageObj = new JSONObject();
        messageObj.put("id", getId().toString());
        Data data = Data.findByOwner(source);
        if (data != null) {
            Object value = data.getData("name");
            if (value != null) {
                messageObj.put("name", value);
            }
        }
        messageObj.put("time", Tools.FORMAT.format(time));
        messageObj.put("text", content);
        messageObj.put("seq", id.getDate().getTime());
        messageObj.put("source", source.toString());
        return messageObj;
    }

    /**
     * Gets the target.
     *
     * @return the target
     */
    public ObjectId getTarget() {
        return target;
    }

    /**
     * Gets the source.
     *
     * @return the source
     */
    public ObjectId getSource() {
        return source;
    }

    /**
     * Gets the time.
     *
     * @return the time
     */
    public Date getTime() {
        return time;
    }

    /**
     * Gets the text.
     *
     * @return the text
     */
    public String getText() {
        return content;
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