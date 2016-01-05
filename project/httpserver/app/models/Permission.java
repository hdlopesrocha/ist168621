package models;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import services.Service;

import java.util.ArrayList;
import java.util.List;

public class Permission {
    private ObjectId id;
    private ObjectId source;
    private ObjectId target;
    private String name;

    public static final String TYPE = "common.Permission";
    public static final String PERMISSION_READ = "READ";
    public static final String PERMISSION_WRITE = "WRITE";
    public static final String PERMISSION_ADMIN = "root.admin";


    private static MongoCollection<Document> collection;

    public static MongoCollection<Document> getCollection() {
        if (collection == null) {
            collection = Service.getDatabase().getCollection(TYPE);
        }

        return collection;
    }


    public Permission() {

    }

    public Permission(ObjectId source, String name, ObjectId target) {
        this.name = name;
        this.source = source;
        this.target = target;
    }


    public ObjectId getSource() {
        return source;
    }

    public void setSource(ObjectId source) {
        this.source = source;
    }

    public ObjectId getTarget() {
        return target;
    }

    public void setTarget(ObjectId target) {
        this.target = target;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void delete() {
        if (id != null) {
            getCollection().deleteOne(new Document("_id", id));
        }
    }

    public void save() {
        Document doc = new Document();
        if (id != null)
            doc.put("_id", id);
        doc.put("target", target);
        doc.put("source", source);
        doc.put("name", name);

        if (id != null)
            getCollection().deleteOne(new Document("_id", id));

        getCollection().insertOne(doc);

        id = doc.getObjectId("_id");

    }

    public static Permission load(Document doc) {
        Permission obj = new Permission();
        obj.id = doc.getObjectId("_id");
        obj.source = doc.getObjectId("source");
        obj.target = doc.getObjectId("target");
        obj.name = doc.getString("name");
        return obj;
    }

    public static void deleteObject(ObjectId id) {
        getCollection().deleteMany(new Document("target", id));
        getCollection().deleteMany(new Document("source", id));
    }

    public static void deleteTargetPermissions(ObjectId target) {
        getCollection().deleteMany(new Document("target", target));
    }

    public static void deleteSourcePermissions(ObjectId source) {
        getCollection().deleteMany(new Document("source", source));
    }

    public static Permission findById(ObjectId id) {
        Document doc = new Document("_id", id);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? load(doc) : null;
    }

    public static boolean allowed(ObjectId source, String name, ObjectId target) {
        if (source != null) {
            return find(source, name, target) != null || find(null, name, target) != null;
        } else {
            return find(source, name, target) != null;
        }
    }

    public static Permission find(ObjectId source, String name, ObjectId target) {
        Document doc = new Document("source", source).append("name", name).append("target", target);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? load(doc) : null;
    }

    public static List<Permission> list(ObjectId source) {
        FindIterable<Document> iter = getCollection().find(new Document("source", source));
        List<Permission> ret = new ArrayList<Permission>();
        for (Document doc : iter) {
            ret.add(load(doc));
        }
        return ret;
    }
}