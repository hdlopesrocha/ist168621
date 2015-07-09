package controllers;

import org.bson.types.ObjectId;

import models.Group;
import models.Membership;
import models.User;
import play.*;
import play.mvc.*;


public class Application extends Controller {

    public Result index() {
    	if(session("email")!=null){
    		return ok(views.html.index.render());
    	} else {
    		return ok(views.html.sign.render());
    		
    	}
    	
    }

    
    
    public Result pubsub() {
    	return ok(views.html.pubsub.render());
    }
    
    public Result group(String groupId) {
    	if(session("email")!=null){
    		ObjectId oid = new ObjectId(groupId);
    		Group group = Group.findById(oid);
    		User user = User.findByEmail(session("email"));
    		
    		if(group!=null && user!=null){
    			
    			Membership membership = Membership.findByUserGroup(user.getId(), group.getId());
    			if(membership!=null){
    				return ok(views.html.group.render(groupId,group.getName(), membership.getId().toString()));
    			}
    		}
    		
    		return redirect("/");
    	} else {
    		return ok(views.html.sign.render());
    		
    	}    	
    }

    
}
