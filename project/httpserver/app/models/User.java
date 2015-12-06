package models;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import main.Tools;
import services.Service;

public class User implements Comparable<User> {
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof User){
			return id!=null && id.equals(((User) obj).getId());
		}
		
		
		return super.equals(obj);
	}



	private String hash, salt, token;
	private ObjectId id = null;
	private static MongoCollection<Document> collection;
	
	
	public static MongoCollection<Document> getCollection(){
		if(collection==null)
			collection = Service.getDatabase().getCollection("users");
		return collection;
			
	}

	public void save() {
		Document doc = new Document();
		if (id != null)
			doc.put("_id", id);

		doc.put("hash", hash);
		doc.put("salt", salt);
		doc.put("token", token);

		
		if (id == null)
			getCollection().insertOne(doc);
		else
			getCollection().replaceOne(new Document("_id", id), doc);
				
		id = doc.getObjectId("_id");

	}

	private static User load(Document doc) {
		User user = new User();
		user.id =doc.getObjectId("_id");
		user.hash = doc.getString("hash");
		user.salt = doc.getString("salt");
		user.token = doc.getString("token");
		return user;
	}

	public List<User> getRelations(){
		List<User> ans = new ArrayList<User>();
		for(Relation rel : Relation.listFrom(getId())){
			if(rel.getToState()){
				User u = User.findById(rel.getTo());
				ans.add(u);	
			}
		}
		for(Relation rel : Relation.listTo(getId())){
			if(rel.getFromState()){
				User u = User.findById(rel.getFrom());
				ans.add(u);		
			}
		}
		return ans;
	}
	
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public static User findByEmail(String email) {
		Document doc = new Document("email", email);
		FindIterable<Document> iter = getCollection().find(doc);
		doc = iter.first();
		return doc != null ? load(doc) : null;
	}

	public static List<User> search(String query) {
		Pattern regex = Pattern.compile(query, Pattern.CASE_INSENSITIVE);	
		BasicDBList or = new BasicDBList();
		or.add(new Document("public.name",regex));
		or.add(new Document("email",regex));
		
		Document doc = new Document("$or", or);
		FindIterable<Document> iter = getCollection().find(doc);
		Iterator<Document> i = iter.iterator();
		List<User> ret = new ArrayList<User>();
		while(i.hasNext()){
			ret.add(User.load(i.next()));
		}
		return ret;
	}
	

	public static User findById(ObjectId id) {
		Document doc = new Document("_id", id);
		FindIterable<Document> iter = getCollection().find(doc);
		doc = iter.first();
		return doc != null ? load(doc) : null;
	}
	
	public static User findByToken(String token) {
		Document doc = new Document("token", token);
		FindIterable<Document> iter = getCollection().find(doc);
		doc = iter.first();
		return doc != null ? load(doc) : null;
	}

	public User() {

	}

	public User(String password) {
		setPassword(password);
	}



	public boolean check(String password) {
		return getHash(password, this.salt).equals(this.hash);
	}

	public void setPassword(String password) {
		this.salt = Tools.getRandomString(32);
		this.hash = getHash(password, salt);
	}



	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	


	/**
	 * Sha1.
	 *
	 * @param input
	 *            the input
	 * @return the string
	 * @throws NoSuchAlgorithmException
	 *             the no such algorithm exception
	 */
	private static String sha1(final String input)
			throws NoSuchAlgorithmException {
		final MessageDigest mDigest = MessageDigest.getInstance("SHA1");
		final byte[] result = mDigest.digest(input.getBytes());
		final StringBuffer sb = new StringBuffer();
		for (int i = 0; i < result.length; i++) {
			sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16)
					.substring(1));
		}

		return sb.toString();
	}



	/**
	 * Gets the hash.
	 *
	 * @param password
	 *            the password
	 * @param salt
	 *            the salt
	 * @return the hash
	 */
	private static String getHash(final String password, final String salt) {
		try {
			return sha1(salt + password);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public int compareTo(User o) {
		return id.compareTo(o.id);
	}



}
