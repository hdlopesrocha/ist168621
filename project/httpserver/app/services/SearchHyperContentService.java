package services;

import models.HyperContent;
import org.bson.types.ObjectId;

import java.util.List;



/**
 * The Class SearchHyperContentService.
 */
public class SearchHyperContentService extends Service<List<HyperContent>> {

    /** The caller. */
    private final ObjectId caller;
    
    /** The gid. */
    private final ObjectId gid;
    
    /** The query. */
    private final String query;

    /**
     * Instantiates a new search hyper content service.
     *
     * @param caller the caller
     * @param gid the gid
     * @param query the query
     */
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

    /* (non-Javadoc)
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {
        return caller != null;
    }


}
