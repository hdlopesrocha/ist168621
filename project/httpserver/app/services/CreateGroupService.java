package services;

import dtos.AttributeDto;
import exceptions.ServiceException;
import models.Data;
import models.Group;
import models.GroupMembership;
import org.bson.types.ObjectId;

import java.util.HashSet;
import java.util.List;


/**
 * The Class CreateGroupService.
 */
public class CreateGroupService extends Service<Group> {

    /** The caller. */
    private final ObjectId caller;
    
    /** The attributes. */
    private final List<AttributeDto> attributes;
    
    /** The visibility. */
    private final Group.Visibility visibility;

    /**
     * Instantiates a new creates the group service.
     *
     * @param uid the uid
     * @param visibility the visibility
     * @param attributes the attributes
     */
    public CreateGroupService(String uid, Group.Visibility visibility, List<AttributeDto> attributes) {
        this.caller = new ObjectId(uid);
        this.attributes = attributes;
        this.visibility = visibility;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public Group dispatch() throws ServiceException {
        Group group = new Group(visibility);
        group.save();
        GroupMembership membership = new GroupMembership(caller, group.getId());
        membership.save();
        attributes.add(new AttributeDto("type", Group.class.getName(), false, false, true, null,new HashSet<String>()));
        new Data(group.getId(), attributes).save();
        return group;
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
