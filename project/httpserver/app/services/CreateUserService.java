package services;

import dtos.AttributeDto;
import exceptions.ServiceException;
import main.Tools;
import models.Attribute;
import models.User;

import java.util.ArrayList;
import java.util.List;


// TODO: Auto-generated Javadoc

/**
 * The Class AuthenticateUserService.
 */
public class CreateUserService extends Service<User> {

    private String password;
    private List<AttributeDto> properties;
    private List<String> permissions = new ArrayList<String>();

    public CreateUserService(final String password, final List<AttributeDto> properties) {
        this.properties = properties;
        this.password = password;
    }

    public CreateUserService addPermission(String permission) {
        permissions.add(permission);
        return this;
    }


    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public synchronized User dispatch() throws ServiceException {

        String token = Tools.getRandomString(32);

        while (User.findByToken(token) != null) {
            token = Tools.getRandomString(32);
        }

        User user = new User(password);
        user.setToken(token);
        user.save();
        for(AttributeDto attr : properties){
            new Attribute(user.getId(),attr.getKey(),attr.getValue(),attr.isIdentifiable(),attr.isSearchable()).save();
        }

        return user;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {


        return password != null;
    }


}
