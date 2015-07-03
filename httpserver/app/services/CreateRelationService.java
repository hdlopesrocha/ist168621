package services;

import models.Relation;
import models.User;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class CreateRelationService extends Service<Void> {

	private User from,to;

	
	public CreateRelationService(String from, String to) {
		this.from = User.findByEmail(from);
		this.to = User.findByEmail(to);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public Void dispatch() {
		Relation rel = Relation.findByEndpoint(from.getId(), to.getId());
		if(rel==null){
			rel =  Relation.findByEndpoint(to.getId(), from.getId());
			if(rel==null){
				rel = new Relation(true, from.getId(), to.getId(), false);	
			} else {
				rel.setToState(true);				
			}
		}else {
			rel.setFromState(true);				
		}
		rel.save();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#canExecute()
	 */
	@Override
	public boolean canExecute() {
		return from!=null && to!=null;
	}

}
