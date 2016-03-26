package services;

import com.mongodb.client.FindIterable;
import models.TimeAnnotation;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;



/**
 * The Class ListTimeTagsService.
 */
public class ListTimeTagsService extends Service<List<TimeAnnotation>> {

    /** The caller. */
    private final ObjectId caller;
    
    /** The gid. */
    private final ObjectId gid;

    /**
     * Instantiates a new list time tags service.
     *
     * @param email the email
     * @param gid the gid
     */
    public ListTimeTagsService(String email, String gid) {
        this.caller = new ObjectId(email);
        this.gid = new ObjectId(gid);
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public List<TimeAnnotation> dispatch() {
        FindIterable<Document> iter = TimeAnnotation.getCollection().find(new Document("gid", gid));
        List<TimeAnnotation> tags = new ArrayList<TimeAnnotation>();
        for (Document doc : iter) {
            TimeAnnotation tag = TimeAnnotation.load(doc);
            tags.add(tag);
        }

        return tags;
    }

    /* (non-Javadoc)
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {
        return caller != null;
    }


}
