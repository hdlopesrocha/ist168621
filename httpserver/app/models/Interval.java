package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import services.Service;

public class Interval {

	private Date start, end;

	private ObjectId id = null;

	public static MongoCollection<Document> collection;

	public static MongoCollection<Document> getCollection() {
		if (collection == null)
			collection = Service.getDatabase().getCollection("intervals");
		return collection;
	}

	public void save() {
		Document doc = new Document();
		if (id != null)
			doc.put("_id", id);

		doc.put("start", start);
		doc.put("end", end);

		if (id == null)
			getCollection().insertOne(doc);
		else
			getCollection().replaceOne(new Document("_id", id), doc);

		id = doc.getObjectId("_id");

	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}


	private static Interval load(Document doc) {
		Interval rec = new Interval();
		rec.id = doc.getObjectId("_id");
		rec.end = doc.getDate("end");
		rec.start = doc.getDate("start");
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

	public static List<Interval> listByGroup(ObjectId groupId) {
		FindIterable<Document> iter = getCollection()
				.find(new Document("gid", groupId));
		List<Interval> ret = new ArrayList<Interval>();
		for (Document doc : iter) {
			ret.add(Interval.load(doc));
		}
		return ret;
	}
	
	public static List<Interval> listByGroup(ObjectId groupId, long sequence) {
		FindIterable<Document> iter = getCollection()
				.find(new Document("gid", groupId).append("seq", new Document("$gt", sequence)));
		List<Interval> ret = new ArrayList<Interval>();
		for (Document doc : iter) {
			ret.add(Interval.load(doc));
		}
		return ret;
	}

	public static Interval findById(ObjectId id) {
		Document doc = new Document("_id", id);
		FindIterable<Document> iter = getCollection().find(doc);
		doc = iter.first();
		return doc != null ? load(doc) : null;
	}

	public Interval() {
	}

	public Interval(Date start, Date end) {
		this.start = start;
		this.end = end;
	}

	public void delete() {
		if (id != null) {
			getCollection().deleteOne(new Document("_id", id));
		}
	}

}