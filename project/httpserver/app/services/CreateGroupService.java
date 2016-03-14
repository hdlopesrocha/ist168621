package services;

import dtos.AttributeDto;
import dtos.PermissionDto;
import exceptions.ServiceException;
import models.*;
import org.bson.types.ObjectId;

import java.util.*;


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

    private final List<PermissionDto> permissions;
    /**
     * Instantiates a new creates the group service.
     *
     * @param uid the uid
     * @param visibility the visibility
     * @param attributes the attributes
     */
    public CreateGroupService(String uid, Group.Visibility visibility, List<PermissionDto> permissions, List<AttributeDto> attributes) {
        this.caller = new ObjectId(uid);
        this.attributes = attributes;
        this.visibility = visibility;
        this.permissions = permissions;
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
        attributes.add(new AttributeDto("type", Group.class.getName(), false, false, true));
        permissions.add(new PermissionDto("type", new HashSet<String>(),new HashSet<String>()));
        new Data(group.getId(), attributes).save();

        Map<String, DataPermission.Entry> realPermissions = new TreeMap<>();
        for(PermissionDto p : permissions){
            Set<ObjectId> readSet = new HashSet<>();
            Set<ObjectId> writeSet = new HashSet<>();
            for(String str : p.getReadSet()){
                readSet.add(new ObjectId(str));
            }
            for(String str : p.getWriteSet()){
                writeSet.add(new ObjectId(str));
            }
            realPermissions.put(p.getKey(), new DataPermission.Entry(readSet,writeSet));
        }

        new DataPermission(group.getId(),realPermissions, attributes).save();
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
