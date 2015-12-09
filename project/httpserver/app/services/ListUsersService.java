package services;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.client.FindIterable;

import models.User;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class ListUsersService extends Service<String> {

	private User caller;

	public ListUsersService(String email) {
		this.caller = email!=null?User.findByEmail(email):null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public String dispatch() {
		FindIterable<Document> iter = User.getCollection().find();
		JSONArray array = new JSONArray();
		
		for (Document doc : iter ) {
			JSONObject inc = new JSONObject();
			inc.put("email",doc.getString("email"));
			inc.put("name",doc.getString("name"));
			array.put(inc);	
		}

		return array.toString();
	}

	@Override
	public boolean canExecute() {
		return caller!=null;
	}

}
