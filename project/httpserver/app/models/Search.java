package models;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import dtos.AttributeDto;
import dtos.KeyValue;
import org.bson.Document;
import org.bson.types.ObjectId;
import services.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by hdlopesrocha on 06-01-2016.
 */
public class Search {

    private static MongoCollection<Document> collection;
    private List<String> search;
    private ObjectId id = null;
    private ObjectId owner = null;
    private Document filter = null;

    public Search(ObjectId owner, List<AttributeDto> attributes) {

        this.search = new ArrayList<String>();
        this.filter = new Document();

        for (AttributeDto attr : attributes) {
            if (attr.isSearchable()) {
                search.add(attr.getValue().toString().toLowerCase());
            }
            if (attr.isAggregator()) {
                filter.append(attr.getKey(), attr.getValue());
            }
        }
        this.owner = owner;
    }

    private Search() {

    }

    public static MongoCollection<Document> getCollection() {
        if (collection == null)
            collection = Service.getDatabase().getCollection(Search.class.getName());
        return collection;
    }

    public static Search load(Document doc) {
        Search user = new Search();
        user.id = doc.getObjectId("_id");
        user.owner = doc.getObjectId("owner");
        user.filter = (Document) doc.get("filter");
        user.search = (List<String>) doc.get("search");
        return user;
    }

    public static Search findById(ObjectId id) {
        Document doc = new Document("_id", id);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? load(doc) : null;
    }

    public static List<Search> listByOwner(ObjectId id) {
        Document doc = new Document("owner", id);
        MongoCursor<Document> iter = getCollection().find(doc).iterator();
        List<Search> ret = new ArrayList<Search>();
        while (iter.hasNext()) {
            ret.add(Search.load(iter.next()));
        }

        return ret;
    }

    private static Document buildQuery(List<List<KeyValue<String>>> filters) {
        Document query = new Document();

        if (filters.size() > 0) {
            List<Document> or = new ArrayList<Document>();
            for (List<KeyValue<String>> list : filters) {
                Document and = new Document();

                for (KeyValue<String> kvp : list) {
                    and.append("filter." + kvp.getKey(), kvp.getValue());
                }
                or.add(and);
            }
            query.append("$or", or);
        }
        return query;
    }

    public static List<Search> search(String search, Integer offset, Integer limit,
                                      List<List<KeyValue<String>>> filters) {
        Document query = buildQuery(filters);
        System.out.println("SEARCH: " + query.toJson());
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
        List<Search> ret = new ArrayList<Search>();
        while (iter.hasNext()) {
            ret.add(Search.load(iter.next()));
        }
        return ret;

    }

    public static void deleteByOwner(ObjectId owner) {
        getCollection().deleteMany(new Document("owner", owner));
    }

    public static Long countByValue(String search, List<List<KeyValue<String>>> filters) {
        Document query = buildQuery(filters);

        if (search != null) {
            query.append("search", Pattern.compile(search));
        }

        return getCollection().count(query);
    }

    public static Long count(List<List<KeyValue<String>>> filters) {
        Document query = buildQuery(filters);

        return getCollection().count(query);
    }

    public ObjectId getOwner() {
        return owner;
    }

    public Search save() {
        Document doc = new Document();
        doc.put("owner", owner);
        doc.put("filter", filter);
        doc.put("search", search);


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


}