package services;

import exceptions.ServiceException;
import models.Attribute;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * The Class AuthenticateUserService.
 */
public class ListOwnerAttributesService extends Service<List<Attribute>> {

	private ObjectId user;
	private ObjectId caller;

	public ListOwnerAttributesService(String caller,String userId) {
		this.user =  new ObjectId(userId);
		this.caller = new ObjectId(caller);
	}


	@Override
	public List<Attribute> dispatch() throws ServiceException {
		return Attribute.listByOwner(user);
	}

	@Override
	public boolean canExecute() {
		return caller!=null;
	}

}
