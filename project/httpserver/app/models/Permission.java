package models;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import dtos.AttributeDto;
import org.bson.Document;
import org.bson.types.ObjectId;
import services.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hdlopesrocha on 06-01-2016.
 */
public class Permission {

    /** The collection. */
    private static MongoCollection<Document> collection;
    
    /** The id. */
    private ObjectId id = null;
    
    /** The owner. */
    private ObjectId owner = null;
    
    /** The data. */
    private Document data = null;

    /**
     * Instantiates a new permission.
     *
     * @param owner the owner
     * @param attributes the attributes
     */
    public Permission(ObjectId owner, List<AttributeDto> attributes) {
        this.data = new Document();

        for (AttributeDto attr : attributes) {
            if (attr.getVisibility().equals(AttributeDto.Visibility.PRIVATE)) {
                List<ObjectId> array = new ArrayList<ObjectId>();
                array.add(owner);
                data.append(attr.getKey(), array);
            }
        }
        this.owner = owner;
    }

    /**
     * Instantiates a new permission.
     */
    private Permission() {

    }

    /**
     * Gets the collection.
     *
     * @return the collection
     */
    public static MongoCollection<Document> getCollection() {
        if (collection == null)
            collection = Service.getDatabase().getCollection(Permission.class.getName());
        return collection;
    }

    /**
     * Load.
     *
     * @param doc the doc
     * @return the permission
     */
    public static Permission load(Document doc) {
        Permission user = new Permission();
        user.id = doc.getObjectId("_id");
        user.owner = doc.getObjectId("owner");
        user.data = (Document) doc.get("data");
        return user;
    }

    /**
     * Find by id.
     *
     * @param id the id
     * @return the permission
     */
    public static Permission findById(ObjectId id) {
        Document doc = new Document("_id", id);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? load(doc) : null;
    }

    /**
     * List by owner.
     *
     * @param id the id
     * @return the list
     */
    public static List<Permission> listByOwner(ObjectId id) {
        Document doc = new Document("owner", id);
        MongoCursor<Document> iter = getCollection().find(doc).iterator();
        List<Permission> ret = new ArrayList<Permission>();
        while (iter.hasNext()) {
            ret.add(Permission.load(iter.next()));
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
     * Find by owner.
     *
     * @param id the id
     * @return the permission
     */
    public static Permission findByOwner(ObjectId id) {
        Document doc = new Document("owner", id);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? load(doc) : null;
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
     * Save.
     *
     * @return the permission
     */
    public Permission save() {
        Document doc = new Document();
        doc.put("owner", owner);
        doc.put("data", data);


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

    /**
     * Gets the data.
     *
     * @return the data
     */
    public Document getData() {
        return data;
    }
}