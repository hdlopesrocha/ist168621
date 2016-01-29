package main;

import org.kurento.client.*;
import org.kurento.client.internal.server.KurentoServerException;
import org.kurento.commons.testing.SystemCompatibilityTests;
import org.kurento.repository.service.pojo.RepositoryItemRecorder;

import java.util.Collections;
import java.util.Date;
import java.util.Map;


/**
 * The Class Recorder.
 */
public class Recorder {

    /**
     * Record.
     *
     * @param endPoint the end point
     * @param duration the duration
     * @param handler the handler
     */

    private RecorderEndpoint recorder;
    private RepositoryItemRecorder item;

    public Recorder(MediaElement endPoint, int duration, RecorderHandler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> metadata = Collections.emptyMap();
                item = KurentoManager.repository.createRepositoryItem(metadata);

                if(item==null){
                    System.out.println("item is null");
                }

                if(endPoint==null){
                    System.out.println("endPoint is null");
                }
                System.out.println("Recording...");

                recorder = new RecorderEndpoint.Builder(endPoint.getMediaPipeline(), item.getUrl()).stopOnEndOfStream().withMediaProfile(MediaProfileSpecType.WEBM).build();


                
                endPoint.connect(recorder);
                recorder.record();

                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                handler.onFileRecorded(new Date(), item.getId());

                try {
                    recorder.stop();
                    endPoint.disconnect(recorder);
                    recorder.release();
                }catch (KurentoServerException e){
                    System.out.println("recorder error!");
                }
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
