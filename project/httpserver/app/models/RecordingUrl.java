package models;

import org.bson.Document;

public class RecordingUrl {
        private String url;

        public String getUrl() {
            return url;
        }

        public RecordingUrl(String uid, String sid,String url) {
            this.url = url;
            this.uid = uid;
            this.sid = sid;
        }


        public RecordingUrl(Document doc) {
            this.url = doc.getString("url");
            this.uid = doc.getString("uid");
            this.sid = doc.getString("sid");
        }

        public Document toDocument(){
            Document doc  =new Document();
            doc.put("url",url);
            doc.put("sid",sid);
            doc.put("uid",uid);
            return doc;
        }

        public String getUid() {
            return uid;

        }

        public String getSid() {
            return sid;
        }

        private String uid;
        private String sid;
    }