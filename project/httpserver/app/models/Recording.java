package models;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import services.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Recording {


    private static MongoCollection<Document> collection;
    private ObjectId groupId, owner;
    private Date start, end;
    private String url;
    private ObjectId id = null;

    private Recording() {

    }

    public Recording(ObjectId groupId, ObjectId owner, Date start, Date end, String url) {
        this.groupId = groupId;
        this.owner = owner;
        this.start = start;
        this.end = end;
        this.url = url;
    }

    public static MongoCollection<Document> getCollection() {
        if (collection == null)
            collection = Service.getDatabase().getCollection(Recording.class.getName());
        return collection;
    }

    public static Recording load(Document doc) {
        Recording rec = new Recording();
        rec.id = doc.getObjectId("_id");
        rec.end = doc.getDate("end");
        rec.start = doc.getDate("start");
        rec.owner = doc.getObjectId("uid");
        rec.groupId = doc.getObjectId("gid");
        rec.url = doc.getString("url");
        return rec;
    }

    public static long countByGroup(ObjectId owner) {
        Document doc = new Document("gid", owner);
        return getCollection().count(doc);
    }

    public static List<Recording> listByGroup(ObjectId groupId, long sequence) {
        FindIterable<Document> iter = getCollection()
                .find(new Document("gid", groupId).append("seq", new Document("$gt", sequence)));
        List<Recording> ret = new ArrayList<Recording>();
        for (Document doc : iter) {
            ret.add(Recording.load(doc));
        }
        return ret;
    }

    public static Recording findById(ObjectId id) {
        Document doc = new Document("_id", id);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? load(doc) : null;
    }

    public void save() {
        Document doc = new Document();
        if (id != null)
            doc.put("_id", id);

        doc.put("gid", groupId);
        doc.put("uid", owner);
        doc.put("start", start);
        doc.put("end", end);
        doc.put("url", url);

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public ObjectId getOwner() {
        return owner;
    }

}