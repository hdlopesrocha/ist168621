package models;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import dtos.AttributeDto;
import dtos.KeyValue;
import org.bson.Document;
import org.bson.types.ObjectId;
import services.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * The Class Data.
 */
public class Attribute {

    private String key;
    private Object value;
    private boolean identifiable;
    private List<ObjectId> readSet;
    private List<ObjectId> writeSet;

    public Attribute(Document doc) {
        key = doc.getString("k");
        value = doc.get("v");
        identifiable = doc.getBoolean("i");
        writeSet = (List<ObjectId>) doc.get("w");
        readSet = (List<ObjectId>) doc.get("r");
    }





    public Attribute(AttributeDto attr) {
        key = attr.getKey();
        value = attr.getValue();
        identifiable = attr.isIdentifiable();


        if(attr.getReadSet()!=null) {
            readSet = new ArrayList<ObjectId>();
            for (String s : attr.getReadSet()) {
                readSet.add(new ObjectId(s));
            }
        }
        if(attr.getWriteSet()!=null) {
            writeSet = new ArrayList<ObjectId>();
            for (String s : attr.getWriteSet()) {
                writeSet.add(new ObjectId(s));
            }
        }
    }

    public Document toDocument(){
        Document doc = new Document();
        doc.put("k",key);
        doc.put("v",value);
        doc.put("i",identifiable);
        doc.put("r", readSet);
        doc.put("w", writeSet);
        return doc;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public boolean isIdentifiable() {
        return identifiable;
    }

    public List<ObjectId> getReadSet() {
        return readSet;
    }

    public List<ObjectId> getWriteSet() {
        return writeSet;
    }

}