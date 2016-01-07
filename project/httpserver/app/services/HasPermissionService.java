package services;

import models.Permission;
import org.bson.types.ObjectId;


/**
 * The Class AuthenticateUserService.
 */
public class HasPermissionService extends Service<Boolean> {

    /**
     * The user.
     */
    private ObjectId source, target;

    private final String name;

    public HasPermissionService(final String source, final String name) {
        if (source != null)
            this.source = new ObjectId(source);
        this.name = name;

    }

    public HasPermissionService(final String source, final String name, final String target) {
        if (source != null)
            this.source = new ObjectId(source);
        this.name = name;
        if (target != null)
            this.target = new ObjectId(target);
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public Boolean dispatch() {
        return Permission.find(source, name, target) != null;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {
        return name != null && source != null;
    }


}
