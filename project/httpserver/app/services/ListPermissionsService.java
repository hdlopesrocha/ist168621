package services;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import exceptions.ServiceException;
import models.Permission;

public class ListPermissionsService extends Service<List<String>> {

	private ObjectId callerId;
	private ObjectId ownerId;
	
	 public ListPermissionsService(String callerId, String ownerId) {
		 this.callerId = new ObjectId(callerId);
		 this.ownerId = new ObjectId(ownerId);
	}
	
	@Override
	public List<String> dispatch() throws ServiceException {
		List<String> ret = new ArrayList<String>();
		for(Permission permission : Permission.list(ownerId)){
			ret.add(permission.getName());
		}
		return ret;
	}

	@Override
	public boolean canExecute() {
		return Permission.find(callerId, Permission.PERMISSION_ADMIN,null)!=null;
	}

}
