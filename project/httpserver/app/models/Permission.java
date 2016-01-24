package models;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import dtos.AttributeDto;
import org.bson.Document;
import org.bson.types.ObjectId;
import services.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hdlopesrocha on 06-01-2016.
 */
public class Permission {

    private static MongoCollection<Document> collection;
    private ObjectId id = null;
    private ObjectId owner = null;
    private Document data = null;

    public Permission(ObjectId owner, List<AttributeDto> attributes) {
        this.data = new Document();

        for (AttributeDto attr : attributes) {
            if (attr.getVisibility().equals(AttributeDto.Visibility.PRIVATE)) {
                List<ObjectId> array = new ArrayList<ObjectId>();
                array.add(owner);
                data.append(attr.getKey(), array);
            }
        }
        this.owner = owner;
    }

    private Permission() {

    }

    public static MongoCollection<Document> getCollection() {
        if (collection == null)
            collection = Service.getDatabase().getCollection(Permission.class.getName());
        return collection;
    }

    public static Permission load(Document doc) {
        Permission user = new Permission();
        user.id = doc.getObjectId("_id");
        user.owner = doc.getObjectId("owner");
        user.data = (Document) doc.get("data");
        return user;
    }

    public static Permission findById(ObjectId id) {
        Document doc = new Document("_id", id);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? load(doc) : null;
    }

    public static List<Permission> listByOwner(ObjectId id) {
        Document doc = new Document("owner", id);
        MongoCursor<Document> iter = getCollection().find(doc).iterator();
        List<Permission> ret = new ArrayList<Permission>();
        while (iter.hasNext()) {
            ret.add(Permission.load(iter.next()));
        }

        return ret;
    }

    public static void deleteByOwner(ObjectId owner) {
        getCollection().deleteMany(new Document("owner", owner));
    }

    public static Permission findByOwner(ObjectId id) {
        Document doc = new Document("owner", id);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? load(doc) : null;
    }

    public ObjectId getOwner() {
        return owner;
    }

    public Permission save() {
        Document doc = new Document();
        doc.put("owner", owner);
        doc.put("data", data);


        if (id == null) {
            getCollection().insertOne(doc);
            id = doc.getObjectId("_id");
        } else {
            doc.put("_id", id);
            getCollection().findOneAndReplace(new Document("_id", id), doc);
        }
        return this;
    }

    public ObjectId getId() {
        return id;
    }

    public Document getData() {
        return data;
    }
}