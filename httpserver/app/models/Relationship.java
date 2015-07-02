package models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;

import services.Service;

public class Relationship {
	private static final short CLOSED = 0;
	private static final short WAITING = 1;
	private static final short OPEN = 2;

	private int fromState, toState;
	private ObjectId from = null, to = null, id = null;

	public int getFromState() {
		return fromState;
	}

	public void setFromState(int fromState) {
		this.fromState = fromState;
	}

	public int getToState() {
		return toState;
	}

	public void setToState(int toState) {
		this.toState = toState;
	}

	public void save() {
		Document doc = new Document();
		if (id != null)
			doc.put("_id", id);

		doc.put("fi", from);
		doc.put("ti", to);
		doc.put("fs", fromState);
		doc.put("ts", toState);

		if (id != null)
			Service.relation.deleteOne(new Document("_id", id));

		Service.relation.insertOne(doc);

		id = doc.getObjectId("_id");

	}

	private static Relationship load(Document doc) {
		Relationship user = new Relationship();
		user.setId(doc.getObjectId("_id"));
		user.setFrom(doc.getObjectId("fi"));
		user.setTo(doc.getObjectId("ti"));
		user.setFromState(doc.getInteger("fs"));
		user.setToState(doc.getInteger("ts"));

		return user;
	}

	public ObjectId getFrom() {
		return from;
	}

	public void setFrom(ObjectId from) {
		this.from = from;
	}

	public ObjectId getTo() {
		return to;
	}

	public void setTo(ObjectId to) {
		this.to = to;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public static Relationship findByEndpoint(ObjectId a, ObjectId b) {
		Document doc = new Document("fi", a).append("ti", b);
		FindIterable<Document> iter = Service.relation.find(doc);
		doc = iter.first();
		return doc != null ? load(doc) : null;
	}

	public static List<Relationship> listFrom(ObjectId ep) {
		Document doc = new Document("fi", ep);
		FindIterable<Document> iter = Service.relation.find(doc);
		doc = iter.first();

		Iterator<Document> i = iter.iterator();
		List<Relationship> ret = new ArrayList<Relationship>();
		while (i.hasNext()) {
			ret.add(load(i.next()));
		}
		return ret;
	}

	public static List<Relationship> listTo(ObjectId ep) {
		Document doc = new Document("ti", ep);
		FindIterable<Document> iter = Service.relation.find(doc);
		doc = iter.first();

		Iterator<Document> i = iter.iterator();
		List<Relationship> ret = new ArrayList<Relationship>();
		while (i.hasNext()) {
			ret.add(load(i.next()));
		}
		return ret;
	}



	
	public static Relationship findById(ObjectId id) {
		Document doc = new Document("_id", id);
		FindIterable<Document> iter = Service.relation.find(doc);
		doc = iter.first();
		return doc != null ? load(doc) : null;
	}

	public Relationship() {
	}

	public Relationship(ObjectId fi, ObjectId ti) {
		this.fromState = CLOSED;
		this.toState = CLOSED;

		this.to = ti;
		this.from = fi;

	}

	public Relationship(int fs, ObjectId fi, ObjectId ti, int ts) {
		this.fromState = fs;
		this.toState = ts;
		this.to = ti;
		this.from = fi;
	}

}