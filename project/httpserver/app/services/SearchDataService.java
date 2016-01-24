package services;

import dtos.KeyValue;
import models.Search;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;



/**
 * The Class SearchDataService.
 */
public class SearchDataService extends Service<List<String>> {

    /** The caller. */
    private ObjectId caller;
    
    /** The offset. */
    private Integer limit = null, offset = null;
    
    /** The count. */
    private Long count = null;
    
    /** The total. */
    private Long total = null;
    
    /** The search. */
    private String search = null;
    
    /** The filters. */
    private List<List<KeyValue<String>>> filters = null;

    /**
     * Instantiates a new search data service.
     *
     * @param callerId the caller id
     * @param offset the offset
     * @param limit the limit
     * @param search the search
     * @param filters the filters
     */
    public SearchDataService(String callerId, Integer offset, Integer limit, String search, List<List<KeyValue<String>>> filters) {
        this.offset = offset;
        this.limit = limit;
        this.search = search;
        this.caller = new ObjectId(callerId);
        this.filters = filters;


    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public List<String> dispatch() {
        List<String> ret = new ArrayList<String>();


        List<Search> tags = Search.search(search, offset, limit, filters);
        total = Search.count(filters);
        count = Search.countByValue(search, filters);

        for (Search tag : tags) {
            ret.add(tag.getOwner().toString());

        }
        return ret;
    }

    /* (non-Javadoc)
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {
        return caller != null;

    }


    /**
     * Gets the count.
     *
     * @return the count
     */
    public Long getCount() {
        return count;
    }


    /**
     * Gets the total.
     *
     * @return the total
     */
    public Long getTotal() {
        return total;
    }


}