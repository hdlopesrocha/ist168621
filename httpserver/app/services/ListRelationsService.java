package services;

import java.util.ArrayList;
import java.util.List;

import models.Relation;
import models.User;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class ListRelationsService extends Service<List<User>> {

	private User caller;
	private String callerEmail;

	
	public ListRelationsService(String email) {
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
			if(r.getToState() && r.getFromState()){
				ret.add(User.findById(r.getFrom()));
			}
		}
		for(Relation r : Relation.listFrom(caller.getId())){
			if(r.getToState() && r.getFromState()){
				ret.add(User.findById(r.getTo()));
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
