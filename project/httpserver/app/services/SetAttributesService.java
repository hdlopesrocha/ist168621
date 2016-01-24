package services;


import dtos.AttributeDto;
import exceptions.ServiceException;
import models.Search;
import org.bson.types.ObjectId;

import java.util.List;



/**
 * The Class SetAttributesService.
 */
public class SetAttributesService extends Service<Search> {

    /** The owner. */
    private ObjectId owner;
    
    /** The attributes. */
    private List<AttributeDto> attributes;

    /**
     * Instantiates a new sets the attributes service.
     *
     * @param owner the owner
     * @param attributes the attributes
     */
    public SetAttributesService(final String owner, List<AttributeDto> attributes) {
        this.owner = owner != null ? new ObjectId(owner) : null;
        this.attributes = attributes;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public synchronized Search dispatch() throws ServiceException {
        Search.deleteByOwner(owner);
        Search data = new Search(owner, attributes).save();
        return data;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {
        return owner != null;
    }

}
