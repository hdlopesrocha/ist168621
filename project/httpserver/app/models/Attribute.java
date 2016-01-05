package models;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.types.ObjectId;
import services.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Attribute {
	public static final String TYPE = "user.Attribute";


	private String key;
	private Object value;
	private ObjectId id = null;

	public Boolean getSearchable() {
		return searchable;
	}

	public Boolean getIdentifiable() {
		return identifiable;
	}

	private Boolean searchable = false;
	private Boolean identifiable=false;


	public ObjectId getOwner() {
		return owner;
	}

	private ObjectId owner = null;


	public Object getValue() {
		return value;
	}

	public String getKey() {
		return key;
	}


	private static MongoCollection<Document> collection;

	public static MongoCollection<Document> getCollection() {
		if(collection==null)
			collection = Service.getDatabase().getCollection(TYPE);
		return collection;
	}

	public static Attribute load(Document doc) {
		Attribute user = new Attribute();
		user.id = doc.getObjectId("_id");
		user.owner = doc.getObjectId("owner");
		user.key = doc.getString("key");
		user.value = doc.get("value");
		user.identifiable = doc.getBoolean("identifiable");
		user.searchable = doc.getBoolean("searchable");
		return user;
	}

	public Attribute save() {
		Document doc = new Document();

		doc.put("key", key);
		doc.put("value", value);
		doc.put("owner", owner);
		doc.put("identifiable", identifiable);
		doc.put("searchable", searchable);

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

	public ObjectId getId() {
		return id;
	}


	public static Attribute findById(ObjectId id) {
		Document doc = new Document("_id", id);
		FindIterable<Document> iter = getCollection().find(doc);
		doc = iter.first();
		return doc != null ? load(doc) : null;
	}

	public Attribute() {

	}
	public static List<Attribute> listByKeyValue(String key, Object value) {
		Document doc = new Document("key", key).append("value",value).append("identifiable",false);
		MongoCursor<Document> iter = getCollection().find(doc).iterator();
		List<Attribute> ret = new ArrayList<Attribute>();
		while (iter.hasNext()){
			ret.add(Attribute.load(iter.next()));
		}

		return ret;
	}

	public static Attribute getByKeyValue(String key, Object value) {
		Document doc = new Document("key", key).append("value",value).append("identifiable",true);
		FindIterable<Document> iter = getCollection().find(doc);
		doc =iter.first();
		return doc!=null ? Attribute.load(doc):null;
	}

	public static Attribute getByOwnerKey(ObjectId id,String key) {
		Document doc = new Document("owner", id).append("key",key);
		FindIterable<Document> iter = getCollection().find(doc);
		doc = iter.first();

		return iter!=null ? Attribute.load(doc):null;
	}

	public static List<Attribute> listByOwner(ObjectId id) {
		Document doc = new Document("owner", id);
		MongoCursor<Document> iter = getCollection().find(doc).iterator();
		List<Attribute> ret = new ArrayList<Attribute>();
		while (iter.hasNext()){
			ret.add(Attribute.load(iter.next()));
		}

		return ret;
	}


	public static List<Attribute> searchByValue(String value){
		Pattern regex = Pattern.compile(value,Pattern.CASE_INSENSITIVE);

		Document query = new Document("value", regex).append("searchable",true);

		MongoCursor<Document> iter = getCollection().find(query).iterator();
		List<Attribute> ret = new ArrayList<Attribute>();
		while (iter.hasNext()){
			ret.add(Attribute.load(iter.next()));
		}
		return ret;

	}


	public Attribute(ObjectId owner, String key, Object value, boolean identifiable,boolean searchable) {
		this.key = key;
		this.owner = owner;
		this.value = value;
		this.identifiable = identifiable;
		this.searchable = searchable;
	}

}
