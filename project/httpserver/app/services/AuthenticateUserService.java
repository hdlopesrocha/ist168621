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
    private ObjectId userId;

    /**
     * The password.
     */
    private String password;

    public AuthenticateUserService(final String userId, final String password) {
        this.userId = new ObjectId(userId);
        this.password = password;
    }


    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public User dispatch() {
        User user = User.findById(userId);
        return user != null && user.check(password) ? user : null;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {
        return userId != null && password != null;
    }

}
