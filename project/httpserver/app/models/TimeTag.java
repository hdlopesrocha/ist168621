package models;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import main.Tools;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import services.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class TimeTag {

    private static MongoCollection<Document> collection;
    private Date time;
    private String title;
    private String content;
    private ObjectId id, gid = null;

    private TimeTag() {

    }

    public TimeTag(ObjectId gid, Date time, String title, String content) {
        this.time = time;
        this.gid = gid;
        this.title = title;
        this.content = content;
    }

    public static MongoCollection<Document> getCollection() {
        if (collection == null)
            collection = Service.getDatabase().getCollection(TimeTag.class.getName());
        return collection;
    }

    public static TimeTag load(Document doc) {
        TimeTag rec = new TimeTag();
        rec.id = doc.getObjectId("_id");
        rec.time = doc.getDate("time");
        rec.gid = doc.getObjectId("gid");
        rec.title = doc.getString("title");
        rec.content = doc.getString("content");
        return rec;
    }

    public static long countByGroup(ObjectId owner) {
        Document doc = new Document("gid", owner);
        return getCollection().count(doc);
    }

    public static List<TimeTag> listByGroup(ObjectId groupId) {
        FindIterable<Document> iter = getCollection()
                .find(new Document("gid", groupId));
        List<TimeTag> ret = new ArrayList<TimeTag>();
        for (Document doc : iter) {
            ret.add(TimeTag.load(doc));
        }
        return ret;
    }

    public static TimeTag findById(ObjectId id) {
        Document doc = new Document("_id", id);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? load(doc) : null;
    }

    public static List<TimeTag> search(ObjectId gid, String query) {
        Pattern regex = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
        Document doc = new Document("gid", gid).append("title", regex);
        FindIterable<Document> iter = getCollection().find(doc);
        Iterator<Document> i = iter.iterator();
        List<TimeTag> ret = new ArrayList<TimeTag>();
        while (i.hasNext()) {
            ret.add(TimeTag.load(i.next()));
        }
        return ret;
    }

    public void save() {
        Document doc = new Document();
        if (id != null)
            doc.put("_id", id);

        doc.put("gid", gid);
        doc.put("time", time);
        doc.put("title", title);
        doc.put("content", content);

        if (id == null)
            getCollection().insertOne(doc);
        else
            getCollection().replaceOne(new Document("_id", id), doc);

        id = doc.getObjectId("_id");

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

    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        obj.put("id", id.toString());
        obj.put("title", title);
        obj.put("content", content);
        obj.put("time", Tools.FORMAT.format(time));
        return obj;
    }

}