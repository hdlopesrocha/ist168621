package models;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import dtos.AttributeDto;
import dtos.KeyValue;
import org.bson.Document;
import org.bson.types.ObjectId;
import services.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * The Class Data.
 */
public class Data {

    /** The id. */
    private ObjectId id = null;

    /** The data. */
    private Document properties = null;

    /** The owner. */
    private ObjectId owner = null;

    /** The search. */
    private List<String> searchableValues;

    private List<String> identifiableKeys;


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
        this.properties = new Document();
        this.searchableValues = new ArrayList<String>();
        this.identifiableKeys = new ArrayList<String>();

        for (AttributeDto attr : attributes) {
            if (attr.isSearchable()) {
                searchableValues.add(attr.getValue().toString().toLowerCase());
            }
            if (attr.isIdentifiable()) {
                identifiableKeys.add(attr.getKey());
            }
        }



        for (AttributeDto attr : attributes) {
            Object value = attr.getValue();

            properties.append(attr.getKey(), value);
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
        if (collection == null)
            collection = Service.getDatabase().getCollection(Data.class.getName());
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
        user.properties = (Document) doc.get("data");
        user.searchableValues = (List<String>) doc.get("search");
        user.identifiableKeys = (List<String>) doc.get("iks");

        return user;
    }

    public boolean isIdentifier(String key) {
        return identifiableKeys.contains(key);
    }


    /**
     * Save.

     *
     * @return the data
     */
    public Data save() {
        Document doc = new Document();
        doc.put("data", properties);
        doc.put("owner", owner);
        doc.put("search", searchableValues);
        doc.put("iks", identifiableKeys);

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
        Document doc = new Document("data." + key, value);
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
        Document doc = new Document("data." + key, value);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? Data.load(doc) : null;
    }

    /**
     * Find by owner.
     *
     * @param id
     *            the id
     * @param projection
     *            the projection
     * @return the document
     */
    public static Document findByOwner(ObjectId id, Document projection) {
        Document doc = new Document("owner", id);
        FindIterable<Document> find = getCollection().find(doc);

        if (projection != null) {
            find.projection(projection);
        }
        doc = find.first();
        return doc != null ? doc : null;
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
                        and.append("data." + kvp.getKey(), new ObjectId(value));
                    } else if ("true".equals(value)) {
                        and.append("data." + kvp.getKey(), true);
                    } else if ("false".equals(value)) {
                        and.append("data." + kvp.getKey(), false);
                    } else {
                        and.append("data." + kvp.getKey(), value);
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
    public Document getData() {
        return properties;
    }
}