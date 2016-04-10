package models;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import dtos.AttributeDto;
import dtos.KeyValue;
import org.bson.Document;
import org.bson.types.ObjectId;
import services.Service;

import javax.print.Doc;
import java.util.*;
import java.util.regex.Pattern;

/**
 * The Class Data.
 */
public class Data {

    /** The id. */
    private ObjectId id = null;

    /** The data. */
    private Map<String,Attribute> properties = null;

    /** The owner. */
    private ObjectId owner = null;

    /** The search. */
    private List<String> searchableValues;



    /**
     * Instantiates a new data.
     *
     * @param owner
     *            the owner
     * @param attributes
     *            the attributes
     */
    public Data(ObjectId owner, List<AttributeDto> attributes) {
        this.id = getIdFromOwner(owner);
        this.properties = new TreeMap<>();
        this.searchableValues = new ArrayList<String>();

        for (AttributeDto attr : attributes) {
            if (attr.isSearchable()) {
                searchableValues.add(attr.getValue().toString().toLowerCase());
            }
            Attribute attribute = new Attribute(attr);
            properties.put(attribute.getKey(),attribute);
        }
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
        if (collection == null) {
            collection = Service.getDatabase().getCollection(Data.class.getName());
            collection.createIndex(new Document("owner",1));
            collection.createIndex(new Document("search","text"));
        }
        return collection;
    }

    /**
     * Instantiates a new data.
     */
    private Data() {

    }

    /**
     * Load.
     *
     * @param doc
     *            the doc
     * @return the data
     */
    public static Data load(Document doc) {
        Data user = new Data();
        user.id = doc.getObjectId("_id");
        user.owner = doc.getObjectId("owner");
        List<Document> attrs = (List<Document>) doc.get("data");
        user.properties = new TreeMap<>();
        for(Document attr : attrs){
            Attribute attribute = new Attribute(attr);
            user.properties.put(attribute.getKey(),attribute);
        }
        user.searchableValues = (List<String>) doc.get("search");
        return user;
    }

    public boolean isIdentifier(String key) {
        Attribute attr = properties.get(key);
        if(attr!=null && attr.isIdentifiable()){
            return true;
        }
        return false;
    }


    /**
     * Save.

     *
     * @return the data
     */
    public Data save() {
        Document doc = new Document();
        List<Document> props = new ArrayList<>();
        for(Attribute attr : properties.values()) {
            props.add(attr.toDocument());
        }


        doc.put("data", props);
        doc.put("owner", owner);
        doc.put("search", searchableValues);

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
     * @param id
     *            the id
     * @return the data
     */
    public static Data findById(ObjectId id) {
        Document doc = new Document("_id", id);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? load(doc) : null;
    }

    /**
     * Gets the by owner key.
     *
     * @param id
     *            the id
     * @param key
     *            the key
     * @return the by owner key
     */
    public static Data getByOwnerKey(ObjectId id, String key) {
        Document doc = new Document("owner", id).append("key", key);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return iter != null ? Data.load(doc) : null;
    }

    /**
     * Delete by owner.
     *
     * @param owner
     *            the owner
     */
    public static void deleteByOwner(ObjectId owner) {
        getCollection().deleteMany(new Document("owner", owner));
    }

    /**
     * List by key value.
     *
     * @param key
     *            the key
     * @param value
     *            the value
     * @return the list
     */
    public static List<Data> listByKeyValue(String key, Object value) {
        Document doc = new Document("data.k", key).append("data.v", value);
        MongoCursor<Document> iter = getCollection().find(doc).iterator();
        List<Data> ret = new ArrayList<Data>();
        while (iter.hasNext()) {
            ret.add(Data.load(iter.next()));
        }

        return ret;
    }

    /**
     * Gets the id from owner.
     *
     * @param owner
     *            the owner
     * @return the id from owner
     */
    private ObjectId getIdFromOwner(ObjectId owner) {
        Document doc = new Document("owner", owner);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? doc.getObjectId("_id") : null;
    }

    /**
     * Gets the by key value.
     *
     * @param key
     *            the key
     * @param value
     *            the value
     * @return the by key value
     */
    public static Data getByKeyValue(String key, Object value) {
        Document doc = new Document("data.k", key).append("data.v",value);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? Data.load(doc) : null;
    }



    /**
     * Find by owner.
     *
     * @param id
     *            the id
     *            the projection
     * @return the document
     */
    public static Data findByOwner(ObjectId id) {
        Document doc = new Document("owner", id);
        FindIterable<Document> find = getCollection().find(doc);
        doc = find.first();
        return doc != null ? load(doc) : null;
    }

    /**
     * Builds the query.
     *
     * @param filters
     *            the filters
     * @return the document
     */
    private static Document buildQuery(List<List<KeyValue<String>>> filters) {
        Document query = new Document();

        if (filters.size() > 0) {
            List<Document> or = new ArrayList<Document>();
            for (List<KeyValue<String>> list : filters) {
                Document and = new Document();

                for (KeyValue<String> kvp : list) {
                    String value = kvp.getValue();
                    if (value != null && ObjectId.isValid(value)) {
                        and.append("data.k" , kvp.getKey()).append("data.v", new ObjectId(value));
                    } else if ("true".equals(value)) {
                        and.append("data.k" , kvp.getKey()).append("data.v", true);
                    } else if ("false".equals(value)) {
                        and.append("data.k" , kvp.getKey()).append("data.v", false);
                    } else {
                        and.append("data.k" , kvp.getKey()).append("data.v", value);
                    }

                }
                or.add(and);
            }
            query.append("$or", or);
        }
        return query;
    }

    /**
     * Search.
     *
     * @param search
     *            the search
     * @param offset
     *            the offset
     * @param limit
     *            the limit
     * @param filters
     *            the filters
     * @return the list
     */
    public static List<Data> search(String search, Integer offset, Integer limit,
                                    List<List<KeyValue<String>>> filters) {
        Document query = buildQuery(filters);
        System.out.println("SEARCH: " + query.toJson());
        if (search != null) {
            query.append("search", Pattern.compile(search));
        }

        FindIterable<Document> find = getCollection().find(query);
        if (offset != null) {
            find.skip(offset);
        }
        if (limit != null) {
            find.limit(limit);
        }

        MongoCursor<Document> iter = find.iterator();
        List<Data> ret = new ArrayList<Data>();
        while (iter.hasNext()) {
            ret.add(Data.load(iter.next()));
        }
        return ret;

    }

    /**
     * Count by value.
     *
     * @param search
     *            the search
     * @param filters
     *            the filters
     * @return the long
     */
    public static Long countByValue(String search, List<List<KeyValue<String>>> filters) {
        Document query = buildQuery(filters);

        if (search != null) {
            query.append("search", Pattern.compile(search));
        }

        return getCollection().count(query);
    }

    /**
     * Count.
     *
     * @param filters
     *            the filters
     * @return the long
     */
    public static Long count(List<List<KeyValue<String>>> filters) {
        Document query = buildQuery(filters);

        return getCollection().count(query);
    }

    /**
     * Gets the data.
     *
     * @return the data
     */
    public Collection<Attribute> getData() {
        return properties.values();
    }

    public Object getData(String key) {
        Attribute attr = properties.get(key);
        return attr!=null ? attr.getValue() : null;
    }





    /**
     * Gets the data.
     *
     * @return the data
     */
    public boolean hasReadPermission(ObjectId caller,String key) {
        Attribute doc = properties.get(key);
        if(doc!=null) {

            if (hasWritePermission(caller, key)) {
                return true;
            }
            List<ObjectId> r = doc.getReadSet();
            if (r != null) {
                return r.contains(caller);
            }
            return true;
        }
        return false;
    }

    public boolean hasWritePermission(ObjectId caller,String key) {
        Attribute doc = properties.get(key);
        if (doc != null) {
            List<ObjectId> w = doc.getWriteSet();
            if (w != null) {
                return w.contains(caller);
            }
            return true;
        }
        return false;
    }
}