package services;

import models.Recording;
import models.User;
import org.bson.types.ObjectId;

import java.util.List;

// TODO: Auto-generated Javadoc

/**
 * The Class AuthenticateUserService.
 */
public class ListRecordingsService extends Service<List<Recording>> {

    private User caller;
    private ObjectId groupId;
    private long sequence;

    public ListRecordingsService(String callerId, String groupId, long sequence) {
        this.caller = User.findById(new ObjectId(callerId));
        this.groupId = new ObjectId(groupId);
        this.sequence = sequence;

    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public List<Recording> dispatch() {
        return Recording.listByGroup(groupId, sequence);
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
