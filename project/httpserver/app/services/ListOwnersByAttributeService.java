package services;

import exceptions.ServiceException;
import models.Attribute;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;


public class ListOwnersByAttributeService extends Service<List<String>> {

	private ObjectId user;
	private String key;
	private String value;

	public ListOwnersByAttributeService(String callerId, String key, String value) {
		this.user = new ObjectId(callerId);
		this.key = key;
		this.value = value;
	}

	@Override
	public List<String> dispatch() throws ServiceException {
		List<String> ret = new ArrayList<String>();
		for(Attribute attr : Attribute.listByKeyValue(key,value)){
			ret.add(attr.getOwner().toString());
		}

		return ret;
	}

	@Override
	public boolean canExecute() {

		return user!=null;
	}
}
