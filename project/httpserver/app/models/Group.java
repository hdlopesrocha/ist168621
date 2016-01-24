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


/**
 * The Class Group.
 */
public class Group {

    /** The collection. */
    private static MongoCollection<Document> collection;
    
    /** The id. */
    private ObjectId id = null;
    
    /** The invite. */
    private String invite = null;
    
    /** The visibility. */
    private Visibility visibility;
    
    /**
     * Instantiates a new group.
     *
     * @param visibility the visibility
     */
    public Group(Visibility visibility) {
        this.visibility = visibility;
    }

    /**
     * Instantiates a new group.
     */
    private Group() {
    }

    /**
     * Gets the collection.
     *
     * @return the collection
     */
    private static MongoCollection<Document> getCollection() {
        if (collection == null)
            collection = Service.getDatabase().getCollection(Group.class.getName());
        return collection;
    }

    /**
     * Load.
     *
     * @param doc the doc
     * @return the group
     */
    private static Group load(Document doc) {
        Group user = new Group();
        user.id = doc.getObjectId("_id");
        user.invite = doc.getString("invite");
        user.visibility = Visibility.valueOf(doc.getString("visibility"));
        return user;
    }

    /**
     * Find by id.
     *
     * @param id the id
     * @return the group
     */
    public static Group findById(ObjectId id) {
        Document doc = new Document("_id", id);
        FindIterable<Document> iter = getCollection().find(doc);
        doc = iter.first();
        return doc != null ? load(doc) : null;
    }

    /**
     * List all.
     *
     * @return the list
     */
    public static List<Group> listAll() {
        FindIterable<Document> iter = getCollection().find(new Document());
        List<Group> ret = new ArrayList<Group>();
        for (Document doc : iter) {
            ret.add(load(doc));
        }
        return ret;
    }

    /**
     * Search.
     *
     * @param caller the caller
     * @param query the query
     * @return the list
     */
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

    /**
     * Gets the visibility.
     *
     * @return the visibility
     */
    public Visibility getVisibility() {
        return visibility;
    }

    /**
     * Generate invite.
     */
    public void generateInvite() {
        invite = Tools.getRandomString(12);
    }

    /**
     * Gets the invite.
     *
     * @return the invite
     */
    public String getInvite() {
        return invite;
    }

    /**
     * Match invite.
     *
     * @param attempt the attempt
     * @return true, if successful
     */
    public boolean matchInvite(String attempt) {
        return invite != null && invite.equals(attempt);
    }

    /**
     * Save.
     */
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

    /**
     * Gets the id.
     *
     * @return the id
     */
    public ObjectId getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(ObjectId id) {
        this.id = id;
    }

    /**
     * Delete.
     */
    public void delete() {
        if (id != null)
            getCollection().deleteOne(new Document("_id", id));
    }

    /**
     * Delete invite.
     */
    public void deleteInvite() {
        invite = null;
    }

    /**
     * The Enum Visibility.
     */
    public enum Visibility {/** The public. */
PUBLIC, /** The private. */
 PRIVATE}


}