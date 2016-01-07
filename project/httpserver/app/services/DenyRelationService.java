package services;

import models.Relation;
import models.User;
import org.bson.types.ObjectId;

// TODO: Auto-generated Javadoc

/**
 * The Class AuthenticateUserService.
 */
public class DenyRelationService extends Service<Void> {

    private final User from;
    private final User to;

    public DenyRelationService(String from, String to) {
        this.from = User.findById(new ObjectId(from));
        this.to = User.findById(new ObjectId(to));
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public Void dispatch() {
        Relation rel1 = Relation.findByEndpoint(from.getId(), to.getId());
        Relation rel2 = Relation.findByEndpoint(to.getId(), from.getId());

        if (rel2 != null) {
            rel2.delete();
        }
        if (rel1 != null) {
            rel1.delete();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {
        return from != null && to != null;
    }

}
