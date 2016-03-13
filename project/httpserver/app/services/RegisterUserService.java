package services;


import dtos.AttributeDto;
import dtos.PermissionDto;
import exceptions.ServiceException;
import models.Data;
import models.DataPermission;
import models.User;
import org.bson.types.ObjectId;

import java.util.*;


/**
 * The Class RegisterUserService.
 */
public class RegisterUserService extends Service<User> {

    /** The password. */
    private String password;
    
    /** The attributes. */
    private List<AttributeDto> attributes;
private List<PermissionDto> permissions;
    /**
     * Instantiates a new register user service.
     *
     * @param password the password
     * @param attributes the attributes
     */
    public RegisterUserService(final String password, List<PermissionDto> permissions, List<AttributeDto> attributes) {
        this.password = password;
        this.attributes = attributes;
        this.permissions = permissions;
    }


    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public synchronized User dispatch() throws ServiceException {
        attributes.add(new AttributeDto("type", User.class.getName(), false, false, true));
        permissions.add(new PermissionDto("type",new HashSet<String>(),new HashSet<String>()));



        User user = new User(password);
        user.save();


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

        new DataPermission(user.getId(),realPermissions, attributes).save();
        new Data(user.getId(), attributes).save();

        return user;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {
        for(AttributeDto attr : attributes){
            if(attr.isIdentifiable()){
                Data data = Data.getByKeyValue(attr.getKey(),attr.getValue());
                if(data!=null){
                    return false;
                }
            }
        }
        return password != null;
    }

}
