package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import services.Service;

public class TimeTag {

	private Date time;
	private String title;
	private String content;
	private ObjectId id, gid = null;

	private static MongoCollection<Document> collection;

	public static MongoCollection<Document> getCollection() {
		if (collection == null)
			collection = Service.getDatabase().getCollection("timeTags");
		return collection;
	}

	public void save() {
		Document doc = new Document();
		if (id != null)
			doc.put("_id", id);

		doc.put("gid", gid);
		doc.put("time", time);
		doc.put("title",title);
		doc.put("content",content);

		if (id == null)
			getCollection().insertOne(doc);
		else
			getCollection().replaceOne(new Document("_id", id), doc);

		id = doc.getObjectId("_id");

	}


	public static TimeTag load(Document doc) {
		TimeTag rec = new TimeTag();
		rec.id = doc.getObjectId("_id");
		rec.time = doc.getDate("time");
		rec.gid = doc.getObjectId("gid");
		rec.title = doc.getString("title");
		rec.content = doc.getString("content");
		return rec;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public static long countByGroup(ObjectId owner) {
		Document doc = new Document("gid", owner);
		return getCollection().count(doc);
	}

	public static List<TimeTag> listByGroup(ObjectId groupId) {
		FindIterable<Document> iter = getCollection()
				.find(new Document("gid", groupId));
		List<TimeTag> ret = new ArrayList<TimeTag>();
		for (Document doc : iter) {
			ret.add(TimeTag.load(doc));
		}
		return ret;
	}
	
	public static TimeTag findById(ObjectId id) {
		Document doc = new Document("_id", id);
		FindIterable<Document> iter = getCollection().find(doc);
		doc = iter.first();
		return doc != null ? load(doc) : null;
	}

	private TimeTag() {

	}
	

	public TimeTag(ObjectId gid,Date time, String title, String content) {
		this.time = time;
		this.gid = gid;
		this.title = title;
		this.content = content;
	}

	public void delete() {
		if (id != null) {
			getCollection().deleteOne(new Document("_id", id));
		}
	}

}