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
public class PostIceCandidateService extends Service<Void> {

	private User user;
	private Membership membership;
	private String sdpjson, token;

	public PostIceCandidateService(String uid, String groupId, String token, String sdpjson) {
		this.user = User.findById(new ObjectId(uid));
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

		ArrayList<Object> list = (ArrayList<Object>) doc.get("ices");
		String mtoken = doc.getString("itoken");

		if (list == null || (token != null && !token.equals(mtoken))) {
			list = new ArrayList<Object>();
		}
		list.add(obj);
		doc.put("itoken", token);
		doc.put("ices", list);
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
