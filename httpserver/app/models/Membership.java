package models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import services.Service;

public class Membership {
	
	

	private ObjectId id = null, groupId = null,userId = null;
	private Document properties = new Document();
	

	private static MongoCollection<Document> collection;
	
	
	public static MongoCollection<Document> getCollection(){
		if(collection==null)
			collection = Service.getDatabase().getCollection("memberships");
		return collection;		
	}
	
	
	public void save() {
		Document doc = new Document();
		if (id != null)
			doc.put("_id", id);
		doc.put("gid", groupId);
		doc.put("uid", userId);
		doc.put("prop", properties);
		
		
		if (id == null)
			getCollection().insertOne(doc);
		else
			getCollection().replaceOne(new Document("_id", id), doc);
		
		id = doc.getObjectId("_id");
	}

	public Document getProperties() {
		return properties;
	}
	
	public void setProperties(Document properties) {
		this.properties = properties;
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
		if(doc.containsKey("prop"))
			user.setProperties((Document)doc.get("prop"));
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
		FindIterable<Document> iter = getCollection().find(doc);
		Iterator<Document> i = iter.iterator();
		List<Membership> ans = new ArrayList<Membership>();
		while(i.hasNext()){
			ans.add(load(i.next()));
		}
		return ans;
	}

	public static Membership findByUserGroup(ObjectId uid, ObjectId gid) {
		Document doc = new Document("gid", gid).append("uid", uid);
		FindIterable<Document> iter = getCollection().find(doc);
		doc = iter.first();
		return doc != null ? load(doc) : null;
	}

	
	public static List<Membership> listByGroup(ObjectId id) {
		Document doc = new Document("gid", id);
		FindIterable<Document> iter = getCollection().find(doc);
		Iterator<Document> i = iter.iterator();
		List<Membership> ans = new ArrayList<Membership>();
		while(i.hasNext()){
			ans.add(load(i.next()));
		}
		return ans;
	}
	
	public static Membership findById(ObjectId id) {
		Document doc = new Document("_id", id);
		FindIterable<Document> iter = getCollection().find(doc);
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
			getCollection().deleteOne(new Document("_id", id));		
	}

}