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
    private ObjectId groupId, owner, interval;
    private Date start, end;
    private String name, type;
    private String url;
    private ObjectId id = null;
    private long sequence;

    public Recording() {
    }
    public Recording(ObjectId groupId, ObjectId owner, Date start, Date end, String name, String type, String url,
                     long sequence, ObjectId interval) {
        this.groupId = groupId;
        this.owner = owner;
        this.start = start;
        this.end = end;
        this.url = url;
        this.sequence = sequence;
        this.name = name;
        this.type = type;
        this.interval = interval;
    }

    public static MongoCollection<Document> getCollection() {
        if (collection == null)
            collection = Service.getDatabase().getCollection("recordings");
        return collection;
    }

    public static Recording load(Document doc) {
        Recording rec = new Recording();
        rec.id = doc.getObjectId("_id");
        rec.end = doc.getDate("end");
        rec.start = doc.getDate("start");
        rec.name = doc.getString("name");
        rec.type = doc.getString("type");
        rec.interval = doc.getObjectId("inter");

        rec.owner = doc.getObjectId("uid");
        rec.groupId = doc.getObjectId("gid");
        rec.url = doc.getString("url");
        rec.sequence = doc.getLong("seq");
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        doc.put("name", name);
        doc.put("inter", interval);
        doc.put("type", type);
        doc.put("seq", sequence);

        if (id == null)
            getCollection().insertOne(doc);
        else
            getCollection().replaceOne(new Document("_id", id), doc);

        id = doc.getObjectId("_id");

    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
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

    public ObjectId getInterval() {
        return interval;
    }

    public void setInterval(ObjectId interval) {
        this.interval = interval;
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