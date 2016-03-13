package models;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import dtos.AttributeDto;
import org.bson.Document;
import org.bson.types.ObjectId;
import services.Service;

import java.util.*;


/**
 * The Class Permission.
 */
public class DataPermission {




    /** The id. */
    private ObjectId id = null;
    
    /** The owner. */
    private ObjectId owner = null;
    
    /** The data. */
    private Document readPermissions = null;
    private Document writePermissions = null;



    /**
     * Instantiates a new permission.
     *
     * @param owner the owner
     * @param permissions the permissions
     * @param attributes the attributes
     */
    public DataPermission(ObjectId owner, Map<String,PermissionEntry> permissions, List<AttributeDto> attributes) {
        this.readPermissions = new Document();
        this.writePermissions = new Document();

		
		for(Map.Entry<String, PermissionEntry> permission : permissions.entrySet()){
            PermissionEntry entry = permission.getValue();
			boolean contains = false;
			for(AttributeDto attr : attributes){
				if(permission.getKey().equals(attr.getKey())){
					contains = true;
					break;
				}
			}
			if(contains){
                readPermissions.put(permission.getKey(),entry.getReadSet());
                writePermissions.put(permission.getKey(),entry.getWriteSet());

            }
		}
		


        this.id = getIdFromOwner(owner);
        this.owner = owner;
    }

    /**
     * Gets the owner.
     *
     * @return the owner
     */
    public ObjectId getOwner() {
        return owner;
    }

    /** The collection. */
    private static MongoCollection<Document> collection;

    /**
     * Gets the collection.
     *
     * @return the collection
     */
    public static MongoCollection<Document> getCollection() {
        if (collection == null)
            collection = Service.getDatabase().getCollection(DataPermission.class.getName());
        return collection;
    }

    /**
     * Instantiates a new permission.
     */
    private DataPermission(){

    }

    /**
     * Gets the id from owner.
     *
     * @param owner the owner
     * @return the id from owner
     */
    private ObjectId getIdFromOwner(ObjectId owner){
    	 Document doc = new Document("owner", owner);
         FindIterable<Document> iter = getCollection().find(doc);
         doc = iter.first();
         return doc != null ? doc.getObjectId("_id") : null;
    }
    
    /**
     * Load.
     *
     * @param doc the doc
     * @return the permission
     */
    public static DataPermission load(Document doc) {
        DataPermission user = new DataPermission();
        user.id = doc.getObjectId("_id");
        user.owner = doc.getObjectId("owner");

        Document data = (Document) doc.get("data");



        return user;
    }

    /**
     * Save.
     *
     * @return the permission
     */
    public DataPermission save() {
        Document doc = new Document();
        doc.put("owner", owner);


        doc.put("rp", readPermissions);
        doc.put("wp", writePermissions);


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
     * Find by id.
     *
     * @param id the id
     * @return the permission
     */
    public static DataPermission findById(ObjectId id) {
        Document doc = new Document("_id", id);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? load(doc) : null;
    }


    /**
     * Find by owner.
     *
     * @param id the id
     * @return the permission
     */
    public static DataPermission findByOwner(ObjectId id) {
        Document doc = new Document("owner", id);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? load(doc) : null;
    }


}