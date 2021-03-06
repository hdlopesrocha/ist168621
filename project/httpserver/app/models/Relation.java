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
 * The Class Relation.
 */
public class Relation {

    /** The collection. */
    private static MongoCollection<Document> collection;
    
    /** The id. */
    private ObjectId source = null, target = null, id = null;

    /**
     * Instantiates a new relation.
     */
    private Relation() {
    }

    /**
     * Instantiates a new relation.
     *
     * @param fi the fi
     * @param ti the ti
     */
    public Relation(ObjectId fi, ObjectId ti) {
        this.target = ti;
        this.source = fi;
    }

    /**
     * Gets the collection.
     *
     * @return the collection
     */
    private static MongoCollection<Document> getCollection() {
        if (collection == null) {
            collection = Service.getDatabase().getCollection(Relation.class.getName());
            collection.createIndex(new Document("s",1));
            collection.createIndex(new Document("t",1));
            collection.createIndex(new Document("s",1).append("t",1));
        }
        return collection;
    }

    /**
     * Load.
     *
     * @param doc the doc
     * @return the relation
     */
    private static Relation load(Document doc) {
        Relation user = new Relation();
        user.setId(doc.getObjectId("_id"));
        user.setFrom(doc.getObjectId("s"));
        user.setTo(doc.getObjectId("t"));
        return user;
    }

    /**
     * Find by endpoint.
     *
     * @param a the a
     * @param b the b
     * @return the relation
     */
    public static Relation findByEndpoint(ObjectId a, ObjectId b) {
        Document doc = new Document("s", a).append("t", b);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? load(doc) : null;
    }

    /**
     * List from.
     *
     * @param ep the ep
     * @return the list
     */
    public static List<Relation> listFrom(ObjectId ep) {
        Document doc = new Document("s", ep);
        FindIterable<Document> iter = getCollection().find(doc);
        return deserialize(iter.iterator());
    }

    /**
     * List to.
     *
     * @param ep the ep
     * @return the list
     */
    public static List<Relation> listTo(ObjectId ep) {
        Document doc = new Document("t", ep);
        FindIterable<Document> iter = getCollection().find(doc);
        return deserialize(iter.iterator());
    }

    private static List<Relation> deserialize(Iterator<Document> it){
        List<Relation> ret = new ArrayList<>();
        while (it.hasNext()) {
            ret.add(load(it.next()));
        }
        return ret;
    }


    /**
     * Find by id.
     *
     * @param id the id
     * @return the relation
     */
    public static Relation findById(ObjectId id) {
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
        if (id != null){
            doc.put("_id", id);
        }
        doc.put("s", source);
        doc.put("t", target);

        if (id == null) {
            getCollection().insertOne(doc);
        } else {
            getCollection().replaceOne(new Document("_id", id), doc);
        }
        id = doc.getObjectId("_id");

    }

    /**
     * Gets the from.
     *
     * @return the from
     */
    public ObjectId getFrom() {
        return source;
    }

    /**
     * Sets the from.
     *
     * @param from the new from
     */
    private void setFrom(ObjectId from) {
        this.source = from;
    }

    /**
     * Gets the to.
     *
     * @return the to
     */
    public ObjectId getTo() {
        return target;
    }

    /**
     * Sets the to.
     *
     * @param to the new to
     */
    private void setTo(ObjectId to) {
        this.target = to;
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
        if (id != null) {
            getCollection().deleteOne(new Document("_id", id));
        }
    }

}