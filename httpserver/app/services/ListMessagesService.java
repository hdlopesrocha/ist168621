package services;

import java.util.List;

import org.bson.types.ObjectId;

import exceptions.ServiceException;
import models.Message;

// TODO: Auto-generated Javadoc
/**
 * The Class SendMessageService.
 */
public class ListMessagesService extends Service<List<Message>> {

	private ObjectId groupId;

	
	
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
	public ListMessagesService(final String groupId) {
		this.groupId = new ObjectId(groupId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public List<Message> dispatch() throws ServiceException {
		return Message.listByGroup(groupId);
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
