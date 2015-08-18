package services;

import org.bson.types.ObjectId;

import models.Relation;
import models.User;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticateUserService.
 */
public class DenyRelationService extends Service<Void> {

	private User from, to;

	public DenyRelationService(String from, String to) {
		this.from = User.findById(new ObjectId(from));
		this.to = User.findById(new ObjectId(to));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public Void dispatch() {
		Relation rel = Relation.findByEndpoint(from.getId(), to.getId());
		if (rel == null) {
			rel = Relation.findByEndpoint(to.getId(), from.getId());
		}

		if (rel != null) {
			rel.delete();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#canExecute()
	 */
	@Override
	public boolean canExecute() {
		return from != null && to != null;
	}

}