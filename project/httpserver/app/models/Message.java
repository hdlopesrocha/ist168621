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
    private String text;
    
    /** The id. */
    private ObjectId id = null;
    
    /** The sequence. */
    private Long sequence;

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
        this.text = text;
        this.time = time;
    }

    /**
     * Gets the collection.
     *
     * @return the collection
     */
    private static MongoCollection<Document> getCollection() {
        if (collection == null)
            collection = Service.getDatabase().getCollection(Message.class.getName());
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
        rec.text = doc.getString("text");
        rec.sequence = doc.getLong("seq");
        return rec;
    }

    /**
     * List by target.
     *
     * @param groupId the group id
     * @param end the end
     * @param len the len
     * @return the list
     */
    public static List<Message> listByTarget(ObjectId groupId, Long end, int len) {
        Document query = new Document("target", groupId);
        if (end != null) {
            query.append("seq", new Document("$lt", end));
        }

        FindIterable<Document> iter = getCollection()
                .find(query).sort(new Document("seq", -1)).limit(len);
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
     * Generate sequence.
     *
     * @return the long
     */
    private Long generateSequence() {
        return getCollection()
                .count(new Document("target", target));
    }

    /**
     * Save.
     */
    public void save() {
        Document doc = new Document();
        if (id != null)
            doc.put("_id", id);

        doc.put("target", target);
        doc.put("source", source);
        doc.put("time", time);
        doc.put("text", text);
        doc.put("seq", sequence);


        if (id == null) {
            doc.put("seq", generateSequence());
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
        Document attr = Data.findByOwner(source,new Document("data.name",1));
        if (attr != null) {
            attr = (Document) attr.get("data");
            if (attr != null) {
                messageObj.put("name", attr.getString("name"));
            }
        }
        messageObj.put("time", Tools.FORMAT.format(time));
        messageObj.put("text", text);
        messageObj.put("seq", sequence);
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
        return text;
    }

    /**
     * Gets the sequence.
     *
     * @return the sequence
     */
    public Long getSequence() {
        return sequence;
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