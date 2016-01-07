package models;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
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


          private static final String TYPE = "common.Tag";


        private List<String> value;
        private ObjectId id = null;

    public Tag(ObjectId owner, List<String> value) {
        this.owner = owner;
        this.value = value;
    }


    public ObjectId getOwner() {
            return owner;
        }

        private ObjectId owner = null;


        public Object getValue() {
            return value;
        }


        private static MongoCollection<Document> collection;

        public static MongoCollection<Document> getCollection() {
            if(collection==null)
                collection = Service.getDatabase().getCollection(TYPE);
            return collection;
        }

        private static Tag load(Document doc) {
            Tag user = new Tag();
            user.id = doc.getObjectId("_id");
            user.owner = doc.getObjectId("owner");
            user.value = (List<String>) doc.get("value");
            return user;
        }

        public Tag save() {
            Document doc = new Document();

            doc.put("value", value);
            doc.put("owner", owner);

            if(id==null){
                getCollection().insertOne(doc);
                id = doc.getObjectId("_id");
            }
            else {
                doc.put("_id", id);
                getCollection().findOneAndReplace(new Document("_id",id),doc);
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

        private Tag() {

        }



        public static List<Tag> listByOwner(ObjectId id) {
            Document doc = new Document("owner", id);
            MongoCursor<Document> iter = getCollection().find(doc).iterator();
            List<Tag> ret = new ArrayList<Tag>();
            while (iter.hasNext()){
                ret.add(Tag.load(iter.next()));
            }

            return ret;
        }

        public static List<Tag> searchByValue(String value, Integer offset, Integer limit){
            Pattern regex = Pattern.compile(value);
            Document query = new Document("value", regex);
            FindIterable<Document> find = getCollection().find(query);
            if(offset!=null){
                find.skip(offset);
            }
            if(limit!=null){
                find.limit(limit);
            }

            MongoCursor<Document> iter = find.iterator();
            List<Tag> ret = new ArrayList<Tag>();
            while (iter.hasNext()){
                ret.add(Tag.load(iter.next()));
            }
            return ret;

        }

    public static void deleteByOwner(ObjectId owner){
        getCollection().deleteMany(new Document("owner",owner));
    }


        public Tag(ObjectId owner, String [] value) {
            this.owner = owner;
            this.value = Arrays.asList(value);
        }

    public static Long countByValue(String search) {
        Pattern regex = Pattern.compile(search,Pattern.CASE_INSENSITIVE);
        Document query = new Document("value", regex);
        return getCollection().count(query);
    }
}
