package services;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.client.FindIterable;

import models.TimeTag;
import models.User;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class SearchTagsService extends Service<List<TimeTag>> {

	private User caller;
	private ObjectId gid;
	private String query;

	public SearchTagsService(String email,String gid,String query) {
		this.caller = email!=null?User.findByEmail(email):null;
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
	
		return TimeTag.search(gid,query);
	}

	@Override
	public boolean canExecute() {
		return true;
	}


}
