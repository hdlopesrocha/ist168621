package services;

import exceptions.BadRequestException;
import models.CollaborativeContent;
import models.User;
import org.bson.types.ObjectId;


/**
 * The Class GetCurrentHyperContentService.
 */
public class SetCollaborativeContentService extends Service<Void> {

    /** The caller. */
    private final User caller;

    /** The group id. */
    private final ObjectId groupId;

    private String content;

    /**
     * Instantiates a new gets the current hyper content service.
     *
     * @param callerId the caller id
     * @param groupId the group id
     */
    public SetCollaborativeContentService(String callerId, String groupId,String content) {
        this.caller = User.findById(new ObjectId(callerId));
        this.groupId = new ObjectId(groupId);
        this.content = content;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public Void dispatch() throws BadRequestException {
        CollaborativeContent  cc = CollaborativeContent.findByGroupId(groupId);
        if(cc==null){
            cc = new CollaborativeContent(groupId,content);
        }else {
            cc.setContent(content);
        }
        cc.save();
        return null;
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
