package services;

import com.mongodb.client.FindIterable;
import models.Attribute;
import models.User;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

// TODO: Auto-generated Javadoc

/**
 * The Class AuthenticateUserService.
 */
public class ListUsersService extends Service<String> {

    private final ObjectId caller;

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

    @Override
    public boolean canExecute() {
        return caller != null;
    }

}
