package services;

import dtos.KeyValue;
import exceptions.ServiceException;
import models.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



/**
 * The Class SearchGroupCandidatesService.
 */
public class SearchGroupCandidatesService extends Service<JSONArray> {

    /** The user. */
    private final ObjectId user;
    
    /** The group. */
    private final ObjectId group;
    
    /** The query. */
    private final String query;

    /**
     * Instantiates a new search group candidates service.
     *
     * @param userId the user id
     * @param groupId the group id
     * @param query the query
     */
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
            if (GroupMembership.findByUserGroup(u.getTo(), group) == null) {
                relations.add(u.getTo());
            }
        }

        List<KeyValue<String>> filter = new ArrayList<KeyValue<String>>();
        filter.add(new KeyValue<String>("type", User.class.getName()));

        List<List<KeyValue<String>>> filters = new ArrayList<>();
        filters.add(filter);

        for (Data m : Data.search(query, null, null, filters)) {
            if (relations.contains(m.getOwner())) {
                Data data =Data.findByOwner(m.getOwner());
                if(data!=null) {


                    JSONObject props = new JSONObject();
                    for(Attribute d : data.getData()){
                        props.put(d.getKey(),d.getValue());
                    }


                    props.put("id", m.getOwner());
                    ans.put(props);
                }
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
