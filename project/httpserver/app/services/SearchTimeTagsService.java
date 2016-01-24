package services;

import models.TimeTag;
import org.bson.types.ObjectId;

import java.util.List;



/**
 * The Class SearchTimeTagsService.
 */
public class SearchTimeTagsService extends Service<List<TimeTag>> {

    /** The caller. */
    private final ObjectId caller;
    
    /** The gid. */
    private final ObjectId gid;
    
    /** The query. */
    private final String query;

    /**
     * Instantiates a new search time tags service.
     *
     * @param caller the caller
     * @param gid the gid
     * @param query the query
     */
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

    /* (non-Javadoc)
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {
        return caller != null;
    }


}
