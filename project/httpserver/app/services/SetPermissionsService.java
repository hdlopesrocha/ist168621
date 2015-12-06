package services;

import java.util.List;

import org.bson.types.ObjectId;

import exceptions.ServiceException;
import models.Permission;

public class SetPermissionsService extends Service<Void> {

	private List<String> permissions;
	private ObjectId callerId;
	private ObjectId source;
	
	 public SetPermissionsService(String callerId, String ownerId, List<String> permissions) {
		 this.permissions = permissions;
		 this.callerId = new ObjectId(callerId);
		 this.source = new ObjectId(ownerId);
	}
	
	@Override
	public Void dispatch() throws ServiceException {
		Permission.deleteSourcePermissions(source);
		for(String permission : permissions){
			new Permission(source,permission,null).save();
		}
		
		return null;
	}

	@Override
	public boolean canExecute() {
		return Permission.find(callerId, Permission.PERMISSION_ADMIN,null)!=null;
	}

}
