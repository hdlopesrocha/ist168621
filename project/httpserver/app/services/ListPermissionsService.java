package services;

import exceptions.ServiceException;
import models.Permission;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class ListPermissionsService extends Service<List<String>> {

    private final ObjectId callerId;
    private final ObjectId ownerId;

    public ListPermissionsService(String callerId, String ownerId) {
        this.callerId = new ObjectId(callerId);
        this.ownerId = new ObjectId(ownerId);
    }

    @Override
    public List<String> dispatch() throws ServiceException {
        List<String> ret = new ArrayList<String>();
        for (Permission permission : Permission.list(ownerId)) {
            ret.add(permission.getName());
        }
        return ret;
    }

    @Override
    public boolean canExecute() {
        return Permission.find(callerId, Permission.PERMISSION_ADMIN, null) != null;
    }

}
