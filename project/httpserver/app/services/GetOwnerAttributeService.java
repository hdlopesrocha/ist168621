package services;

import exceptions.ServiceException;
import models.Attribute;
import models.Permission;
import org.bson.types.ObjectId;



/**
 * The Class AuthenticateUserService.
 */
public class GetOwnerAttributeService extends Service<Attribute> {

	private final ObjectId userId;
	private final String attr;

	public GetOwnerAttributeService(String userId, String attr) {
		this.attr = attr;
		this.userId =  new ObjectId(userId);
	}


	@Override
	public Attribute dispatch() throws ServiceException {
		Attribute at = Attribute.getByOwnerKey(userId,attr);
		if( Permission.allowed(userId, Permission.PERMISSION_READ, at.getId()) || Permission.allowed(userId, Permission.PERMISSION_WRITE, at.getId())){
			return at;
		}
		
		return null;
	}

	@Override
	public boolean canExecute() {
		return true;
	}

}
