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
public class RegisterUserService extends Service<User> {

	private User user;
	private List<KeyValueFile> files;
	private String email,password;
	private Document properties;
	private List<String> permissions = new ArrayList<String>();
	
	public RegisterUserService(final String email, final String password,  final JSONObject properties, List<KeyValueFile>  files) {
		this.user = User.findByEmail(email);	
		
		
		this.properties = Document.parse(properties.toString());
		this.files = files;
		this.email = email;
		this.password = password;
		
		
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
	public synchronized User dispatch() throws ServiceException {
		if(user!=null){
			throw new ConflictException();
		}
		
		
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
		User newUser = new User(email, password, properties, permissions);

			
		String token = getRandomString(32);
		
		while(User.findByToken(token) !=null){
			token = getRandomString(32);
		}
		
		newUser.setToken(token);
		newUser.save();

		return newUser;
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
