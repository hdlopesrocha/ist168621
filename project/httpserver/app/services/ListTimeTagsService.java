package services;

import com.mongodb.client.FindIterable;
import models.TimeTag;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc

/**
 * The Class AuthenticateUserService.
 */
public class ListTimeTagsService extends Service<List<TimeTag>> {

    private final ObjectId caller;
    private final ObjectId gid;

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
    public List<TimeTag> dispatch() {
        FindIterable<Document> iter = TimeTag.getCollection().find(new Document("gid", gid));
        List<TimeTag> tags = new ArrayList<TimeTag>();
        for (Document doc : iter) {
            TimeTag tag = TimeTag.load(doc);
            tags.add(tag);
        }

        return tags;
    }

    @Override
    public boolean canExecute() {
        return caller != null;
    }


}
