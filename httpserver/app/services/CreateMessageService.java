package services;

import java.util.Date;

import org.bson.types.ObjectId;

import exceptions.ServiceException;
import models.KeyValueFile;
import models.Message;

// TODO: Auto-generated Javadoc
/**
 * The Class SendMessageService.
 */
public class CreateMessageService extends Service<Message> {

	private KeyValueFile anex;
	private ObjectId groupId;
	private ObjectId userId;
	private Date time;
	private String text;
	
	
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
	public CreateMessageService(final String groupId, final String userId,final String text,final KeyValueFile anex) {
		this.anex = anex;
		this.groupId = new ObjectId(groupId);
		this.userId = new ObjectId(userId);
		this.text = text;
		this.time = new Date();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public Message dispatch() throws ServiceException {
		Message msg = new Message(groupId,userId,time,text);
		msg.save();
		return msg;
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
