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

	public static MongoCollection<Document> collection;

	public static MongoCollection<Document> getCollection() {
		if (collection == null)
			collection = Service.getDatabase().getCollection("messages");
		return collection;
	}

	public void save() {
		Document doc = new Document();
		if (id != null)
			doc.put("_id", id);

		doc.put("gid", groupId);
		doc.put("uid", userId);
		doc.put("time", time);
		doc.put("text", text);
		

		if (id == null)
			getCollection().insertOne(doc);
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
		return rec;
	}

	public ObjectId getId() {
		return id;
	}
	
	public static List<Message> listByGroup(ObjectId groupId) {
		FindIterable<Document> iter = getCollection()
				.find(new Document("gid", groupId));
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
		User u = User.findById(getUserId());
		messageObj.put("id", getId().toString());
		messageObj.put("name", u.getPublicProperties().getString("name"));
		messageObj.put("time", Tools.FORMAT.format(getTime()));
		messageObj.put("text", getText());
		messageObj.put("uid", getUserId().toString());
		return messageObj;
	}
	
	public ObjectId getGroupId() {
		return groupId;
	}

	public Date getTime() {
		return time;
	}

	public String getText() {
		return text;
	}

	public void delete() {
		if (id != null) {
			getCollection().deleteOne(new Document("_id", id));
		}
	}

	public ObjectId getUserId() {
		return userId;
	}

}