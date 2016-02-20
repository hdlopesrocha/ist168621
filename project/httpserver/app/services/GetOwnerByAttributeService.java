package services;

import exceptions.ServiceException;
import models.Data;


/**
 * The Class GetOwnerByAttributeService.
 */
public class GetOwnerByAttributeService extends Service<String> {

    /** The key. */
    private String key;
    
    /** The value. */
    private Object value;

    /**
     * Instantiates a new gets the owner by attribute service.
     *
     * @param key the key
     * @param value the value
     */
    public GetOwnerByAttributeService(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    /* (non-Javadoc)
     * @see services.Service#dispatch()
     */
    @Override
    public String dispatch() throws ServiceException {
        Data at = Data.getByKeyValue(key, value);
        return at != null ? at.getOwner().toString() : null;
    }

    /* (non-Javadoc)
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {
        return true;
    }

}
