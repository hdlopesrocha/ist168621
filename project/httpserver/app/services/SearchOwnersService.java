package services;

import com.mongodb.client.FindIterable;
import models.Tag;
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

	
	public SearchOwnersService(String callerId, Integer offset, Integer limit, String search) {
		this.offset = offset;
		this.limit = limit;
		this.search = search;
		this.caller = new ObjectId(callerId);
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
		total = Tag.getCollection().count();

		List<Tag> tags = Tag.searchByValue(search,offset,limit);
		count = Tag.countByValue(search);

		for (Tag tag : tags ) {
			ret.add(tag.getOwner().toString());
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
