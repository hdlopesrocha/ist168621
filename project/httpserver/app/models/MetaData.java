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
public class MetaData {

    private List<String> search;
    private ObjectId id = null;
    private ObjectId owner = null;
    private Document filter= null;

    public MetaData(ObjectId owner, List<String> search, Document filter) {
        this.owner = owner;
        this.search = search;
        this.filter = filter;
    }

    public ObjectId getOwner() {
        return owner;
    }


    private static MongoCollection<Document> collection;

    public static MongoCollection<Document> getCollection() {
        if (collection == null)
            collection = Service.getDatabase().getCollection(MetaData.class.getName());
        return collection;
    }

    public static MetaData load(Document doc) {
        MetaData user = new MetaData();
        user.id = doc.getObjectId("_id");
        user.owner = doc.getObjectId("owner");
        user.filter = (Document) doc.get("filter");
        user.search = (List<String>) doc.get("search");
        return user;
    }

    public MetaData save() {
        Document doc = new Document();

        doc.put("search", search);
        doc.put("owner", owner);
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

    public static MetaData findById(ObjectId id) {
        Document doc = new Document("_id", id);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? load(doc) : null;
    }

    public MetaData() {

    }

    public static List<MetaData> listByOwner(ObjectId id) {
        Document doc = new Document("owner", id);
        MongoCursor<Document> iter = getCollection().find(doc).iterator();
        List<MetaData> ret = new ArrayList<MetaData>();
        while (iter.hasNext()) {
            ret.add(MetaData.load(iter.next()));
        }

        return ret;
    }

    public static List<MetaData> search(String search, Integer offset, Integer limit, List<KeyValue<String>> filters) {
        Document query = new Document();

        for(KeyValue<String> kvp : filters){
            query.append("filter."+kvp.getKey(), kvp.getValue());
        }

        if (search != null) {
            query.append("search", Pattern.compile(search));
        }


        FindIterable<Document> find = getCollection().find(query);
        if (offset != null) {
            find.skip(offset);
        }
        if (limit != null) {
            find.limit(limit);
        }

        MongoCursor<Document> iter = find.iterator();
        List<MetaData> ret = new ArrayList<MetaData>();
        while (iter.hasNext()) {
            ret.add(MetaData.load(iter.next()));
        }
        return ret;

    }

    public static void deleteByOwner(ObjectId owner) {
        getCollection().deleteMany(new Document("owner", owner));
    }


    public MetaData(ObjectId owner, List<String> search) {
        this.owner = owner;
        this.search = search;
    }

    public MetaData(ObjectId owner, String[] search) {
        this.owner = owner;
        this.search = Arrays.asList(search);
    }

    public static Long count(String search, List<KeyValue<String>> filters) {
        Document query = new Document();
        for(KeyValue<String> kvp : filters){
            query.append("filter."+kvp.getKey(), kvp.getValue());
        }
        if (search != null) {
            query.append("search", Pattern.compile(search));
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