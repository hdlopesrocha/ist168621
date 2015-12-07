package controllers;

import java.io.File;
import java.util.ArrayList;

import org.bson.types.ObjectId;
import org.json.JSONObject;

import exceptions.ServiceException;
import models.Group;
import models.KeyValueFile;
import models.Membership;
import models.Relation;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Html;
import services.AddGroupMemberService;
import services.CreateGroupService;
import services.CreateRelationService;
import services.CreateUserService;
import services.GetGroupInviteService;
import services.JoinGroupInviteService;
import services.Service;


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
    
  
    public Result reset()  throws ServiceException {
    	Service.reset();
    	JSONObject prop1=	new JSONObject();
    	prop1.put("name", "Henrique Rocha");
    	prop1.put("photo", "/assets/images/user1.jpeg");

    	CreateUserService registerService = new CreateUserService("hdlopesrocha","qazokm", prop1, new ArrayList<KeyValueFile>());
    	User user1 = registerService.execute();
    	CreateGroupService groupService = new CreateGroupService(user1.getId().toString(),"WebRTC");
    	Group group = groupService.execute();
    	JSONObject prop2=	new JSONObject();
    	prop2.put("name", "Nikhil Bhatt");
    	prop2.put("photo", "/assets/images/user2.jpeg");
    	CreateUserService registerService2 = new CreateUserService("nbhatt","qazokm", prop2, new ArrayList<KeyValueFile>());
    	User user2 = registerService2.execute();
    	
    	
    	new CreateRelationService(user1.getId().toString(),user2.getId().toString()).execute();
    	new CreateRelationService(user2.getId().toString(),user1.getId().toString()).execute();
    	
    	
    	AddGroupMemberService joinService = new AddGroupMemberService(user1.getId().toString(),group.getId().toString(),user2.getId().toString());
    	joinService.execute();
    	session().clear();
		return redirect("/");
    }
    
    public Result pubsub() {
    	return ok(views.html.pubsub.render());
    }
    
    public Result group(String groupId) throws ServiceException {
    	if(session("uid")!=null){
    		ObjectId oid = new ObjectId(groupId);
    		Group group = Group.findById(oid);
    		User user = User.findById(new ObjectId(session("uid")));
    		GetGroupInviteService service = new GetGroupInviteService(session("uid"), groupId);
    		String token = service.execute();
    		if(group!=null && user!=null){
    			Membership membership = Membership.findByUserGroup(user.getId(), group.getId());
    			if(membership!=null){
    				return ok(views.html.group.render(groupId,group.getName(),session("uid"),token)) ;
    			}
    		}
    		
    		return redirect("/");
    	} else {
    		return ok(views.html.sign.render());
    		
    	}    	
    }
    
    public Result userProfile(String userId) {
    	if(session("uid")!=null){
    	
    		boolean from = Relation.findByEndpoint(new ObjectId(session("uid")), new ObjectId(userId))!=null;
    		boolean to = Relation.findByEndpoint(new ObjectId(userId),new ObjectId(session("uid")))!=null;
    		
    		return ok(views.html.profile.render(userId,from,to));
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
