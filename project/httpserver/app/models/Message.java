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
	
	private ObjectId groupId, userId;
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
				.count(new Document("gid", groupId));
		
		return count;
	}
	
	public void save() {
		Document doc = new Document();
		if (id != null)
			doc.put("_id", id);

		doc.put("gid", groupId);
		doc.put("uid", userId);
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
		rec.userId = doc.getObjectId("uid");
		rec.groupId = doc.getObjectId("gid");
		rec.time = doc.getDate("time");
		rec.text = doc.getString("text");
		rec.sequence = doc.getLong("seq");
		return rec;
	}

	public ObjectId getId() {
		return id;
	}
	
	public static List<Message> listByGroup(ObjectId groupId, Long end, int len) {
		Document query = new Document("gid", groupId);
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
		this.groupId = groupId;
		this.userId = userId;
		this.text = text;
		this.time = time;
	}

	public JSONObject toJsonObject(){
		JSONObject messageObj = new JSONObject();
		User u = User.findById(userId);
		messageObj.put("id", getId().toString());
		messageObj.put("name", u.getPublicProperties().getString("name"));
		messageObj.put("time", Tools.FORMAT.format(time));
		messageObj.put("text", text);
		messageObj.put("seq", sequence);
		messageObj.put("uid", userId.toString());
		return messageObj;
	}
	
	public ObjectId getGroupId() {
		return groupId;
	}

	public ObjectId getUserId() {
		return userId;
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