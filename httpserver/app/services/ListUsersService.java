package services;

import models.User;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.client.FindIterable;

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
		FindIterable<Document> iter = users.find();
		
		
		
		

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
		if (caller != null) {
			try {
				JSONArray array = new JSONArray(caller.getPermissions());
				for (int i = 0; i < array.length(); ++i) {
					if (array.getString(i).equals("ADMIN"))
						return true;

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return false;
	}


}
