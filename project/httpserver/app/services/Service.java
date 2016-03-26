package services;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.mongodb.gridfs.GridFS;
import exceptions.ServiceException;
import exceptions.UnauthorizedException;



/**
 * The Class Service.
 *
 * @param <T> the generic type
 */
public abstract class Service<T> {
    
    /** The files. */
    static GridFS files;

    /** The db. */
    private static DB db;
    
    /** The client. */
    private static MongoClient client;
    
    /** The database. */
    private static MongoDatabase database;
    
    /** The db name. */
    private static String DB_NAME = "webrtc";

    /**
     * Gets the client.
     *
     * @return the client
     */
    private synchronized static MongoClient getClient() {
        if (client == null) {
            client = new MongoClient(ServerAddress.defaultHost());
        }
        return client;
    }

    /**
     * Inits the.
     *
     * @param dbname the dbname
     */
    public static void init(String dbname) {
        DB_NAME = dbname;
        db = null;
        database = null;
        files = new GridFS(getDB());
    }

    /**
     * Gets the db.
     *
     * @return the db
     */
    @SuppressWarnings("deprecation")
    private synchronized static DB getDB() {
        if (db == null) {
            db = Service.getClient().getDB(DB_NAME);
        }
        return db;
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    public synchronized static MongoDatabase getDatabase() {
        if (database == null) {
            database = getClient().getDatabase(DB_NAME);
        }
        return database;
    }

    /**
     * Reset.
     */
    public static void reset() {
        getDatabase().drop();
    }


    /**
     * Execute.
     *
     * @return the t
     * @throws ServiceException the service exception
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
     * @throws ServiceException the service exception
     */
    protected abstract T dispatch() throws ServiceException;

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    protected abstract boolean canExecute();
}
