package services;

import java.io.IOException;
import java.util.UUID;

import models.KeyValueFile;

import com.mongodb.gridfs.GridFSInputFile;

// TODO: Auto-generated Javadoc
/**
 * The Class SendMessageService.
 */
public class UploadFileService extends Service<String> {

	private KeyValueFile anex;
	private String uid;

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
	public UploadFileService(final KeyValueFile anex, final String uid) {
		this.anex = anex;
		this.uid = uid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public String dispatch() {

		String keyName = uid + "/" + UUID.randomUUID().toString() + "/"
				+ anex.getName();

		try {
			GridFSInputFile file = files.createFile(anex.getValue());
			file.setFilename(keyName);
			file.save();

			return keyName;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
