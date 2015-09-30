package models;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

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
	private static MongoCollection<Document> collection;
	
	
	public static MongoCollection<Document> getCollection(){
		if(collection==null)
			collection = Service.getDatabase().getCollection("groups");
		return collection;		
	}
	
	public void save() {
		Document doc = new Document();
		if (id != null)
			doc.put("_id", id);

		doc.put("name", name);
		if (id == null)
			getCollection().insertOne(doc);
		else
			getCollection().replaceOne(new Document("_id", id), doc);
				
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
		FindIterable<Document> iter = getCollection().find(doc);
		doc = iter.first();
		return doc != null ? load(doc) : null;
	}

	public Group() {
	}

	public Group(String name) {
		this.name = name;
	}

	public void delete() {
		if (id != null)
			getCollection().deleteOne(new Document("_id", id));		
	}

	public static List<Group> listAll() {
		FindIterable<Document> iter = getCollection().find(new Document());
		List<Group> ret = new ArrayList<Group>();
		for(Document doc : iter){
			ret.add(load(doc));
		}
		return ret;
	}

}