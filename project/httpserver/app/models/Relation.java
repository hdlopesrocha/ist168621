package models;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import services.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Relation {

    private static MongoCollection<Document> collection;
    private ObjectId from = null, to = null, id = null;

    private Relation() {
    }

    public Relation(ObjectId fi, ObjectId ti) {

        this.to = ti;
        this.from = fi;

    }

    private static MongoCollection<Document> getCollection() {
        if (collection == null)
            collection = Service.getDatabase().getCollection(Relation.class.getName());
        return collection;
    }

    private static Relation load(Document doc) {
        Relation user = new Relation();
        user.setId(doc.getObjectId("_id"));
        user.setFrom(doc.getObjectId("fi"));
        user.setTo(doc.getObjectId("ti"));

        return user;
    }

    public static Relation findByEndpoint(ObjectId a, ObjectId b) {
        Document doc = new Document("fi", a).append("ti", b);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? load(doc) : null;
    }

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

    public static Relation findById(ObjectId id) {
        Document doc = new Document("_id", id);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? load(doc) : null;
    }

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

    public ObjectId getFrom() {
        return from;
    }

    private void setFrom(ObjectId from) {
        this.from = from;
    }

    public ObjectId getTo() {
        return to;
    }

    private void setTo(ObjectId to) {
        this.to = to;
    }

    public ObjectId getId() {
        return id;
    }

    private void setId(ObjectId id) {
        this.id = id;
    }

    public void delete() {
        if (id != null)
            getCollection().deleteOne(new Document("_id", id));
    }

}