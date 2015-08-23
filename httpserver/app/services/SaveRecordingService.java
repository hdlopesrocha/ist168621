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
	private ObjectId groupId;
	private ObjectId userId;

	private String start;
	private String name;
	private String type;
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
	public SaveRecordingService(final KeyValueFile anex, final String groupId, final String userId,String start, String end, String name, String type) {
		this.anex = anex;
		this.groupId = new ObjectId(groupId);
		this.userId = new ObjectId(userId);
		this.start = start;
		this.end = end;
		this.name = name;
		this.type = type;
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

			
			Recording rec = new Recording(groupId,userId, start, end,name,type, url,Recording.countByGroup(groupId));
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
