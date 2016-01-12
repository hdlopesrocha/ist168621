package services;

import com.mongodb.client.FindIterable;
import dtos.KeyValue;
import models.MetaData;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;


/**
 * The Class AuthenticateUserService.
 */
public class SearchOwnersService extends Service<List<String>> {

	private final ObjectId caller;
	private Integer limit=null, offset = null;
	private Long count =null;
	private Long total =null;
	private String search =null;
private List<KeyValue<String>> filters;
	
	public SearchOwnersService(String callerId, Integer offset, Integer limit, String search,List<KeyValue<String>> filters) {
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
		FindIterable<Document> iter;
		total = MetaData.getCollection().count();

		List<MetaData> metaDatas = MetaData.search(search,offset,limit,filters);
		count = MetaData.count(search,filters);

		for (MetaData metaData : metaDatas) {
			ret.add(metaData.getOwner().toString());
		}
		return ret;
	}

	@Override
	public boolean canExecute() {
		return caller!=null;

	}

	
	public Long getCount() {
		return count;
	}
	


	
	public Long getTotal() {
		return total;
	}


}
