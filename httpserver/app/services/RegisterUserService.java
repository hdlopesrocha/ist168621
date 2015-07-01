package services;

import java.util.ArrayList;
import java.util.List;

import models.KeyValueFile;
import models.User;

import org.bson.Document;
import org.json.JSONObject;

import exceptions.ConflictException;
import exceptions.ServiceException;


// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class RegisterUserService extends Service<String> {

	private User user;
	private KeyValueFile photo;
	private String email,password, name;
	private Document properties;
	private List<String> permissions = new ArrayList<String>();
	
	public RegisterUserService(final String email, final String name,final String password,  final JSONObject properties, KeyValueFile  photo) {
		this.user = User.findByEmail(email);	
		
		
		this.properties = Document.parse(properties.toString());
		this.photo = photo;
		this.email = email;
		this.password = password;
		
		
		this.name = name;
	}
	
	public RegisterUserService addPermission(String permission) {
		permissions.add(permission);
		return this;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public synchronized String dispatch() throws ServiceException {
		if(user!=null){
			throw new ConflictException();
		}
		
		String photoUrl = "";
		

		if(photo!=null){
			UploadFileService service = new UploadFileService(photo,email);
			try {
				photoUrl = service.execute();
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		properties.put("photo", photoUrl);
		
		User newUser = new User(email, name, password, properties, permissions);

			
		String token = getRandomString(32);
		
		while(User.findByToken(token) !=null){
			token = getRandomString(32);
		}
		
		newUser.setToken(token);
		newUser.save();

		return newUser.getToken();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#canExecute()
	 */
	@Override
	public boolean canExecute() {
		

		return password!=null;
	}
	
	

}
