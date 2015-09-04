package main;

import java.util.Date;
import java.util.UUID;

import org.kurento.client.RecorderEndpoint;
import org.kurento.client.WebRtcEndpoint;

public class MyRecorder {

	public interface RecorderHandler {

		void onFileRecorded(Date begin, Date end, String filepath, String filename);
	}

	private String name;
	private RecorderEndpoint recorder;
	private WebRtcEndpoint endPoint;
	private Room room;

	private Object recorderLock = new Object();
	private Thread recorderThread = null;
	private Runnable recorderRunnable = null;
	
	public MyRecorder(WebRtcEndpoint endPoint, Room room, RecorderHandler handler) {
		this.name = UUID.randomUUID().toString();
		this.endPoint = endPoint;
		this.room = room;
		
		this.recorderRunnable = new Runnable() {

			@Override
			public void run() {
				Date begin = new Date();

				while (recorderThread!=null) {

					String filename = name + ".webm";
					String filepath = "file:///rec/" + filename;
					
					System.out.println("REC: " + filepath);
					recorder = new RecorderEndpoint.Builder(room.getMediaPipeline(), filepath).build();
					endPoint.connect(recorder);
					recorder.record();
				
					
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					recorder.stop();
					Date end = new Date();
					
					handler.onFileRecorded(begin, end, filepath, filename);

					endPoint.disconnect(recorder);
					recorder.release();

					// continuous parts (although not true)
					begin = end;
				}
			}
		};
	}

	public void start() {
		synchronized (recorderLock) {
			if (recorderThread==null) {
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
	
	

}
