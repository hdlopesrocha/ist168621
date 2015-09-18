package main;

import java.util.Date;
import java.util.UUID;

import org.kurento.client.MediaElement;
import org.kurento.client.RecorderEndpoint;

public class MyRecorder {

	public interface RecorderHandler {

		void onFileRecorded(Date begin, Date end, String filepath, String filename);
	}

	private RecorderEndpoint recorder;
	private Object recorderLock = new Object();
	private Thread recorderThread = null;
	private Runnable recorderRunnable = null;
	private Date begin;
	
	public MyRecorder(MediaElement endPoint, RecorderHandler handler) {

		this.recorderRunnable = new Runnable() {

			@Override
			public void run() {
				begin = new Date();

				while (recorderThread != null) {
					try {
						final String name = UUID.randomUUID().toString();

						final String filename = name + ".mp4";
						final String filepath = "file:///rec/" + filename;

						recorder = new RecorderEndpoint.Builder(endPoint.getMediaPipeline(), filepath).build();
						endPoint.connect(recorder);
						recorder.record();

						try {
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						recorder.stop();
						final Date end = new Date();
						new Thread(new Runnable() {
							
							@Override
							public void run() {								
								handler.onFileRecorded(begin, end, filepath, filename);
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
