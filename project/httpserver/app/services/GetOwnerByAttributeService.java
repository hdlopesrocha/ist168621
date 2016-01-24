package services;

import exceptions.ServiceException;
import models.Data;

/**
 * The Class AuthenticateUserService.
 */
public class GetOwnerByAttributeService extends Service<String> {

    private String key;
    private Object value;

    public GetOwnerByAttributeService(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String dispatch() throws ServiceException {
        Data at = Data.getByKeyValue(key, value);
        return at != null ? at.getOwner().toString() : null;
    }

    @Override
    public boolean canExecute() {
        return true;
    }

}
