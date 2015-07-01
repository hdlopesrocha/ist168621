package models;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import services.Service;

import com.mongodb.client.FindIterable;

public class User {
	private String email, name, hash, salt, token;
	private ObjectId id = null;
	private List<String> permissions;
	private Document publicProperties= new Document(); 
	private Document privateProperties = new Document();

	public void save() {
		Document doc = new Document();
		if (id != null)
			doc.put("_id", id);

		doc.put("email", email);
		doc.put("name", name);
		doc.put("hash", hash);
		doc.put("salt", salt);
		doc.put("token", token);
		doc.put("permissions", permissions);
		doc.put("public", publicProperties);
		doc.put("private", privateProperties);
		

		if(id!=null)
			Service.users.deleteOne(new Document("_id",id));
			
		Service.users.insertOne(doc);
		
		
		
		
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
		user.setName(doc.getString("name"));
		user.setHash(doc.getString("hash"));
		user.setSalt(doc.getString("salt"));
		user.setToken(doc.getString("token"));
		user.setPublicProperties((Document) doc.get("public"));
		user.setPrivateProperties((Document) doc.get("private"));
		

		user.setPermissions((List<String>) doc.get("permissions"));
		return user;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public static User findByEmail(String email) {
		Document doc = new Document("email", email);
		FindIterable<Document> iter = Service.users.find(doc);
		doc = iter.first();
		return doc != null ? load(doc) : null;
	}


	public static User findById(ObjectId id) {
		Document doc = new Document("_id", id);
		FindIterable<Document> iter = Service.users.find(doc);
		doc = iter.first();
		return doc != null ? load(doc) : null;
	}
	
	public static User findByToken(String token) {
		Document doc = new Document("token", token);
		FindIterable<Document> iter = Service.users.find(doc);
		doc = iter.first();
		return doc != null ? load(doc) : null;
	}

	public User() {

	}

	public User(String email, String name, String password,
			Document properties, List<String> permissions) {
		this.name = name;
		this.email = email;
		this.publicProperties =  properties;
		setPassword(password);
		this.permissions = permissions;
	}



	public boolean check(String password) {
		return getHash(password, this.salt).equals(this.hash);
	}

	public void setPassword(String password) {
		this.salt = Service.getRandomString(32);
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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



}
