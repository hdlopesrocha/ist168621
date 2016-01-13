package models;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import main.Tools;
import org.bson.Document;
import org.bson.types.ObjectId;
import services.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class Group {

    public enum Visibility {PUBLIC, PRIVATE}


    private static MongoCollection<Document> collection;
    private ObjectId id = null;
    private String invite = null;
    private Visibility visibility;

    public Group(Visibility visibility ) {
        this.visibility = visibility;
    }

    private Group() {
    }

    private static MongoCollection<Document> getCollection() {
        if (collection == null)
            collection = Service.getDatabase().getCollection(Group.class.getName());
        return collection;
    }

    private static Group load(Document doc) {
        Group user = new Group();
        user.id = doc.getObjectId("_id");
        user.invite = doc.getString("invite");
        user.visibility = Visibility.valueOf(doc.getString("visibility"));
        return user;
    }

    public static Group findById(ObjectId id) {
        Document doc = new Document("_id", id);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? load(doc) : null;
    }

    public static List<Group> listAll() {
        FindIterable<Document> iter = getCollection().find(new Document());
        List<Group> ret = new ArrayList<Group>();
        for (Document doc : iter) {
            ret.add(load(doc));
        }
        return ret;
    }

    public static List<Group> search(User caller, String query) {
        Pattern regex = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
        Document doc = new Document("name", regex);
        FindIterable<Document> iter = getCollection().find(doc);
        Iterator<Document> i = iter.iterator();
        List<Group> ret = new ArrayList<Group>();
        while (i.hasNext()) {
            ret.add(Group.load(i.next()));
        }
        return ret;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void generateInvite() {
        invite = Tools.getRandomString(12);
    }

    public String getInvite() {
        return invite;
    }

    public boolean matchInvite(String attempt) {
        return invite != null && invite.equals(attempt);
    }

    public void save() {
        Document doc = new Document();
        if (id != null)
            doc.put("_id", id);

        doc.put("invite", invite);
        doc.put("visibility", visibility.toString());

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
        if (id != null)
            getCollection().deleteOne(new Document("_id", id));
    }

    public void deleteInvite() {
        invite = null;
    }


}