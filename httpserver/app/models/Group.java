package models;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;

import services.Service;

public class Group {
	
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private String name = null;
	private ObjectId id = null;

	
	public void save() {
		Document doc = new Document();
		if (id != null)
			doc.put("_id", id);

		doc.put("name", name);
		if (id != null)
			Service.groups.deleteOne(new Document("_id", id));

		Service.groups.insertOne(doc);

		id = doc.getObjectId("_id");

	}

	private static Group load(Document doc) {
		Group user = new Group();
		user.setId(doc.getObjectId("_id"));
		user.setName(doc.getString("name"));
		return user;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}
	
	public static Group findById(ObjectId id) {
		Document doc = new Document("_id", id);
		FindIterable<Document> iter = Service.groups.find(doc);
		doc = iter.first();
		return doc != null ? load(doc) : null;
	}

	public Group() {
	}

	public Group(String name) {
	
	}

	public void delete() {
		if (id != null)
			Service.groups.deleteOne(new Document("_id", id));		
	}

}