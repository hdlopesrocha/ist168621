package services;

import models.KeyValueFile;
import models.User;

import org.bson.Document;
import org.json.JSONObject;

import exceptions.ServiceException;


// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class UpdateUserService extends Service<Void> {

	private JSONObject info;
	private User user;
	private KeyValueFile photo;
	
	public UpdateUserService(final String email,final JSONObject info, KeyValueFile  photo) {

		this.user = email!=null? User.findByEmail(email):null;
		
		this.info = info;
		
		this.photo = photo;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public Void dispatch() {
		
		Document properties = Document.parse(info.toString());

		
		if(photo!=null){
			UploadFileService service = new UploadFileService(photo,user.getEmail());
			try {
				properties.put("photo",service.execute());
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
