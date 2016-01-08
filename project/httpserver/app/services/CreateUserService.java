package services;

import dtos.AttributeDto;
import exceptions.ServiceException;
import main.Tools;
import models.User;

import java.util.ArrayList;
import java.util.List;


// TODO: Auto-generated Javadoc

/**
 * The Class AuthenticateUserService.
 */
public class CreateUserService extends Service<User> {

    private final String password;
    private final List<String> permissions = new ArrayList<String>();
    private final List<AttributeDto> attributes;

    public CreateUserService(final String password, List<AttributeDto> attributes) {
        this.password = password;
        this.attributes = attributes;
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

        new SetAttributesService(user.getId().toString(),user.getId().toString(),attributes).execute();

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
