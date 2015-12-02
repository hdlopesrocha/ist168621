package services;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
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
	protected static DB db;

	private static String DB_NAME = "webrtc";
	
	public static MongoClient getClient() {
		if (client == null) {
			client = new MongoClient(ServerAddress.defaultHost());
		}
		return client;
	}
	
	public static void init(String dbname){
		DB_NAME = dbname;
		db = null;
		database = null;		
		files = new GridFS(getDB());
	}
	
	@SuppressWarnings("deprecation")
	public static DB getDB() {
		if (db == null) {
			db = Service.getClient().getDB(DB_NAME);
		}
		return db;
	}

	public static MongoDatabase getDatabase() {
		if (database == null) {
			database = getClient().getDatabase(DB_NAME);
		}
		return database;
	}
	
	
	protected static MongoClient client;
	protected static MongoDatabase database;
	protected static GridFS files;
	static long lastOid = 0l;
		
	public void reset(){
		getDatabase().drop();
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
