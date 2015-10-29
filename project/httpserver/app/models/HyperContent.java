package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import services.Service;

public class HyperContent {
	
	private Date start, end;
	private String content = null;
	private ObjectId id , groupId= null;
	private static MongoCollection<Document> collection;

	public HyperContent(ObjectId gid, Date start, Date end, String content) {
		this.start = start;
		this.end = end;
		this.groupId = gid;
		this.content = content;
	}
	
	public HyperContent() {
	}

	public static MongoCollection<Document> getCollection(){
		if(collection==null)
			collection = Service.getDatabase().getCollection("hypercontent");
		return collection;		
	}
		
	public void save() {
		Document doc = new Document();
		if (id != null)
			doc.put("_id", id);

		doc.put("start", start);
		doc.put("end", end);
		doc.put("gid", groupId);
		doc.put("content", content);

		if (id == null)
			getCollection().insertOne(doc);
		else
			getCollection().replaceOne(new Document("_id", id), doc);
				
		id = doc.getObjectId("_id");

	}

	public static HyperContent load(Document doc) {
		HyperContent content = new HyperContent();
		content.id = doc.getObjectId("_id");
		content.start = doc.getDate("start");
		content.end = doc.getDate("end");
		content.groupId = doc.getObjectId("gid");
		content.content = doc.getString("content");

		return content;
	}

	public Date getStart() {
		return start;
	}

	public Date getEnd() {
		return end;
	}

	public ObjectId getGroupId() {
		return groupId;
	}

	public String getContent() {
		return content;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}
	
	public static HyperContent findById(ObjectId id) {
		Document doc = new Document("_id", id);
		FindIterable<Document> iter = getCollection().find(doc);
		doc = iter.first();
		return doc != null ? load(doc) : null;
	}


	public void delete() {
		if (id != null)
			getCollection().deleteOne(new Document("_id", id));		
	}

	public static List<HyperContent> listAll() {
		FindIterable<Document> iter = getCollection().find(new Document());
		List<HyperContent> ret = new ArrayList<HyperContent>();
		for(Document doc : iter){
			ret.add(load(doc));
		}
		return ret;
	}

	

}