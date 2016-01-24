package services;

import dtos.KeyValue;
import exceptions.ServiceException;
import models.*;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO: Auto-generated Javadoc

/**
 * The Class AuthenticateUserService.
 */
public class SearchGroupCandidatesService extends Service<JSONArray> {

    private final ObjectId user;
    private final ObjectId group;
    private final String query;

    public SearchGroupCandidatesService(String userId, String groupId, String query) {
        this.user = new ObjectId(userId);
        this.group = new ObjectId(groupId);
        this.query = query.toLowerCase();
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public JSONArray dispatch() throws ServiceException {
        JSONArray ans = new JSONArray();
        Set<ObjectId> relations = new HashSet<ObjectId>();

        for (Relation u : new ListRelationsService(user.toString()).execute()) {
            if (Membership.findByUserGroup(u.getTo(), group) == null) {
                relations.add(u.getTo());
            }
        }

        List<KeyValue<String>> filter = new ArrayList<KeyValue<String>>();
        filter.add(new KeyValue<String>("type",User.class.getName()));


        List<List<KeyValue<String>>> filters = new ArrayList<>();
        filters.add(filter);

        for(Search m : Search.search(query,null,null,filters)) {
            if (relations.contains(m.getOwner())) {
                JSONObject props = new JSONObject();
                List<Attribute> attrs = Attribute.listByOwner(m.getOwner());
                for (Attribute a : attrs) {
                    props.put(a.getKey(), a.getValue());
                }
                props.put("id", m.getOwner());
                ans.put(props);
            }
        }

        return ans;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {
        return user != null && group != null;
    }

}
