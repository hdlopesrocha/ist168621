package models;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import dtos.KeyValue;
import org.bson.Document;
import org.bson.types.ObjectId;
import services.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by hdlopesrocha on 06-01-2016.
 */
public class Tag {

    private List<String> search;
    private ObjectId id = null;
    private String type;
    private ObjectId owner = null;
    private Document filter= null;

    public Tag(ObjectId owner, List<String> search, String type,Document filter) {
        this.owner = owner;
        this.search = search;
        this.type = type;
        this.filter = filter;
    }

    public ObjectId getOwner() {
        return owner;
    }


    private static MongoCollection<Document> collection;

    public static MongoCollection<Document> getCollection() {
        if (collection == null)
            collection = Service.getDatabase().getCollection(Tag.class.getName());
        return collection;
    }

    public String getType() {
        return type;
    }

    public static Tag load(Document doc) {
        Tag user = new Tag();
        user.id = doc.getObjectId("_id");
        user.owner = doc.getObjectId("owner");
        user.type = doc.getString("type");
        user.filter = (Document) doc.get("filter");
        user.search = (List<String>) doc.get("search");
        return user;
    }

    public Tag save() {
        Document doc = new Document();

        doc.put("search", search);
        doc.put("owner", owner);
        doc.put("type", type);
        doc.put("filter", filter);

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

    public static Tag findById(ObjectId id) {
        Document doc = new Document("_id", id);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? load(doc) : null;
    }

    public Tag() {

    }

    public static List<Tag> listByOwner(ObjectId id) {
        Document doc = new Document("owner", id);
        MongoCursor<Document> iter = getCollection().find(doc).iterator();
        List<Tag> ret = new ArrayList<Tag>();
        while (iter.hasNext()) {
            ret.add(Tag.load(iter.next()));
        }

        return ret;
    }

    public static List<Tag> search(String search, Integer offset, Integer limit, String type, List<KeyValue<String>> filters) {
        Document query = new Document();

        for(KeyValue<String> kvp : filters){
            query.append("filter."+kvp.getKey(), kvp.getValue());
        }

        if (search != null) {
            query.append("search", Pattern.compile(search));
        }
        if (type != null) {
            query.append("type", type);
        }

        FindIterable<Document> find = getCollection().find(query);
        if (offset != null) {
            find.skip(offset);
        }
        if (limit != null) {
            find.limit(limit);
        }

        MongoCursor<Document> iter = find.iterator();
        List<Tag> ret = new ArrayList<Tag>();
        while (iter.hasNext()) {
            ret.add(Tag.load(iter.next()));
        }
        return ret;

    }

    public static void deleteByOwner(ObjectId owner) {
        getCollection().deleteMany(new Document("owner", owner));
    }

    public Tag(ObjectId owner, String[] search) {
        this.owner = owner;
        this.search = Arrays.asList(search);
    }

    public static Long countByValue(String search, String type, List<KeyValue<String>> filters) {
        Document query = new Document();
        for(KeyValue<String> kvp : filters){
            query.append("filter."+kvp.getKey(), kvp.getValue());
        }
        if (search != null) {
            query.append("search", Pattern.compile(search));
        }
        if (type != null) {
            query.append("type", type);
        }

        return getCollection().count(query);
    }

    public static Long count(List<KeyValue<String>> filters) {
        Document query = new Document();
        for(KeyValue<String> kvp : filters){
            query.append("filter."+kvp.getKey(), kvp.getValue());
        }

        return getCollection().count(query);
    }
}