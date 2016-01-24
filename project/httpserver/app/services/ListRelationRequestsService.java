package services;

import models.Relation;
import models.User;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;



/**
 * The Class ListRelationRequestsService.
 */
public class ListRelationRequestsService extends Service<List<User>> {

    /** The caller. */
    private final User caller;


    /**
     * Instantiates a new list relation requests service.
     *
     * @param uid the uid
     */
    public ListRelationRequestsService(String uid) {
        this.caller = User.findById(new ObjectId(uid));

    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public List<User> dispatch() {
        List<User> ret = new ArrayList<User>();
        for (Relation r : Relation.listTo(caller.getId())) {
            Relation r2 = Relation.findByEndpoint(caller.getId(), r.getFrom());
            if (r2 == null) {
                ret.add(User.findById(r.getFrom()));
            }
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {
        return caller != null;
    }

}
