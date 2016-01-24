package main;

import org.kurento.client.MediaElement;
import org.kurento.client.RecorderEndpoint;
import org.kurento.repository.service.pojo.RepositoryItemRecorder;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

public class MyRecorder {

    public static void record(MediaElement endPoint, Date begin, int duration, RecorderHandler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> metadata = Collections.emptyMap();
                RepositoryItemRecorder item = KurentoManager.repository.createRepositoryItem(metadata);

                RecorderEndpoint recorder = new RecorderEndpoint.Builder(endPoint.getMediaPipeline(), item.getUrl()).build();
                endPoint.connect(recorder);
                recorder.record();

                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                recorder.stop();
                endPoint.disconnect(recorder);
                recorder.release();
                final Date end = new Date();
                handler.onFileRecorded(begin, end, item.getId());
            }
        }).start();


    }


    public interface RecorderHandler {
        void onFileRecorded(Date begin, Date end, String filepath);
    }

}
