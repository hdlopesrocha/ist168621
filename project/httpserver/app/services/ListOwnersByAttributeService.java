package services;


import exceptions.ServiceException;
import models.Data;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;



/**
 * The Class ListOwnersByAttributeService.
 */
public class ListOwnersByAttributeService extends Service<List<String>> {

    /** The user. */
    private ObjectId user;
    
    /** The key. */
    private String key;
    
    /** The value. */
    private String value;

    /**
     * Instantiates a new list owners by attribute service.
     *
     * @param callerId the caller id
     * @param key the key
     * @param value the value
     */
    public ListOwnersByAttributeService(String callerId, String key, String value) {
        this.user = new ObjectId(callerId);
        this.key = key;
        this.value = value;
    }

    /* (non-Javadoc)
     * @see services.Service#dispatch()
     */
    @Override
    public List<String> dispatch() throws ServiceException {
        List<String> ret = new ArrayList<String>();
        for (Data attr : Data.listByKeyValue(key, value)) {
            ret.add(attr.getOwner().toString());
        }

        return ret;
    }

    /* (non-Javadoc)
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {

        return user != null;
    }
}
