package services;


import dtos.AttributeDto;
import exceptions.ServiceException;
import models.Data;
import models.User;

import java.util.HashSet;
import java.util.List;


/**
 * The Class RegisterUserService.
 */
public class RegisterUserService extends Service<User> {

    /** The password. */
    private String password;
    
    /** The attributes. */
    private List<AttributeDto> attributes;
    /**
     * Instantiates a new register user service.
     *
     * @param password the password
     * @param attributes the attributes
     */
    public RegisterUserService(final String password, List<AttributeDto> attributes) {
        this.password = password;
        this.attributes = attributes;
    }


    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public synchronized User dispatch() throws ServiceException {
        attributes.add(new AttributeDto("type", User.class.getName(), false, false, true,null,new HashSet<String>()));

        User user = new User(password);
        user.save();
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
