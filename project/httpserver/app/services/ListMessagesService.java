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
	private Long end;
	private int len;

	
	
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
	public ListMessagesService(final String groupId, final Long end, final int len) {
		this.groupId = new ObjectId(groupId);
		this.end = end;
		this.len = len;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public List<Message> dispatch() throws ServiceException {
		return Message.listByGroup(groupId,end,len);
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
