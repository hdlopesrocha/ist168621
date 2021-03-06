package services;

import models.Relation;
import models.User;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;



/**
 * The Class ListRelationsService.
 */
public class ListRelationsService extends Service<List<Relation>> {

    /** The caller. */
    private final User caller;


    /**
     * Instantiates a new list relations service.
     *
     * @param uid the uid
     */
    public ListRelationsService(String uid) {
        this.caller = User.findById(new ObjectId(uid));

    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public List<Relation> dispatch() {
        List<Relation> ret = new ArrayList<Relation>();

        for (Relation r : Relation.listFrom(caller.getId())) {
            Relation r2 = Relation.findByEndpoint(r.getTo(), caller.getId());
            if (r2 != null) {
                ret.add(r);
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
