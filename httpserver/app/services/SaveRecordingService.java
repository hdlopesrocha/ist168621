package services;

import java.text.ParseException;
import java.util.Date;

import org.bson.types.ObjectId;

import exceptions.BadRequestException;
import exceptions.ServiceException;
import models.Interval;
import models.KeyValueFile;
import models.Recording;

// TODO: Auto-generated Javadoc
/**
 * The Class SendMessageService.
 */
public class SaveRecordingService extends Service<Recording> {

	private KeyValueFile anex;
	private ObjectId groupId;
	private ObjectId userId;

	private ObjectId interval;
	private String start;
	private String name;
	private String type;
	private String url;
	private String end;
	private static Object LOCK = new Object();

	/**
	 * Instantiates a new send message service.
	 *
	 * @param username
	 *            the username
	 * @param groupId
	 *            the group id
	 * @param content
	 *            the content
	 * @param anex
	 */
	public SaveRecordingService(final KeyValueFile anex, final String url, final String groupId, final String userId, String start,
			String end, String name, String type, String interval) {
		this.anex = anex;
		this.groupId = new ObjectId(groupId);
		this.userId = new ObjectId(userId);
		this.interval = interval != null ? new ObjectId(interval) : null;
		this.start = start;
		this.end = end;
		this.name = name;
		this.type = type;
		this.url = url;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public Recording dispatch() throws ServiceException {
		if(url==null){
			UploadFileService uploadService = new UploadFileService(anex);
			url = uploadService.execute();
		}
		synchronized (LOCK) {
			Interval inter = null;
			try {
				Date dateStart = Recording.FORMAT.parse(start);
				Date dateEnd = Recording.FORMAT.parse(end);

				if (interval == null) {
					inter = new Interval(dateStart, dateEnd);
					inter.save();
					interval = inter.getId();
				} else {
					inter = Interval.findById(interval);
					if(inter.getStart()==null){
						inter.setStart(dateStart);
					}
					inter.setEnd(dateEnd);
					inter.save();
				}

				
				Recording rec = new Recording(groupId, userId, dateStart, dateEnd, name, type, url,	Recording.countByGroup(groupId), inter.getId());
				rec.save();
				return rec;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new BadRequestException();
			}

		}
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#canExecute()
	 */
	@Override
	public boolean canExecute() {
		return true;
	}

}
