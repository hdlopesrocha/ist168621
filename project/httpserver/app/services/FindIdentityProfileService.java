package services;

import models.IdentityProfile;


/**
 * The Class AuthenticateUserService.
 */
public class FindIdentityProfileService extends Service<String> {

    private String key;
    private String value;

    public FindIdentityProfileService(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public String dispatch() {
        IdentityProfile user = IdentityProfile.find("data." + key, value);
        return user != null ? user.getOwner().toString() : null;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {

        return key != null && value != null;
    }

}