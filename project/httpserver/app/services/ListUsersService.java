package services;

import com.mongodb.client.FindIterable;
import models.Attribute;
import models.User;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;



/**
 * The Class ListUsersService.
 */
public class ListUsersService extends Service<String> {

    /** The caller. */
    private final ObjectId caller;

    /**
     * Instantiates a new list users service.
     *
     * @param caller the caller
     */
    public ListUsersService(String caller) {
        this.caller = new ObjectId(caller);
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public String dispatch() {


        FindIterable<Document> iter = User.getCollection().find();
        JSONArray array = new JSONArray();

        for (Document doc : iter) {
            User user = User.load(doc);
            JSONObject inc = new JSONObject();
            List<Attribute> attributes = Attribute.listByOwner(user.getId());
            for (Attribute attribute : attributes) {
                inc.put(attribute.getKey(), attribute.getValue());
            }
            array.put(inc);
        }

        return array.toString();
    }

    /* (non-Javadoc)
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {
        return caller != null;
    }

}
