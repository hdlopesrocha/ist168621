package services;


import exceptions.ServiceException;
import models.Data;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;


/**
 * The Class ListOwnerAttributesService.
 */
public class ListOwnerAttributesService extends Service<Document> {

    /** The user. */
    private ObjectId owner;
    
    /** The caller. */
    private ObjectId caller;
    
    /** The projection. */
    private Document projection;

    /**
     * Instantiates a new list owner attributes service.
     *
     * @param caller the caller
     * @param caller the caller id
     * @param projection the projection
     */
    public ListOwnerAttributesService(String caller, String owner, List<String> projection) {
        this.owner = new ObjectId(owner);
        this.caller = new ObjectId(caller);
        if (projection != null) {
            this.projection = new Document();
            for (String str : projection) {
                this.projection.append("data." + str, 1);
            }
        }
    }


    /* (non-Javadoc)
     * @see services.Service#dispatch()
     */
    @Override
    public Document dispatch() throws ServiceException {
        Data data = Data.findByOwner(owner);
        Document doc = null;

        if (data != null) {
            doc = new Document();

            for(Document d : data.getData()){
                String key = d.getString("k");
                Object value = d.get("v");
                if((data.hasReadPermission(caller, key))) {
                        doc.put(key, value);
                }

            }

        }

        return doc;
    }

    /* (non-Javadoc)
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {
        return true;
    }

}
