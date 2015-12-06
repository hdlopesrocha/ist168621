package models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import services.Service;

import com.mongodb.BasicDBList;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

public class PublicProfile {
	public static final String TYPE = "user.PublicProfile";
	private ObjectId owner=null;
	private ObjectId id = null;
	private Document data = new Document();

	private static MongoCollection<Document> collection;

	public static MongoCollection<Document> getCollection() {
		if(collection==null)
			collection = Service.getDatabase().getCollection(TYPE);
		return collection;
	}
	public JSONObject toJson(){
		return new JSONObject(data.toJson());
	}
	public static PublicProfile load(Document doc) {
		PublicProfile user = new PublicProfile();
		user.id = doc.getObjectId("_id");
		user.owner = doc.getObjectId("owner");
		user.data = (Document) doc.get("data");
		return user;
	}

	public PublicProfile save() {
		Document doc = new Document();

		doc.put("data", data);
		doc.put("owner", owner);

		if(id==null){
			getCollection().insertOne(doc);
			id = doc.getObjectId("_id");
		}
		else {
			doc.put("_id", id);
			getCollection().findOneAndReplace(new Document("_id",id),doc);
		}
		return this;
	}

	public static PublicProfile findByOwner(ObjectId id) {
		Document doc = new Document("owner", id);
		FindIterable<Document> iter = getCollection().find(doc);
		doc = iter.first();
		return doc != null ? load(doc) : null;
	}


	public PublicProfile() {

	}
	
	public static List<PublicProfile> search(String key,String query) {
		Pattern regex = Pattern.compile(query, Pattern.CASE_INSENSITIVE);	
		BasicDBList or = new BasicDBList();
		or.add(new Document("data."+key,regex));
		
		Document doc = new Document("$or", or);
		FindIterable<Document> iter = getCollection().find(doc);
		Iterator<Document> i = iter.iterator();
		List<PublicProfile> ret = new ArrayList<PublicProfile>();
		while(i.hasNext()){
			ret.add(PublicProfile.load(i.next()));
		}
		return ret;
	}

	public PublicProfile(ObjectId owner,
						 Document properties) {
		this.data =  properties;
		this.owner = owner;
	}



	public Document getData() {
		return data;
	}

	public void setData(Document properties) {
		this.data = properties;
	}


	public ObjectId getOwner() {
		return owner;
	}
}