package services;

import com.mongodb.client.FindIterable;
import models.Permission;
import models.User;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/* XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX */
/* !!! THIS SERVICE IS COMPLETELY WRONG !!! */
/* XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX */

/**
 * The Class AuthenticateUserService.
 */
public class ListOwnersService extends Service<List<ObjectId>> {

	private ObjectId caller;
	private Integer limit=null, offset = null;
	private Long count =null;
	private Long total =null;
	private String search =null;

	
	public ListOwnersService(String callerId, Integer offset, Integer limit) {
		this.offset = offset;
		this.limit = limit;
		this.caller = new ObjectId(callerId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public List<ObjectId> dispatch() {
		List<ObjectId> ret = new ArrayList<ObjectId>();
		FindIterable<Document> iter;
		total = User.getCollection().count();

		if(search!=null){
			Pattern regex = Pattern.compile(search,Pattern.CASE_INSENSITIVE);
			Document query = new Document("value",regex).append("searchable",true);
			count = User.getCollection().count(query);
			iter = User.getCollection().find(query);						
		}else {
			count = User.getCollection().count();
			iter = User.getCollection().find();			
		}
		
		if(offset!=null){
			iter = iter.skip(offset);
		}
		if(limit!=null){
			if(limit<=0){
				return ret;
			}
			iter = iter.limit(limit);
		}
		for (Document doc : iter ) {
			ret.add(User.load(doc).getId());
		}
		return ret;
	}

	@Override
	public boolean canExecute() {
		return caller!=null && Permission.find(caller, Permission.PERMISSION_ADMIN,null)!=null;

	}

	
	public Long getCount() {
		return count;
	}
	
	public void setSearch(String search) {
		this.search = search;
	}


	
	public Long getTotal() {
		return total;
	}


}
