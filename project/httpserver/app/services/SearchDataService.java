package services;

import java.util.ArrayList;
import java.util.List;

import dtos.KeyValue;
import models.Search;
import org.bson.types.ObjectId;


/**
 * The Class AuthenticateUserService.
 */
public class SearchDataService extends Service<List<String>> {

	private ObjectId caller;
	private Integer limit=null, offset = null;
	private Long count =null;
	private Long total =null;
	private String search =null;
	private List<List<KeyValue<String>>> filters=null;

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


		List<Search> tags = Search.search(search,offset,limit, filters);
		total = Search.count(filters);
		count = Search.countByValue(search,filters);

		for (Search tag : tags ) {
			ret.add(tag.getOwner().toString());

		}
		return ret;
	}

	@Override
	public boolean canExecute() {
		return caller!=null ;

	}


	public Long getCount() {
		return count;
	}




	public Long getTotal() {
		return total;
	}


}