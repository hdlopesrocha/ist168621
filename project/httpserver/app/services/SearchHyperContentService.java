package services;

import models.HyperContent;
import models.TimeTag;
import org.bson.types.ObjectId;

import java.util.List;

// TODO: Auto-generated Javadoc

/**
 * The Class AuthenticateUserService.
 */
public class SearchHyperContentService extends Service<List<HyperContent>> {

    private final ObjectId caller;
    private final ObjectId gid;
    private final String query;

    public SearchHyperContentService(String caller, String gid, String query) {
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
    public List<HyperContent> dispatch() {

        return HyperContent.search(gid, query);
    }

    @Override
    public boolean canExecute() {
        return caller!=null;
    }


}
