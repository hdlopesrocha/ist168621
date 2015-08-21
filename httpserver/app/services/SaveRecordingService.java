package services;

import org.bson.types.ObjectId;

import exceptions.ServiceException;
import models.KeyValueFile;
import models.Recording;

// TODO: Auto-generated Javadoc
/**
 * The Class SendMessageService.
 */
public class SaveRecordingService extends Service<Void> {

	private KeyValueFile anex;
	private ObjectId owner;
	private String start;
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
	public SaveRecordingService(final KeyValueFile anex, final String owner,String start, String end) {
		this.anex = anex;
		this.owner = new ObjectId(owner);
		this.start = start;
		this.end = end;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public Void dispatch() throws ServiceException {
		UploadFileService uploadService = new UploadFileService(anex);
		String url = uploadService.execute();
		synchronized (LOCK) {
			Recording rec = new Recording(owner, start, end, url,Recording.countByOwner(owner));
			rec.save();
		}
		return null;
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
