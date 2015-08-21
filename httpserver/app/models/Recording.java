package models;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import services.Service;

public class Recording {
	
	private ObjectId owner;
	private String start,end;
	private String url;
	private ObjectId id = null;
	private long sequence;
	
	public static MongoCollection<Document> collection;
	
	
	public static MongoCollection<Document> getCollection(){
		if(collection==null)
			collection = Service.getDatabase().getCollection("recordings");
		return collection;		
	}
	
	public void save() {
		Document doc = new Document();
		if (id != null)
			doc.put("_id", id);

		doc.put("owner", owner);
		doc.put("start", start);
		doc.put("end", end);
		doc.put("url", url);
		doc.put("seq", sequence);
		
		
		if (id == null)
			getCollection().insertOne(doc);
		else
			getCollection().replaceOne(new Document("_id", id), doc);
		
		id = doc.getObjectId("_id");

	}

	public long getSequence() {
		return sequence;
	}

	public void setSequence(long sequence) {
		this.sequence = sequence;
	}

	public ObjectId getOwner() {
		return owner;
	}

	public void setOwner(ObjectId owner) {
		this.owner = owner;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	private static Recording load(Document doc) {
		Recording rec = new Recording();
		rec.setId(doc.getObjectId("_id"));
		rec.setEnd(doc.getString("end"));
		rec.setStart(doc.getString("start"));
		rec.setOwner(doc.getObjectId("owner"));
		rec.setUrl(doc.getString("url"));
		rec.setSequence(doc.getLong("seq"));
		return rec;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public static long countByOwner(ObjectId owner){
		Document doc = new Document("owner", owner);
		return getCollection().count(doc);
	}
	
	public static Recording findById(ObjectId id) {
		Document doc = new Document("_id", id);
		FindIterable<Document> iter = getCollection().find(doc);
		doc = iter.first();
		return doc != null ? load(doc) : null;
	}

	public Recording() {
	}

	public Recording(ObjectId owner, String start, String end, String url, long sequence) {
		this.owner = owner;
		this.start = start;
		this.end = end;
		this.url = url;
		this.sequence = sequence;
	}

	public void delete() {
		if (id != null){
			getCollection().deleteOne(new Document("_id", id));
		}
	}

}