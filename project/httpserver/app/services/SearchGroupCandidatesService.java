package services;

import models.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

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
        for (User u : user.getRelations()) {
            if (Membership.findByUserGroup(u.getId(), group.getId()) == null) {
                IdentityProfile idProfile = IdentityProfile.findByOwner(u.getId());
                PublicProfile pubProfile = PublicProfile.findByOwner(u.getId());
                boolean match = false;
                Document doc = idProfile.getData();
                for (String key : doc.keySet()) {
                    String value = doc.getString(key).toLowerCase();
                    if (value.contains(query)) {
                        match = true;
                        break;
                    }
                }
                String name = pubProfile.getData().getString("name");
                if (!match && name != null) {
                    if (name.toLowerCase().contains(query)) {
                        match = true;
                    }
                }
                if (match) {
                    JSONObject props = new JSONObject(pubProfile.getData().toJson());
                    JSONObject identityProperties = new JSONObject(idProfile.getData().toJson());
                    for (String key : idProfile.getData().keySet()) {
                        props.put(key, identityProperties.get(key));
                    }
                    props.put("id", u.getId().toString());
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
