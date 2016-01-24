package services;

import exceptions.ServiceException;
import models.User;
import org.bson.types.ObjectId;


/**
 * The Class ChangeUserPasswordService.
 */
public class ChangeUserPasswordService extends Service<Void> {

    /** The password. */
    private final String password;
    
    /** The old password. */
    private final String oldPassword;
    
    /** The user id. */
    private final ObjectId userId;
    
    /** The user. */
    private User user;

    /**
     * Instantiates a new change user password service.
     *
     * @param uid the uid
     * @param oldPassword the old password
     * @param password the password
     */
    public ChangeUserPasswordService(final String uid, final String oldPassword, final String password) {
        this.userId = uid != null ? new ObjectId(uid) : null;
        this.password = password;
        this.oldPassword = oldPassword;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public Void dispatch() {

        user.setPassword(password);
        user.save();
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {
        try {
            user = new AuthenticateUserService(userId.toString(), oldPassword).execute();
        } catch (ServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return userId != null && user != null && password != null;
    }


}
