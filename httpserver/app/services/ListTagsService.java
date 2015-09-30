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
public class ListTagsService extends Service<List<TimeTag>> {

	private User caller;
	private ObjectId gid;

	public ListTagsService(String email,String gid) {
		this.caller = email!=null?User.findByEmail(email):null;
		this.gid = new ObjectId(gid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public List<TimeTag> dispatch() {
		FindIterable<Document> iter = TimeTag.getCollection().find(new Document("gid",gid));
		JSONArray array = new JSONArray();
		List<TimeTag> tags = new ArrayList<TimeTag>();
		
		for (Document doc : iter ) {
			TimeTag tag = TimeTag.load(doc);
			tags.add(tag);	
		}

		return tags;
	}

	@Override
	public boolean canExecute() {
		return true;
	}


}
