package main;

import org.kurento.client.MediaElement;
import org.kurento.client.MediaProfileSpecType;
import org.kurento.client.RecorderEndpoint;
import org.kurento.client.internal.server.KurentoServerException;
import org.kurento.repository.service.pojo.RepositoryItemRecorder;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


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
    private static boolean useRepo = true;
    private RecorderEndpoint recorder;
    private RepositoryItemRecorder item;
    private File tempFile;
    private Timer timer = new Timer();
    private RecorderHandler handler;

    public Recorder(MediaElement endPoint, int duration, RecorderHandler handler) {
        this.handler = handler;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (endPoint) {
                        RecorderEndpoint.Builder builder;
                        if (useRepo) {
                            item = KurentoManager.repository.createRepositoryItem( Collections.emptyMap());
                            builder = new RecorderEndpoint.Builder(endPoint.getMediaPipeline(), item.getUrl());
                        } else {
                            tempFile = File.createTempFile("hyper", ".webm");
                            builder = new RecorderEndpoint.Builder(endPoint.getMediaPipeline(), "file://" + tempFile.getAbsolutePath());
                        }

                        builder.withMediaProfile(MediaProfileSpecType.WEBM).stopOnEndOfStream();
                        recorder = builder.build();
                        endPoint.connect(recorder);
                        recorder.record();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                release();
                            }
                        },duration);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public synchronized void release() {
        try {
            if (timer != null) {
                timer.cancel();
                timer.purge();
                timer = null;
                recorder.release();
            }
            if (useRepo) {
                handler.onFileRecorded(new Date(), item.getId());
            } else {
                handler.onFileRecorded(new Date(), "file://" + tempFile.getAbsolutePath());
            }

        } catch (KurentoServerException e) {
            System.out.println("recorder error!");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
