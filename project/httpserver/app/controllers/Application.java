package controllers;

import java.io.File;

import org.bson.types.ObjectId;

import exceptions.ServiceException;
import models.Group;
import models.Membership;
import models.User;
import play.*;
import play.mvc.*;
import play.twirl.api.Html;
import services.JoinGroupInviteService;


public class Application extends Controller {

    public Result index() {
    	if(session("uid")!=null){
    		return ok(views.html.index.render());
    	} else {
    		return ok(views.html.sign.render());
    		
    	}
    	
    }

    public Result template(){
    	return ok(views.html.template.render(0,new Html("")));
    }
    
    public Result stream(String path){
    	File file = new File(path);
    	Result res = ok(file);
    	response().setHeader("Content-type", "video/webm");
    	
    	
    	return res;
    }
    
    
    public Result pubsub() {
    	return ok(views.html.pubsub.render());
    }
    
    public Result group(String groupId) {
    	if(session("uid")!=null){
    		ObjectId oid = new ObjectId(groupId);
    		Group group = Group.findById(oid);
    		User user = User.findById(new ObjectId(session("uid")));
    		
    		if(group!=null && user!=null){
    			Membership membership = Membership.findByUserGroup(user.getId(), group.getId());
    			if(membership!=null){
    				return ok(views.html.group.render(groupId,group.getName(),session("uid"))) ;
    			}
    		}
    		
    		return redirect("/");
    	} else {
    		return ok(views.html.sign.render());
    		
    	}    	
    }
    
    
  
    public Result join(String groupId, String token) throws ServiceException{
    	JoinGroupInviteService service = new JoinGroupInviteService(session("uid"), groupId, token);
		if(service.execute()){			
    		return redirect("/group/"+groupId);
		}
		return badRequest();
    }

    
}
