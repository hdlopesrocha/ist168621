package services;

import models.PubSub;
import org.bson.Document;

// TODO: Auto-generated Javadoc

/**
 * The Class AuthenticateUserService.
 */
public class PublishService extends Service<Void> {

    private String key;

    public PublishService(String key) {
        this.key = key;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public Void dispatch() {


        Document doc = new Document();
        doc.append("ts", generateTs());
        doc.append("key", key);

        PubSub.getCollection().insertOne(doc);
        return null;
    }

    private Long generateTs() {

        Document find = PubSub.getKeyCollection().find(new Document("key", key)).first();
        if (find != null) {
            Document doc = PubSub.getKeyCollection().findOneAndUpdate(new Document("key", key), new Document("$inc", new Document("ts", 1)));
            return doc.getLong("ts") + 1;
        } else {
            PubSub.getKeyCollection().insertOne(new Document("key", key).append("ts", 0l));
            return 0l;
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {
        return true;
    }

}
