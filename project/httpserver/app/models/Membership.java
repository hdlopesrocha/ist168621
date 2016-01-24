package models;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import services.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * The Class Membership.
 */
public class Membership {


    /** The collection. */
    private static MongoCollection<Document> collection;
    
    /** The user id. */
    private ObjectId id = null, groupId = null, userId = null;
    
    /** The properties. */
    private Document properties = new Document();


    /**
     * Instantiates a new membership.
     */
    private Membership() {
    }


    /**
     * Instantiates a new membership.
     *
     * @param userId the user id
     * @param groupId the group id
     */
    public Membership(ObjectId userId, ObjectId groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }

    /**
     * Gets the collection.
     *
     * @return the collection
     */
    private static MongoCollection<Document> getCollection() {
        if (collection == null)
            collection = Service.getDatabase().getCollection(Membership.class.getName());
        return collection;
    }

    /**
     * Load.
     *
     * @param doc the doc
     * @return the membership
     */
    private static Membership load(Document doc) {
        Membership user = new Membership();
        user.setId(doc.getObjectId("_id"));
        user.setUserId(doc.getObjectId("uid"));
        user.setGroupId(doc.getObjectId("gid"));
        if (doc.containsKey("prop"))
            user.setProperties((Document) doc.get("prop"));
        return user;
    }

    /**
     * List by user.
     *
     * @param id the id
     * @return the list
     */
    public static List<Membership> listByUser(ObjectId id) {
        Document doc = new Document("uid", id);
        FindIterable<Document> iter = getCollection().find(doc);
        Iterator<Document> i = iter.iterator();
        List<Membership> ans = new ArrayList<Membership>();
        while (i.hasNext()) {
            ans.add(load(i.next()));
        }
        return ans;
    }

    /**
     * Find by user group.
     *
     * @param uid the uid
     * @param gid the gid
     * @return the membership
     */
    public static Membership findByUserGroup(ObjectId uid, ObjectId gid) {
        Document doc = new Document("gid", gid).append("uid", uid);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? load(doc) : null;
    }

    /**
     * List by group.
     *
     * @param id the id
     * @return the list
     */
    public static List<Membership> listByGroup(ObjectId id) {
        Document doc = new Document("gid", id);
        FindIterable<Document> iter = getCollection().find(doc);
        Iterator<Document> i = iter.iterator();
        List<Membership> ans = new ArrayList<Membership>();
        while (i.hasNext()) {
            ans.add(load(i.next()));
        }
        return ans;
    }

    /**
     * Find by id.
     *
     * @param id the id
     * @return the membership
     */
    public static Membership findById(ObjectId id) {
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
        doc.put("uid", userId);
        doc.put("prop", properties);


        if (id == null)
            getCollection().insertOne(doc);
        else
            getCollection().replaceOne(new Document("_id", id), doc);

        id = doc.getObjectId("_id");
    }

    /**
     * Gets the properties.
     *
     * @return the properties
     */
    public Document getProperties() {
        return properties;
    }

    /**
     * Sets the properties.
     *
     * @param properties the new properties
     */
    private void setProperties(Document properties) {
        this.properties = properties;
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
     * Sets the group id.
     *
     * @param groupId the new group id
     */
    private void setGroupId(ObjectId groupId) {
        this.groupId = groupId;
    }

    /**
     * Gets the user id.
     *
     * @return the user id
     */
    public ObjectId getUserId() {
        return userId;
    }

    /**
     * Sets the user id.
     *
     * @param userId the new user id
     */
    private void setUserId(ObjectId userId) {
        this.userId = userId;
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
    private void setId(ObjectId id) {
        this.id = id;
    }

    /**
     * Delete.
     */
    public void delete() {
        if (id != null)
            getCollection().deleteOne(new Document("_id", id));
    }


}