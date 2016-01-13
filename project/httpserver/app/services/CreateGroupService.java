package services;

import dtos.AttributeDto;
import exceptions.ServiceException;
import main.Global;
import models.*;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc

/**
 * The Class AuthenticateUserService.
 */
public class CreateGroupService extends Service<Group> {

    private final ObjectId caller;
    private final List<AttributeDto> attributes;

    public CreateGroupService(String uid, List<AttributeDto> attributes) {
        this.caller = new ObjectId(uid);
        this.attributes = attributes;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public Group dispatch() throws ServiceException {
        Group group = new Group(Group.Visibility.PRIVATE);
        group.save();
        Membership membership = new Membership(caller, group.getId());
        membership.save();
        Global.manager.getRoom(membership.getId().toString());
        attributes.add(new AttributeDto("type",Group.class.getName(), AttributeDto.Access.READ, AttributeDto.Visibility.PUBLIC,false,false,true));
        new SetAttributesService(group.getId().toString(),group.getId().toString(),attributes).execute();
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
