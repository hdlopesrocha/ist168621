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
    private ObjectId from = null, to = null, id = null;

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

        this.to = ti;
        this.from = fi;

    }

    /**
     * Gets the collection.
     *
     * @return the collection
     */
    private static MongoCollection<Document> getCollection() {
        if (collection == null)
            collection = Service.getDatabase().getCollection(Relation.class.getName());
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
        user.setFrom(doc.getObjectId("fi"));
        user.setTo(doc.getObjectId("ti"));

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
        Document doc = new Document("fi", a).append("ti", b);
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
        Document doc = new Document("fi", ep);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();

        Iterator<Document> i = iter.iterator();
        List<Relation> ret = new ArrayList<Relation>();
        while (i.hasNext()) {
            ret.add(load(i.next()));
        }
        return ret;
    }

    /**
     * List to.
     *
     * @param ep the ep
     * @return the list
     */
    public static List<Relation> listTo(ObjectId ep) {
        Document doc = new Document("ti", ep);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();

        Iterator<Document> i = iter.iterator();
        List<Relation> ret = new ArrayList<Relation>();
        while (i.hasNext()) {
            ret.add(load(i.next()));
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
        if (id != null)
            doc.put("_id", id);

        doc.put("fi", from);
        doc.put("ti", to);


        if (id == null)
            getCollection().insertOne(doc);
        else
            getCollection().replaceOne(new Document("_id", id), doc);

        id = doc.getObjectId("_id");

    }

    /**
     * Gets the from.
     *
     * @return the from
     */
    public ObjectId getFrom() {
        return from;
    }

    /**
     * Sets the from.
     *
     * @param from the new from
     */
    private void setFrom(ObjectId from) {
        this.from = from;
    }

    /**
     * Gets the to.
     *
     * @return the to
     */
    public ObjectId getTo() {
        return to;
    }

    /**
     * Sets the to.
     *
     * @param to the new to
     */
    private void setTo(ObjectId to) {
        this.to = to;
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