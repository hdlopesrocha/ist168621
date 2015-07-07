package services;

import java.util.ArrayList;

import org.bson.Document;
import org.bson.types.ObjectId;

import models.Membership;
import models.User;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class PostSdpService extends Service<Void> {

	private User user;
	private Membership membership;
	private String sdpjson, token;

	public PostSdpService(String email, String groupId, String token, String sdpjson) {
		this.user = User.findByEmail(email);
		this.membership = Membership.findByUserGroup(user.getId(), new ObjectId(groupId));
		this.sdpjson = sdpjson;
		this.token = token;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public Void dispatch() {

		Document obj = Document.parse(sdpjson);
		Document doc = membership.getProperties();

		ArrayList<Object> list = (ArrayList<Object>) doc.get("sdps");
		String mtoken = doc.getString("token");

		if (list == null || (token != null && !token.equals(mtoken))) {
			list = new ArrayList<Object>();
		}
		list.add(obj);
		doc.put("token", token);
		doc.put("sdps", list);
		membership.save();

	
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#canExecute()
	 */
	@Override
	public boolean canExecute() {
		return user != null && membership != null;
	}

}
