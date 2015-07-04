package models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;

import services.Service;

public class Membership {
	
	

	private ObjectId id = null, groupId = null,userId = null;

	
	public void save() {
		Document doc = new Document();
		if (id != null)
			doc.put("_id", id);

		doc.put("gid", groupId);
		doc.put("uid", userId);
		if (id != null)
			Service.memberships.deleteOne(new Document("_id", id));

		Service.memberships.insertOne(doc);

		id = doc.getObjectId("_id");

	}

	public ObjectId getGroupId() {
		return groupId;
	}

	public void setGroupId(ObjectId groupId) {
		this.groupId = groupId;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

	private static Membership load(Document doc) {
		Membership user = new Membership();
		user.setId(doc.getObjectId("_id"));
		user.setUserId(doc.getObjectId("uid"));
		user.setGroupId(doc.getObjectId("gid"));
		return user;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}
	
	public static List<Membership> listByUser(ObjectId id) {
		Document doc = new Document("uid", id);
		FindIterable<Document> iter = Service.memberships.find(doc);
		Iterator<Document> i = iter.iterator();
		List<Membership> ans = new ArrayList<Membership>();
		while(i.hasNext()){
			ans.add(load(i.next()));
		}
		return ans;
	}
	
	public static List<Membership> listByGroup(ObjectId id) {
		Document doc = new Document("gid", id);
		FindIterable<Document> iter = Service.memberships.find(doc);
		Iterator<Document> i = iter.iterator();
		List<Membership> ans = new ArrayList<Membership>();
		while(i.hasNext()){
			ans.add(load(i.next()));
		}
		return ans;
	}
	
	public static Membership findById(ObjectId id) {
		Document doc = new Document("_id", id);
		FindIterable<Document> iter = Service.memberships.find(doc);
		doc = iter.first();
		return doc != null ? load(doc) : null;
	}

	public Membership() {
	}

	public Membership(ObjectId userId,ObjectId groupId) {
		this.userId = userId;
		this.groupId = groupId;
	}

	public void delete() {
		if (id != null)
			Service.memberships.deleteOne(new Document("_id", id));		
	}

}