package services;

import exceptions.BadRequestException;
import models.CollaborativeContent;
import models.User;
import org.bson.types.ObjectId;


/**
 * The Class GetCurrentHyperContentService.
 */
public class GetCollaborativeContentService extends Service<String> {

    /** The caller. */
    private final User caller;

    /** The group id. */
    private final ObjectId groupId;

    /**
     * Instantiates a new gets the current hyper content service.
     *
     * @param callerId the caller id
     * @param groupId the group id
     */
    public GetCollaborativeContentService(String callerId, String groupId) {
        this.caller = User.findById(new ObjectId(callerId));
        this.groupId = new ObjectId(groupId);
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public String dispatch() throws BadRequestException {
        CollaborativeContent cc = CollaborativeContent.findByGroupId(groupId);
        if(cc!=null){
            return  cc.getContent();
        }
        return "";
    }


    /*
     * (non-Javadoc)
     *
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {
        return caller != null;
    }

}
