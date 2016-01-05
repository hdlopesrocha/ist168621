package services;

import models.Attribute;
import models.Group;
import models.Membership;
import models.User;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO: Auto-generated Javadoc

/**
 * The Class AuthenticateUserService.
 */
public class SearchGroupCandidatesService extends Service<JSONArray> {

    private User user;
    private Group group;
    private String query;

    public SearchGroupCandidatesService(String userId, String groupId, String query) {
        this.user = User.findById(new ObjectId(userId));
        this.group = Group.findById(new ObjectId(groupId));
        this.query = query.toLowerCase();
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public JSONArray dispatch() {
        JSONArray ans = new JSONArray();
        List<User> relations = user.getRelations();
        List<Attribute> attributes = Attribute.searchByValue(query);

        Set<ObjectId> matches = new HashSet<ObjectId>();

        for(Attribute a : attributes) {
            if(!matches.contains(a.getOwner())) {
                for (User u : relations) {
                    if (Membership.findByUserGroup(u.getId(), group.getId()) == null) {

                      matches.add(a.getOwner());

                    }
                }
            }
        }

        for(ObjectId oid : matches){
            JSONObject props = new JSONObject();
            List<Attribute> attrs = Attribute.listByOwner(oid);
            for(Attribute a : attrs){
                props.put(a.getKey(),a.getValue());
            }


            props.put("id", oid.toString());
            ans.put(props);
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
