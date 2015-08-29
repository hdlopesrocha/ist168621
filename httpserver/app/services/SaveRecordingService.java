package services;

import java.util.Date;

import org.bson.types.ObjectId;

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
	private Date start;
	private String name;
	private String type;
	private String url;
	private Date end;
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
	public SaveRecordingService(final KeyValueFile anex, final String url, final String groupId, final String userId,
			Date start, Date end, String name, String type, String interval) {
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
		if (url == null) {
			UploadFileService uploadService = new UploadFileService(anex);
			url = uploadService.execute();
		}
		synchronized (LOCK) {
			Interval inter = null;

			if (interval == null) {
				inter = new Interval(start, end);
				inter.save();
				interval = inter.getId();
			} else {
				inter = Interval.findById(interval);
				if (inter.getStart() == null) {
					inter.setStart(start);
				}
				inter.setEnd(end);
				inter.save();
			}

			Recording rec = new Recording(groupId, userId, start, end, name, type, url, Recording.countByGroup(groupId),
					inter.getId());
			rec.save();
			return rec;

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
