package models;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.types.ObjectId;
import services.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * The Class Attribute.
 */
public class Attribute {


    /** The collection. */
    private static MongoCollection<Document> collection;
    
    /** The key. */
    private String key;
    
    /** The value. */
    private Object value;
    
    /** The id. */
    private ObjectId id = null;
    
    /** The identifiable. */
    private Boolean identifiable = false;
    
    /** The owner. */
    private ObjectId owner = null;

    /**
     * Instantiates a new attribute.
     */
    public Attribute() {

    }


    /**
     * Instantiates a new attribute.
     *
     * @param owner the owner
     * @param key the key
     * @param value the value
     * @param identifiable the identifiable
     */
    public Attribute(ObjectId owner, String key, Object value, boolean identifiable) {
        this.key = key;
        this.owner = owner;
        this.value = value;
        this.identifiable = identifiable;
    }

    /**
     * Gets the collection.
     *
     * @return the collection
     */
    private static MongoCollection<Document> getCollection() {
        if (collection == null)
            collection = Service.getDatabase().getCollection(Attribute.class.getName());
        return collection;
    }

    /**
     * Load.
     *
     * @param doc the doc
     * @return the attribute
     */
    private static Attribute load(Document doc) {
        Attribute user = new Attribute();
        user.id = doc.getObjectId("_id");
        user.owner = doc.getObjectId("owner");
        user.key = doc.getString("key");
        user.value = doc.get("value");
        user.identifiable = doc.getBoolean("identifiable");
        return user;
    }

    /**
     * Find by id.
     *
     * @param id the id
     * @return the attribute
     */
    public static Attribute findById(ObjectId id) {
        Document doc = new Document("_id", id);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? load(doc) : null;
    }

    /**
     * List by key value.
     *
     * @param key the key
     * @param value the value
     * @return the list
     */
    public static List<Attribute> listByKeyValue(String key, Object value) {
        Document doc = new Document("key", key).append("value", value).append("identifiable", false);
        MongoCursor<Document> iter = getCollection().find(doc).iterator();
        List<Attribute> ret = new ArrayList<Attribute>();
        while (iter.hasNext()) {
            ret.add(Attribute.load(iter.next()));
        }

        return ret;
    }

    /**
     * Gets the by key value.
     *
     * @param key the key
     * @param value the value
     * @return the by key value
     */
    public static Attribute getByKeyValue(String key, Object value) {
        Document doc = new Document("key", key).append("value", value).append("identifiable", true);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? Attribute.load(doc) : null;
    }

    /**
     * Gets the by owner key.
     *
     * @param id the id
     * @param key the key
     * @return the by owner key
     */
    public static Attribute getByOwnerKey(ObjectId id, String key) {
        Document doc = new Document("owner", id).append("key", key);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();

        return iter != null ? Attribute.load(doc) : null;
    }

    /**
     * List by owner.
     *
     * @param id the id
     * @return the list
     */
    public static List<Attribute> listByOwner(ObjectId id) {
        Document doc = new Document("owner", id);
        MongoCursor<Document> iter = getCollection().find(doc).iterator();
        List<Attribute> ret = new ArrayList<Attribute>();
        while (iter.hasNext()) {
            ret.add(Attribute.load(iter.next()));
        }

        return ret;
    }

    /**
     * Delete by owner.
     *
     * @param owner the owner
     */
    public static void deleteByOwner(ObjectId owner) {
        getCollection().deleteMany(new Document("owner", owner));
    }

    /**
     * Gets the identifiable.
     *
     * @return the identifiable
     */
    public Boolean getIdentifiable() {
        return identifiable;
    }

    /**
     * Gets the owner.
     *
     * @return the owner
     */
    public ObjectId getOwner() {
        return owner;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Gets the key.
     *
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Save.
     *
     * @return the attribute
     */
    public Attribute save() {
        Document doc = new Document();

        doc.put("key", key);
        doc.put("value", value);
        doc.put("owner", owner);
        doc.put("identifiable", identifiable);

        if (id == null) {
            getCollection().insertOne(doc);
            id = doc.getObjectId("_id");
        } else {
            doc.put("_id", id);
            getCollection().findOneAndReplace(new Document("_id", id), doc);
        }
        return this;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public ObjectId getId() {
        return id;
    }


}
