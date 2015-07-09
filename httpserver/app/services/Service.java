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
import com.mongodb.client.model.CreateCollectionOptions;
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
	public static MongoCollection<Document> users,relations,groups,memberships,pubsub;
	protected static GridFS files;
	 static long lastOid = 0l;
		
	@SuppressWarnings("deprecation")
	public static void init() {
		client = new MongoClient(ServerAddress.defaultHost());
		database = client.getDatabase("webrtc");
		files = new GridFS(client.getDB("files"));
		users = database.getCollection("users");
		relations = database.getCollection("relations");
		groups = database.getCollection("groups");
		memberships = database.getCollection("memberships");
		pubsub = database.getCollection("pubsub");
		
		
		if(pubsub.count()==0){
			System.out.println("init pubsub");
			CreateCollectionOptions options = new CreateCollectionOptions();
			options.capped(true);
			options.sizeInBytes(20971520);
			database.createCollection("pubsub", options);
			pubsub = database.getCollection("pubsub");
		

			final Document doc = new Document("ts",lastOid).append("key", "xpto");
			pubsub.insertOne(doc);
		
		}/*
		else {
			for(Document doc : pubsub.find()){
				Long ts =doc.getLong("ts");
				if(ts>lastOid)
					lastOid = ts;
			}
		}
		
		new Thread(new Runnable() {			
			@Override
			public void run() {
				System.out.println("wait > " +lastOid);
	            final Document query = new Document("ts", new Document("$gt", lastOid)).append("key", "xpto");
	            
				MongoCursor<Document> cursor = pubsub.find(query).sort(new Document("$natural", 1)).cursorType(CursorType.TailableAwait).noCursorTimeout(true).iterator();
				Document doc = cursor.next();
				System.out.println("ok! " + doc.getString("msg"));
			}
		}).start();
		
		
		
		new Thread(new Runnable() {			
			@Override
			public void run() {
				
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				final Document doc = new Document("ts", lastOid+1).append("msg", "hello").append("key", "xpto");
				pubsub.insertOne(doc);
				
			}
		}).start();
		*/

		new Thread(new Runnable() {			
			@Override
			public void run() {
				try {
					Thread.sleep(10000);
					new PublishService("xxx", "{'msg':'hello world!'").execute();
				} catch (InterruptedException | ServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();

		
		new Thread(new Runnable() {			
			@Override
			public void run() {
					try {
						Document doc = new SubscribeService("xxx",0l).execute();
						System.out.println(doc.toJson());
					} catch (ServiceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}).start();
		
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
