package services;

import models.User;


// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class HasPermissionService extends Service<Boolean> {

	/** The user. */
	private User user;
	
	private String perm;

	
	
	public HasPermissionService(final String email,final String perm) {		
		this.user = email!=null? User.findByEmail(email):null;
		this.perm = perm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public Boolean dispatch() {
		
		for(Object str : user.getPermissions()){
			if(perm.equals(str)){
				return true;
			}
		}
		
		return false;
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
