package services;

import dtos.AttributeDto;
import org.bson.types.ObjectId;

import java.util.List;


// TODO: Auto-generated Javadoc

/**
 * The Class AuthenticateUserService.
 */
public class UpdateUserService extends Service<Void> {

    private List<AttributeDto> attributes;
    private ObjectId user;

    public UpdateUserService(final String uid, final List<AttributeDto> attributes) {

        this.user = uid != null ? new ObjectId(uid) : null;

        this.attributes = attributes;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public Void dispatch() {

        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {
        return user != null;
    }


}
