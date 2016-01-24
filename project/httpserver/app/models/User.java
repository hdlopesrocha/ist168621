package models;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import main.Tools;
import org.bson.Document;
import org.bson.types.ObjectId;
import services.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User implements Comparable<User> {

	private String hash, salt, token;
	private ObjectId id = null;

	private static MongoCollection<Document> collection;

	public static MongoCollection<Document> getCollection() {
		if(collection==null)
			collection = Service.getDatabase().getCollection(User.class.getName());
		return collection;
	}

	public static User load(Document doc) {
		User user = new User();
		user.id = doc.getObjectId("_id");
		user.hash = doc.getString("hash");
		user.salt = doc.getString("salt");
		user.token = doc.getString("token");
		return user;
	}

	public User save() {
		Document doc = new Document();
		
		doc.put("hash", hash);
		doc.put("salt", salt);
		doc.put("token", token);

		if(id==null){
			getCollection().insertOne(doc);
			id = doc.getObjectId("_id");
		}
		else {
			doc.put("_id", id);
			getCollection().findOneAndReplace(new Document("_id",id),doc);
		}
		return this;
	}

	public ObjectId getId() {
		return id;
	}


	public static User findById(ObjectId id) {
		Document doc = new Document("_id", id);
		FindIterable<Document> iter = getCollection().find(doc);
		doc = iter.first();
		return doc != null ? load(doc) : null;
	}
	
	public static User findById(String id) {
		if(id!=null){
			Document doc = new Document("_id", new ObjectId(id));
			FindIterable<Document> iter = getCollection().find(doc);
			doc = iter.first();
			return doc != null ? load(doc) : null;
		}
		return null;
	}

	public User() {

	}

	public User(String password) {
		setPassword(password);
		this.token = Tools.getRandomString(32);
	}

	public boolean check(String password) {
		return getHash(password, this.salt).equals(this.hash);
	}

	public void setPassword(String password) {
		this.salt = Tools.getRandomString(32);
		this.hash = getHash(password, salt);
	}

	public String getToken() {
		return token;
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
