package services;

import models.TimeTag;
import org.bson.types.ObjectId;

import java.util.List;

// TODO: Auto-generated Javadoc

/**
 * The Class AuthenticateUserService.
 */
public class SearchTimeTagsService extends Service<List<TimeTag>> {

    private final ObjectId caller;
    private final ObjectId gid;
    private final String query;

    public SearchTimeTagsService(String caller, String gid, String query) {
        this.caller = new ObjectId(caller);
        this.gid = new ObjectId(gid);
        this.query = query;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public List<TimeTag> dispatch() {

        return TimeTag.search(gid, query);
    }

    @Override
    public boolean canExecute() {
        return caller!=null;
    }


}
