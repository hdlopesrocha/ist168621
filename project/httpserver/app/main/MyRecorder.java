package main;

import org.kurento.client.MediaElement;
import org.kurento.client.RecorderEndpoint;
import org.kurento.repository.service.pojo.RepositoryItemRecorder;

import java.util.Collections;
import java.util.Date;
import java.util.Map;


/**
 * The Class MyRecorder.
 */
public class MyRecorder {

    /**
     * Record.
     *
     * @param endPoint the end point
     * @param duration the duration
     * @param handler the handler
     */
    public static void record(MediaElement endPoint, int duration, RecorderHandler handler) {
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
                handler.onFileRecorded(new Date(), item.getId());
            }
        }).start();


    }


    /**
     * The Interface RecorderHandler.
     */
    public interface RecorderHandler {

        /**
         * On file recorded.
         *
         * @param end the end
         * @param filepath the filepath
         */
        void onFileRecorded(Date end, String filepath);
    }

}
