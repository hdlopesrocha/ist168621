package models;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.types.ObjectId;

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



	private String email, hash, salt, token;
	private ObjectId id = null;
	private List<String> permissions;
	private Document publicProperties= new Document(); 
	private Document privateProperties = new Document();
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

		doc.put("email", email);
		doc.put("hash", hash);
		doc.put("salt", salt);
		doc.put("token", token);
		doc.put("permissions", permissions);
		doc.put("public", publicProperties);
		doc.put("private", privateProperties);
		
		if (id == null)
			getCollection().insertOne(doc);
		else
			getCollection().replaceOne(new Document("_id", id), doc);
				
		id = doc.getObjectId("_id");

	}

	public Document getPrivateProperties() {
		return privateProperties;
	}



	public void setPrivateProperties(Document privateProperties) {
		this.privateProperties = privateProperties;
	}


	@SuppressWarnings("unchecked")
	private static User load(Document doc) {
		User user = new User();
		user.setId(doc.getObjectId("_id"));
		user.setEmail(doc.getString("email"));
		user.setHash(doc.getString("hash"));
		user.setSalt(doc.getString("salt"));
		user.setToken(doc.getString("token"));
		user.setPublicProperties((Document) doc.get("public"));
		user.setPrivateProperties((Document) doc.get("private"));
		

		user.setPermissions((List<String>) doc.get("permissions"));
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
		Pattern regex = Pattern.compile(query);		
		Document doc = new Document("email", regex);
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

	public User(String email, String password,
			Document properties, List<String> permissions) {
		this.email = email;
		this.publicProperties =  properties;
		setPassword(password);
		this.permissions = permissions;
	}



	public boolean check(String password) {
		return getHash(password, this.salt).equals(this.hash);
	}

	public void setPassword(String password) {
		this.salt = Tools.getRandomString(32);
		this.hash = getHash(password, salt);
	}

	public Document getPublicProperties() {
		return publicProperties;
	}

	public void setPublicProperties(Document properties) {
		this.publicProperties = properties;
	}

	public List<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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
