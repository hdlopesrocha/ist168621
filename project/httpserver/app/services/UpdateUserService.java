package services;

import dtos.AttributeDto;
import org.bson.types.ObjectId;

import java.util.List;




/**
 * The Class UpdateUserService.
 */
public class UpdateUserService extends Service<Void> {

    /** The attributes. */
    private List<AttributeDto> attributes;
    
    /** The user. */
    private ObjectId user;

    /**
     * Instantiates a new update user service.
     *
     * @param uid the uid
     * @param attributes the attributes
     */
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
