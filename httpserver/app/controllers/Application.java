package controllers;

import org.bson.types.ObjectId;

import models.Group;
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

    public Result group(String groupId) {
    	
    	
    	
    	if(session("email")!=null){
    		ObjectId oid = new ObjectId(groupId);
    		Group group = Group.findById(oid); 		
    		return ok(views.html.group.render(groupId,group.getName()));
    	} else {
    		return ok(views.html.sign.render());
    		
    	}    	
    }

    
}
