package models;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import services.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * The Class HyperContent.
 */
public class CollaborativeContent {

    /** The collection. */
    private static MongoCollection<Document> collection;

    /** The content. */
    private String content = null;

    /** The group id. */
    private ObjectId id, groupId = null;

    /**
     * Instantiates a new hyper content.
     *
     * @param gid the gid
     * @param content the content
     */
    public CollaborativeContent(ObjectId gid, String content) {

        this.groupId = gid;
        this.content = content;
    }

    /**
     * Instantiates a new hyper content.
     */
    private CollaborativeContent() {
    }

    /**
     * Gets the collection.
     *
     * @return the collection
     */
    public static MongoCollection<Document> getCollection() {
        if (collection == null)
            collection = Service.getDatabase().getCollection(CollaborativeContent.class.getName());
        return collection;
    }

    /**
     * Load.
     *
     * @param doc the doc
     * @return the hyper content
     */
    public static CollaborativeContent load(Document doc) {
        CollaborativeContent content = new CollaborativeContent();
        content.id = doc.getObjectId("_id");
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
    public static CollaborativeContent findByGroupId(ObjectId id) {
        Document doc = new Document("gid", id);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? load(doc) : null;
    }

    /**
     * Find by id.
     *
     * @param id the id
     * @return the hyper content
     */
    public static CollaborativeContent findById(ObjectId id) {
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

        doc.put("gid", groupId);
        doc.put("content", content);

        if (id == null)
            getCollection().insertOne(doc);
        else
            getCollection().replaceOne(new Document("_id", id), doc);

        id = doc.getObjectId("_id");

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

    public void setContent(String content) {
        this.content = content;
    }
}