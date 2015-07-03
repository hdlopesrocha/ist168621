package services;

import java.util.ArrayList;
import java.util.List;

import models.Relation;
import models.User;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class ListRelationRequestsService extends Service<List<User>> {

	private User caller;
	private String callerEmail;

	
	public ListRelationRequestsService(String email) {
		this.callerEmail = email;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public List<User> dispatch() {
		List<User> ret = new ArrayList<User>();
		for(Relation r : Relation.listTo(caller.getId())){
			if(!r.getToState()){
				ret.add(User.findById(r.getFrom()));
			}
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#canExecute()
	 */
	@Override
	public boolean canExecute() {
		if(callerEmail==null)
			return false;
		
		this.caller = User.findByEmail(callerEmail);

		return caller!=null;
	}

}
