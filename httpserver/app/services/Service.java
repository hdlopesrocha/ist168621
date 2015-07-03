package services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.gridfs.GridFS;

import exceptions.ServiceException;
import exceptions.UnauthorizedException;

// TODO: Auto-generated Javadoc
/**
 * The Class Service.
 *
 * @param <T>
 *            the generic type
 */
public abstract class Service<T> {
	private static final String allDigits = "0987654321";

	private static final String allChars = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM0987654321";
	private static Random random = new Random();
	protected static MongoClient client;
	protected static MongoDatabase database;
	public static MongoCollection<Document> users,relations;
	protected static GridFS files;

	@SuppressWarnings("deprecation")
	public static void init() {
		client = new MongoClient(ServerAddress.defaultHost());
		database = client.getDatabase("webrtc");
		files = new GridFS(client.getDB("files"));
		users = database.getCollection("users");
		relations = database.getCollection("relation");
	}

	/**
	 * Gets the random salt.
	 *
	 * @param size
	 *            the size
	 * @return the random salt
	 */
	public static String getRandomString(int size) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < size; ++i) {
			builder.append(allChars.charAt(random.nextInt(allChars.length())));
		}
		return builder.toString();
	}

	static public String friendlyTime(String time) {
		try {

			if (time.charAt(time.length() - 1) != 'Z') {
				time += "Z";
			}
			
			
             ZonedDateTime zdt = ZonedDateTime.parse(time); 
           LocalDateTime ldt =  zdt.toLocalDateTime();
             
             
             return  String.format("%02d", ldt.getDayOfMonth())+"/"+ String.format("%02d", ldt.getMonthValue())+"/"+ String.format("%04d", ldt.getYear())
             + " " + String.format("%02d", ldt.getHour())+":"+ String.format("%02d", ldt.getMinute()) +":"+String.format("%02d", ldt.getSecond());
		} catch (Exception e) {
			e.printStackTrace();
			// return "00/00/0000 00:00:00";
		}
		return time;
	}

	public static String getCurrentTime() {
		return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
				.withZone(ZoneOffset.UTC).format(Instant.now());
	}

	/**
	 * Execute.
	 *
	 * @return the t
	 * @throws ServiceException
	 *             the service exception
	 */
	public T execute() throws ServiceException {
		if (canExecute()) {
			return dispatch();
		} else {
			throw new UnauthorizedException();
		}

	}

	/**
	 * Dispatch.
	 *
	 * @return the t
	 * @throws ServiceException
	 */
	public abstract T dispatch() throws ServiceException;

	/**
	 * Can execute.
	 *
	 * @return true, if successful
	 */
	public abstract boolean canExecute();
}
