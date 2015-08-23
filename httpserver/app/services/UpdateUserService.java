package services;

import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import exceptions.ServiceException;
import models.KeyValueFile;
import models.User;


// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class UpdateUserService extends Service<Void> {

	private JSONObject info;
	private User user;
	private List<KeyValueFile> files;
	
	public UpdateUserService(final String uid,final JSONObject info, List<KeyValueFile>  files) {

		this.user = uid!=null? User.findById(new ObjectId(uid)):null;
		
		this.info = info;
		
		this.files = files;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public Void dispatch() {
		Document properties = Document.parse(info.toString());
		for(KeyValueFile kvf : files){
			UploadFileService service = new UploadFileService(kvf);
			String photoUrl = "";
			try {
				photoUrl = service.execute();
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			properties.put(kvf.getKey(), photoUrl);
		}
		
		user.setPublicProperties( properties);
		user.save();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#canExecute()
	 */
	@Override
	public boolean canExecute() {
		return user!=null;
	}
	


	
	

}
