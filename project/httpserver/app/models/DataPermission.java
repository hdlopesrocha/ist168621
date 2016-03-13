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


    public static class Entry {
        private Set<ObjectId> readSet;	// empty means all can read
        private Set<ObjectId> writeSet;	// empty means no one can write

        public Entry(List<ObjectId> readSet, List<ObjectId> writeSet) {
            this.readSet = new HashSet<>();
            this.writeSet = new HashSet<>();
            this.readSet.addAll(readSet);
            this.writeSet.addAll(writeSet);
        }

        public Entry(Set<ObjectId> readSet, Set<ObjectId> writeSet) {
            this.readSet = readSet;
            this.writeSet = writeSet;
        }

        public Set<ObjectId> getReadSet() {
            return readSet;
        }

        public Set<ObjectId> getWriteSet() {
            return writeSet;
        }
    }

    /** The id. */
    private ObjectId id = null;
    
    /** The owner. */
    private ObjectId owner = null;
    
    /** The data. */
    private Map<String, Entry> permissions = null;



    /**
     * Instantiates a new permission.
     *
     * @param owner the owner
     * @param permissions the permissions
     * @param attributes the attributes
     */
    public DataPermission(ObjectId owner, Map<String,Entry> permissions, List<AttributeDto> attributes) {
		List<String> invalidPermisssions = new ArrayList<String>();
        this.permissions = new TreeMap<String, Entry>();

		
		for(Map.Entry<String, Entry> permission : permissions.entrySet()){
            Entry entry = permission.getValue();
			boolean contains = false;
			for(AttributeDto attr : attributes){
				if(permission.getKey().equals(attr.getKey())){
					contains = true;
					break;
				}
			}
			if(contains){
				this.permissions.put(permission.getKey(),entry);
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
        user.permissions = new TreeMap<String, Entry>();
        if(data!=null) {
            for (String key : data.keySet()) {
                Document elem = (Document) data.get(key);
                List<ObjectId> readSet = (List<ObjectId>) elem.get("r");
                List<ObjectId> writeSet = (List<ObjectId>) elem.get("w");
                user.permissions.put(key,new Entry(readSet, writeSet));
            }
        }

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

        Document data = new Document();
        for(Map.Entry<String, Entry> p : permissions.entrySet()){
            Entry entry = p.getValue();
            Document elem = new Document();
            elem.put("r",entry.getReadSet());
            elem.put("w",entry.getWriteSet());
            data.put(p.getKey(),elem);
        }
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
     * List by owner.
     *
     * @param id the id
     * @return the list
     */
    public static List<DataPermission> listByOwner(ObjectId id) {
        Document doc = new Document("owner", id);
        MongoCursor<Document> iter = getCollection().find(doc).iterator();
        List<DataPermission> ret = new ArrayList<DataPermission>();
        while (iter.hasNext()) {
            ret.add(DataPermission.load(iter.next()));
        }

        return ret;
    }
    
    /**
     * Find by owner.
     *
     * @param id the id
     * @param projection the projection
     * @return the document
     */
    public static Document findByOwner(ObjectId id, Document projection) {
        Document doc = new Document("owner", id);
        FindIterable<Document> find = getCollection().find(doc);

        if(projection!=null){
            find.projection(projection);
        }
        doc = find.first();
        return doc != null ? doc : null;
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
    public static DataPermission findByOwner(ObjectId id) {
        Document doc = new Document("owner", id);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? load(doc) : null;
    }

    /**
     * Gets the data.
     *
     * @return the data
     */
    public Map<String,Entry> getData() {
        return permissions;
    }
}