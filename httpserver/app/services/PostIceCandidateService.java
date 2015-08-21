package services;

import java.util.ArrayList;
import java.util.List;

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

		List<?> list = null;
		String mtoken = doc.getString("itoken");

		if (token == null || token.equals(mtoken)) {
			list = (List<?>) doc.get("ices");
		}
		
		List<Object> ices = new ArrayList<Object>();
		if(list!=null){
			for(Object o : list){
				ices.add(o);
			}
		}
		ices.add(obj);
		
		doc.put("itoken", token);
		doc.put("ices", ices);
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
