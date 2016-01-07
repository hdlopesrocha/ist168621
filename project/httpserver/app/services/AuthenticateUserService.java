package services;

import models.User;
import org.bson.types.ObjectId;


/**
 * The Class AuthenticateUserService.
 */
public class AuthenticateUserService extends Service<User> {

    /**
     * The user.
     */
    private final ObjectId user;

    /**
     * The password.
     */
    private final String password;


    public AuthenticateUserService(final String userId, final String password) {
        this.user = new ObjectId(userId);
        this.password = password;
    }


    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public User dispatch() {
        User u = User.findById(user);
        if (u != null && u.check(password)) {
            return u;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {
        return user != null && password != null;
    }

}
