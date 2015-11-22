package models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import main.Tools;
import services.Service;

public class Group {
	
	
	private String name = null;
	private ObjectId id = null;
	private String invite = null;
	private static MongoCollection<Document> collection;

	public Group() {
	}

	public Group(String name) {
		this.name = name;
	}

	public static MongoCollection<Document> getCollection(){
		if(collection==null)
			collection = Service.getDatabase().getCollection("groups");
		return collection;		
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void generateInvite(){
		invite = Tools.getRandomString(12);
	}
	
	public String getInvite(){
		return invite;
	}
	
	public boolean matchInvite(String attempt){
		return invite!=null && invite.equals(attempt);
	}
	
	public void save() {
		Document doc = new Document();
		if (id != null)
			doc.put("_id", id);

		doc.put("name", name);
		doc.put("invite", invite);

		if (id == null)
			getCollection().insertOne(doc);
		else
			getCollection().replaceOne(new Document("_id", id), doc);
				
		id = doc.getObjectId("_id");

	}

	private static Group load(Document doc) {
		Group user = new Group();
		user.id = doc.getObjectId("_id");
		user.name = doc.getString("name");
		user.invite = doc.getString("invite");
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

	public void deleteInvite() {
		invite = null;
	}

	public static List<Group> search(User caller, String query) {
		Pattern regex = Pattern.compile(query, Pattern.CASE_INSENSITIVE);		
		Document doc = new Document("name", regex);
		FindIterable<Document> iter = getCollection().find(doc);
		Iterator<Document> i = iter.iterator();
		List<Group> ret = new ArrayList<Group>();
		while(i.hasNext()){
			ret.add(Group.load(i.next()));
		}
		return ret;
	}
	
	

}