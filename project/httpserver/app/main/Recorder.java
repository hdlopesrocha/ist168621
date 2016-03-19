package main;

import org.kurento.client.MediaElement;
import org.kurento.client.MediaProfileSpecType;
import org.kurento.client.RecorderEndpoint;
import org.kurento.client.internal.server.KurentoServerException;
import org.kurento.repository.service.pojo.RepositoryItemRecorder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Date;
import java.util.Map;


/**
 * The Class Recorder.
 */
public class Recorder {

    private static boolean useRepo = false;

    /**
     * Record.
     *
     * @param endPoint the end point
     * @param duration the duration
     * @param handler the handler
     */

    private RecorderEndpoint recorder;
    private RepositoryItemRecorder item;
    private File tempFile;

    public Recorder(MediaElement endPoint, int duration, RecorderHandler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    System.out.println("Recording...");

                    RecorderEndpoint.Builder builder;

                    if (useRepo) {
                        item = KurentoManager.repository.createRepositoryItem(Collections.emptyMap());
                        builder = new RecorderEndpoint.Builder(endPoint.getMediaPipeline(), item.getUrl());
                    } else {
                        tempFile = File.createTempFile("hyper", ".webm");
                        builder = new RecorderEndpoint.Builder(endPoint.getMediaPipeline(), "file://" + tempFile.getAbsolutePath());
                    }
                    builder.stopOnEndOfStream().withMediaProfile(MediaProfileSpecType.WEBM).build();
                    recorder = builder.build();


                    endPoint.connect(recorder);
                    recorder.record();

                    try {
                        Thread.sleep(duration);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    try {
                        recorder.stop();
                        endPoint.disconnect(recorder);
                        recorder.release();
                    } catch (KurentoServerException e) {
                        System.out.println("recorder error!");
                    }


                    if (useRepo) {
                        handler.onFileRecorded(new Date(), item.getId());
                    } else {
                        handler.onFileRecorded(new Date(), "file://" + tempFile.getAbsolutePath());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
//  ffmpeg -i osi.mp4 -ss 0 -t 10  -filter:v "setpts=0.5*PTS"  -filter:a "atempo=2.0"  -c:a libvorbis -c:v libvpx -pass 2 -b:v 1000K -threads 8 -speed 4 -f webm test.webm

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
