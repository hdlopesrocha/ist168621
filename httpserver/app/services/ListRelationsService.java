package services;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import models.Relation;
import models.User;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class ListRelationsService extends Service<List<User>> {

	private User caller;

	
	public ListRelationsService(String uid) {
		this.caller = User.findById(new ObjectId(uid));

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
		return caller!=null;
	}

}
