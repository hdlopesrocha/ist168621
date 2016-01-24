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
public class Data {

    private static MongoCollection<Document> collection;
    private ObjectId id = null;
    private Document data = null;
    private ObjectId owner = null;

    public Data(ObjectId owner, List<AttributeDto> attributes) {

        this.data = new Document();
        for (AttributeDto attr : attributes) {
            data.append(attr.getKey(), attr.getValue());
        }
        this.owner = owner;
    }

    private Data() {

    }

    public static MongoCollection<Document> getCollection() {
        if (collection == null)
            collection = Service.getDatabase().getCollection(Data.class.getName());
        return collection;
    }

    public static Data load(Document doc) {
        Data user = new Data();
        user.id = doc.getObjectId("_id");
        user.owner = doc.getObjectId("owner");
        user.data = (Document) doc.get("data");
        return user;
    }

    public static Data findById(ObjectId id) {
        Document doc = new Document("_id", id);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? load(doc) : null;
    }

    public static Data getByOwnerKey(ObjectId id, String key) {
        Document doc = new Document("owner", id).append("key", key);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();

        return iter != null ? Data.load(doc) : null;
    }

    public static void deleteByOwner(ObjectId owner) {
        getCollection().deleteMany(new Document("owner", owner));
    }

    public static List<Data> listByKeyValue(String key, Object value) {
        Document doc = new Document("data." + key, value);
        MongoCursor<Document> iter = getCollection().find(doc).iterator();
        List<Data> ret = new ArrayList<Data>();
        while (iter.hasNext()) {
            ret.add(Data.load(iter.next()));
        }

        return ret;
    }

    public static Data getByKeyValue(String key, Object value) {
        Document doc = new Document("data." + key, value);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? Data.load(doc) : null;
    }

    public static Document findByOwner(ObjectId id, Document projection) {
        Document doc = new Document("owner", id);
        FindIterable<Document> find = getCollection().find(doc);

        if (projection != null) {
            find.projection(projection);
        }
        doc = find.first();
        return doc != null ? doc : null;
    }

    public ObjectId getOwner() {
        return owner;
    }

    public Data save() {
        Document doc = new Document();
        doc.put("data", data);
        doc.put("owner", owner);


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