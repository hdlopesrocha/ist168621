package services;

import models.Relation;
import models.User;
import org.bson.types.ObjectId;

// TODO: Auto-generated Javadoc

/**
 * The Class AuthenticateUserService.
 */
public class CreateRelationService extends Service<Void> {

    private final User from;
    private final User to;


    public CreateRelationService(String from, String to) {
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
        Relation rel = Relation.findByEndpoint(from.getId(), to.getId());
        if (rel == null) {
            rel = new Relation(from.getId(), to.getId());
            rel.save();
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
