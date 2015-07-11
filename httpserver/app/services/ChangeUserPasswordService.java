package services;

import org.bson.types.ObjectId;

import exceptions.ServiceException;
import models.User;


// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class ChangeUserPasswordService extends Service<Void> {

	private String password, oldPassword; 
	private User user;
	
	public ChangeUserPasswordService(final String uid, final String oldPassword,final String password) {
		this.user = uid!=null? User.findById(new ObjectId(uid)):null;
		this.password = password;
		this.oldPassword = oldPassword;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public Void dispatch() {
		
		user.setPassword(password);
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
		Boolean auth = false;
		try {
			auth = new AuthenticateUserService(user.getEmail(), oldPassword).execute();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return user!=null && auth  && password!=null;
	}
	
	




	
	

}
