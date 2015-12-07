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
public class CreateRecordingService extends Service<Recording> {

	private KeyValueFile anex;
	private ObjectId groupId;
	private ObjectId owner;

	private ObjectId interval;
	private Date start;
	private String name;
	private String type;
	private String url;
	private Interval inter = null;
	private Date end;
	private static Object LOCK = new Object();

	public Interval getInterval(){
		return inter;
	}
	
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
	public CreateRecordingService(final KeyValueFile anex, final String url, final String groupId, final String owner,
			Date start, Date end, String name, String type, String interval) {
		this.anex = anex;
		this.groupId = new ObjectId(groupId);
		this.owner = owner !=null ? new ObjectId(owner) : null;
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

			if (interval == null) {
				inter = new Interval(groupId, start, end);
				inter.save();
				interval = inter.getId();
			} else {
				inter = Interval.findById(interval);
				if(inter==null){
					return null;
				}else if (inter.getStart() == null) {
					inter.setStart(start);
				}
				inter.setEnd(end);
				inter.save();
			}

			Recording rec = new Recording(groupId, owner, start, end, name, type, url, Recording.countByGroup(groupId),
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
