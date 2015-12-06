package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import main.Tools;
import services.Service;

public class Message {
	
	private ObjectId target, source;
	private Date time;
	private String text;
	private ObjectId id = null;
	private Long sequence;

	private static MongoCollection<Document> collection;

	public static MongoCollection<Document> getCollection() {
		if (collection == null)
			collection = Service.getDatabase().getCollection("messages");
		return collection;
	}
	
	public Long generateSequence(){
		long count = getCollection()
				.count(new Document("target", target));
		
		return count;
	}
	
	public void save() {
		Document doc = new Document();
		if (id != null)
			doc.put("_id", id);

		doc.put("target", target);
		doc.put("source", source);
		doc.put("time", time);
		doc.put("text", text);
		doc.put("seq", sequence);
		

		if (id == null){
			doc.put("seq", generateSequence());
			getCollection().insertOne(doc);
		}
		else
			getCollection().replaceOne(new Document("_id", id), doc);

		id = doc.getObjectId("_id");

	}

	public static Message load(Document doc) {
		Message rec = new Message();
		rec.id = doc.getObjectId("_id");
		rec.source = doc.getObjectId("source");
		rec.target = doc.getObjectId("target");
		rec.time = doc.getDate("time");
		rec.text = doc.getString("text");
		rec.sequence = doc.getLong("seq");
		return rec;
	}

	public ObjectId getId() {
		return id;
	}
	
	public static List<Message> listByTarget(ObjectId groupId, Long end, int len) {
		Document query = new Document("target", groupId);
		if(end!=null){
			query.append("seq", new Document("$lt",end));
		}
		
		FindIterable<Document> iter = getCollection()
				.find(query ).sort(new Document("seq",-1)).limit(len);
		List<Message> ret = new ArrayList<Message>();
		for (Document doc : iter) {
			ret.add(Message.load(doc));
		}
		return ret;
	}

	public static Message findById(ObjectId id) {
		Document doc = new Document("_id", id);
		FindIterable<Document> iter = getCollection().find(doc);
		doc = iter.first();
		return doc != null ? load(doc) : null;
	}

	public Message() {
	}

	public Message(ObjectId groupId, ObjectId userId, Date time, String text) {
		this.target = groupId;
		this.source = userId;
		this.text = text;
		this.time = time;
	}

	public JSONObject toJsonObject(){
		JSONObject messageObj = new JSONObject();
		messageObj.put("id", getId().toString());
		PublicProfile publicProfile = PublicProfile.findByOwner(source);
		messageObj.put("name", publicProfile.getData().getString("name"));
		messageObj.put("time", Tools.FORMAT.format(time));
		messageObj.put("text", text);
		messageObj.put("seq", sequence);
		messageObj.put("source", source.toString());
		return messageObj;
	}
	
	public ObjectId getTarget() {
		return target;
	}

	public ObjectId getSource() {
		return source;
	}

	public Date getTime() {
		return time;
	}

	public String getText() {
		return text;
	}

	public Long getSequence() {
		return sequence;
	}

	public void delete() {
		if (id != null) {
			getCollection().deleteOne(new Document("_id", id));
		}
	}

}