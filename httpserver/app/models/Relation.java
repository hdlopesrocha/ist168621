package models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;

import services.Service;

public class Relation {
	
	
	public static final short NONE = 0;	// no existing relationship
	public static final short WAITING = 1;	// waiting for endpoint answer
	public static final short CLOSED = 2;	// endpoint didn't accepted or blocked
	public static final short OPEN = 3;	// endpoint accepted 
	
	// in order to have a successful relationship both endPoint states must be OPEN

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
			Service.relations.deleteOne(new Document("_id", id));

		Service.relations.insertOne(doc);

		id = doc.getObjectId("_id");

	}

	private static Relation load(Document doc) {
		Relation user = new Relation();
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

	public static Relation findByEndpoint(ObjectId a, ObjectId b) {
		Document doc = new Document("fi", a).append("ti", b);
		FindIterable<Document> iter = Service.relations.find(doc);
		doc = iter.first();
		return doc != null ? load(doc) : null;
	}

	public static List<Relation> listFrom(ObjectId ep) {
		Document doc = new Document("fi", ep);
		FindIterable<Document> iter = Service.relations.find(doc);
		doc = iter.first();

		Iterator<Document> i = iter.iterator();
		List<Relation> ret = new ArrayList<Relation>();
		while (i.hasNext()) {
			ret.add(load(i.next()));
		}
		return ret;
	}

	public static List<Relation> listTo(ObjectId ep) {
		Document doc = new Document("ti", ep);
		FindIterable<Document> iter = Service.relations.find(doc);
		doc = iter.first();

		Iterator<Document> i = iter.iterator();
		List<Relation> ret = new ArrayList<Relation>();
		while (i.hasNext()) {
			ret.add(load(i.next()));
		}
		return ret;
	}



	
	public static Relation findById(ObjectId id) {
		Document doc = new Document("_id", id);
		FindIterable<Document> iter = Service.relations.find(doc);
		doc = iter.first();
		return doc != null ? load(doc) : null;
	}

	public Relation() {
	}

	public Relation(ObjectId fi, ObjectId ti) {
		this.fromState = NONE;
		this.toState = NONE;

		this.to = ti;
		this.from = fi;

	}

	public Relation(int fs, ObjectId fi, ObjectId ti, int ts) {
		this.fromState = fs;
		this.toState = ts;
		this.to = ti;
		this.from = fi;
	}

}