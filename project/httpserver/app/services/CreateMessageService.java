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

	private ObjectId target;
	private ObjectId source;
	private Date time;
	private String text;
	
	
	/**
	 * Instantiates a new send message service.
	 *
	 * @param username
	 *            the username
	 * @param target
	 *            the group id
	 * @param content
	 *            the content
	 * @param anex
	 */
	public CreateMessageService(final String source, final String target,final String text) {
		this.target = new ObjectId(target);
		this.source = new ObjectId(source);
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
		Message msg = new Message(target,source,time,text);
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
