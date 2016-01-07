package services;


import exceptions.ServiceException;
import models.Attribute;

/**
 * The Class AuthenticateUserService.
 */
public class GetOwnerByAttributeService extends Service<String> {

	private final String key;
	private final Object value;

	public GetOwnerByAttributeService(String key, Object value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public String dispatch() throws ServiceException {
		Attribute at = Attribute.getByKeyValue(key,value);
		return at!=null ? at.getOwner().toString() : null;
	}

	@Override
	public boolean canExecute() {
		return true;
	}

}
