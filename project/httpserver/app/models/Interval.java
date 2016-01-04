package models;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import services.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Interval {

    private static MongoCollection<Document> collection;
    private Date start, end;
    private ObjectId id, gid = null;

    private Interval() {

    }

    public Interval(ObjectId gid) {
        this.gid = gid;

    }

    public Interval(ObjectId gid, Date start, Date end) {
        this.start = start;
        this.gid = gid;
        this.end = end;
    }

    public static MongoCollection<Document> getCollection() {
        if (collection == null)
            collection = Service.getDatabase().getCollection("intervals");
        return collection;
    }

    private static Interval load(Document doc) {
        Interval rec = new Interval();
        rec.id = doc.getObjectId("_id");
        rec.end = doc.getDate("end");
        rec.gid = doc.getObjectId("gid");
        rec.start = doc.getDate("start");
        return rec;
    }

    public static long countByGroup(ObjectId owner) {
        Document doc = new Document("gid", owner);
        return getCollection().count(doc);
    }

    public static List<Interval> listByGroup(ObjectId groupId) {
        FindIterable<Document> iter = getCollection()
                .find(new Document("gid", groupId));
        List<Interval> ret = new ArrayList<Interval>();
        for (Document doc : iter) {
            ret.add(Interval.load(doc));
        }
        return ret;
    }

    public static Interval findById(ObjectId id) {
        Document doc = new Document("_id", id);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? load(doc) : null;
    }

    public void save() {
        Document doc = new Document();
        if (id != null)
            doc.put("_id", id);

        doc.put("gid", gid);
        doc.put("start", start);
        doc.put("end", end);

        if (id == null)
            getCollection().insertOne(doc);
        else
            getCollection().replaceOne(new Document("_id", id), doc);

        id = doc.getObjectId("_id");

    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void delete() {
        if (id != null) {
            getCollection().deleteOne(new Document("_id", id));
        }
    }

}