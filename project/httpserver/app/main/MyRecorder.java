package main;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.kurento.client.MediaElement;
import org.kurento.client.MediaProfileSpecType;
import org.kurento.client.RecorderEndpoint;
import org.kurento.repository.service.pojo.RepositoryItemRecorder;

public class MyRecorder {

	public interface RecorderHandler {

		String onFileRecorded(Date begin, Date end, String filepath, String filename, String intervalId);
	}

	private RecorderEndpoint recorder;
	private Object recorderLock = new Object();
	private Thread recorderThread = null;
	private Runnable recorderRunnable = null;
	private Date begin;
	
	private String intervalId = null;

	
	public MyRecorder(MediaElement endPoint, RecorderHandler handler) {

		this.recorderRunnable = new Runnable() {

			@Override
			public void run() {
				begin = new Date();

				while (recorderThread != null) {
					try {
						Map<String, String> metadata = Collections.emptyMap();
						RepositoryItemRecorder item =  KurentoManager.repository.createRepositoryItem(metadata);
						
						
						recorder = new RecorderEndpoint.Builder(endPoint.getMediaPipeline(), item.getUrl()).withMediaProfile(MediaProfileSpecType.WEBM).build();
					//	recorder.setVideoFormat(new VideoCaps(VideoCodec.H264, new Fraction(1, 15)));
					//	recorder.setAudioFormat(new AudioCaps(AudioCodec.OPUS, 16000));
						endPoint.connect(recorder);
						recorder.record();

						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						recorder.stop();
						final Date end = new Date();
						new Thread(new Runnable() {
							
							@Override
							public void run() {								
								intervalId = handler.onFileRecorded(begin, end, item.getUrl(), item.getId(),intervalId);
							}
						}).start();

						endPoint.disconnect(recorder);
						recorder.release();

						// continuous parts (although not true)
						begin = end;
					} catch (Exception e) {
						System.out.println("RECORDER STOP!");
						break;
					}
				}
			}
		};
	}

	public void start() {
		synchronized (recorderLock) {
			if (recorderThread == null) {
				recorderThread = new Thread(recorderRunnable);
				recorderThread.start();
			}
		}
	}

	public void stop() {
		synchronized (recorderLock) {
			recorderThread = null;
		}
	}

	public void close() {
		stop();
		recorder.release();
	}

}
