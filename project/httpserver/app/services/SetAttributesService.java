package services;

import dtos.AttributeDto;
import exceptions.ServiceException;
import models.Data;
import org.bson.types.ObjectId;

import java.util.List;


/**
 * The Class SetAttributesService.
 */
public class SetAttributesService extends Service<Data> {

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
		this.owner = owner !=null ? new ObjectId(owner) : null;
		this.attributes = attributes;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see services.Service#dispatch()
	 */
	@Override
	public synchronized Data dispatch() throws ServiceException {
		Data.deleteByOwner(owner);
		Data data = new Data(owner,attributes).save();
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
