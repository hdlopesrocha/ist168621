package services;


import dtos.PermissionDto;
import exceptions.ServiceException;
import models.Data;
import models.Permission;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * The Class ListOwnerAttributesService.
 */
public class ListOwnerAttributesService extends Service<Document> {

    /** The user. */
    private ObjectId user;
    
    /** The caller. */
    private ObjectId caller;
    
    /** The projection. */
    private Document projection;

    /**
     * Instantiates a new list owner attributes service.
     *
     * @param caller the caller
     * @param userId the user id
     * @param projection the projection
     */
    public ListOwnerAttributesService(String caller, String userId, List<String> projection) {
        this.user = new ObjectId(userId);
        this.caller = new ObjectId(caller);
        if (projection != null) {
            this.projection = new Document();
            for (String str : projection) {
                this.projection.append("data." + str, 1);
            }
        }
    }


    /* (non-Javadoc)
     * @see services.Service#dispatch()
     */
    @Override
    public Document dispatch() throws ServiceException {
        Permission permission = Permission.findByOwner(user);
        Document doc = (Document) Data.findByOwner(user, projection);

        if (doc != null) {
            doc = (Document) doc.get("data");

            if (permission != null) {
                Map<String, Permission.Entry> dPerm = permission.getData();
                for (Map.Entry<String, Permission.Entry> perm : dPerm.entrySet()) {
                    Set<ObjectId> aPerms = perm.getValue().getReadSet();
                    if (!aPerms.contains(caller)) {
                        doc.remove(perm.getKey());
                    }
                }

            }
        }
        return doc;
    }

    /* (non-Javadoc)
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {
        return true;
    }

}
