package controllers;

import dtos.AttributeDto;
import exceptions.ServiceException;
import models.*;
import org.bson.types.ObjectId;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Html;
import services.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Application extends Controller {

    public Result index() {
        if (session("uid") != null) {
            return ok(views.html.index.render());
        } else {
            return ok(views.html.sign.render());
        }
    }

    public Result template() {
        return ok(views.html.template.render(0, new Html("")));
    }

    public Result stream(String path) {
        File file = new File(path);
        Result res = ok(file);
        response().setHeader("Content-type", "video/webm");
        return res;
    }

    public Result reset() throws ServiceException {
        System.out.println("RESET!");
        Service.reset();

        User user1,user2,user3,user4;
        {
            List<AttributeDto> attributes = new ArrayList<AttributeDto>();
            attributes.add(new AttributeDto("email", "hdlopesrocha", AttributeDto.Access.READ, AttributeDto.Visibility.PUBLIC, true,true,false));
            attributes.add(new AttributeDto("name", "Henrique Rocha", AttributeDto.Access.READ, AttributeDto.Visibility.PUBLIC, false,true,false));
            attributes.add(new AttributeDto("photo", "/assets/images/user1.jpeg", AttributeDto.Access.READ, AttributeDto.Visibility.PUBLIC, false,false,false));
            user1 = new CreateUserService("qazokm",attributes).execute();
        }

        {
            List<AttributeDto> attributes = new ArrayList<AttributeDto>();
            attributes.add(new AttributeDto("email", "nbhatt", AttributeDto.Access.READ, AttributeDto.Visibility.PUBLIC, true,true,false));
            attributes.add(new AttributeDto("name", "Nikhil Bhatt", AttributeDto.Access.READ, AttributeDto.Visibility.PUBLIC, false,true,false));
            attributes.add(new AttributeDto("photo", "/assets/images/user2.jpeg", AttributeDto.Access.READ, AttributeDto.Visibility.PUBLIC, false,false,false));
            user2 = new CreateUserService("qazokm",attributes).execute();
        }

        {
            List<AttributeDto> attributes = new ArrayList<AttributeDto>();
            attributes.add(new AttributeDto("email", "grocha", AttributeDto.Access.READ, AttributeDto.Visibility.PUBLIC, true,true,false));
            attributes.add(new AttributeDto("name", "Gonçalo Rocha", AttributeDto.Access.READ, AttributeDto.Visibility.PUBLIC, false,true,false));
            attributes.add(new AttributeDto("photo", "/assets/images/user3.jpeg", AttributeDto.Access.READ, AttributeDto.Visibility.PUBLIC, false,false,false));
            user3 = new CreateUserService("qazokm",attributes).execute();
        }

        {
            List<AttributeDto> attributes = new ArrayList<AttributeDto>();
            attributes.add(new AttributeDto("email", "dvd-r", AttributeDto.Access.READ, AttributeDto.Visibility.PUBLIC, true,true,false));
            attributes.add(new AttributeDto("name", "David Rocha", AttributeDto.Access.READ, AttributeDto.Visibility.PUBLIC, false,true,false));
            attributes.add(new AttributeDto("photo", "/assets/images/user4.jpeg", AttributeDto.Access.READ, AttributeDto.Visibility.PUBLIC, false,false,false));
            user4 = new CreateUserService("qazokm",attributes).execute();
        }

        System.out.println("1!");

        new CreateRelationService(user1.getId().toString(), user2.getId().toString()).execute();
        new CreateRelationService(user2.getId().toString(), user1.getId().toString()).execute();

        new CreateRelationService(user1.getId().toString(), user3.getId().toString()).execute();
        new CreateRelationService(user3.getId().toString(), user1.getId().toString()).execute();

        new CreateRelationService(user1.getId().toString(), user4.getId().toString()).execute();
        new CreateRelationService(user4.getId().toString(), user1.getId().toString()).execute();

        System.out.println("2!");

        List<AttributeDto> attributes = new ArrayList<AttributeDto>();
        attributes.add(new AttributeDto("name","WebRTC", AttributeDto.Access.WRITE, AttributeDto.Visibility.PUBLIC,false,true,false));

        System.out.println("3!");

        Group group =  new CreateGroupService(user1.getId().toString(), attributes).execute();
        System.out.println("4!");

        AddGroupMemberService joinService = new AddGroupMemberService(user1.getId().toString(), group.getId().toString(), user2.getId().toString());
        joinService.execute();
        session().clear();
        return redirect("/");
    }

    public Result pubsub() {
        return ok(views.html.pubsub.render());
    }

    public Result group(String groupId) throws ServiceException {
        if (session("uid") != null) {
            ObjectId oid = new ObjectId(groupId);
            Group group = Group.findById(oid);
            User user = User.findById(new ObjectId(session("uid")));
            GetGroupInviteService service = new GetGroupInviteService(session("uid"), groupId);
            String token = service.execute();
            if (group != null && user != null) {
                Membership membership = Membership.findByUserGroup(user.getId(), group.getId());
                if (membership != null) {
                    Attribute attrName = new GetOwnerAttributeService(groupId,"name").execute();

                    return ok(views.html.group.render(groupId, attrName!=null ? attrName.getValue().toString():"", session("uid"), token));
                }
            }
            return redirect("/");
        } else {
            return ok(views.html.sign.render());

        }
    }

    public Result userProfile(String userId) {
        if (session("uid") != null) {
            boolean from = Relation.findByEndpoint(new ObjectId(session("uid")), new ObjectId(userId)) != null;
            boolean to = Relation.findByEndpoint(new ObjectId(userId), new ObjectId(session("uid"))) != null;
            return ok(views.html.profile.render(userId, from, to));
        } else {
            return ok(views.html.sign.render());
        }
    }

    public Result join(String groupId, String token) throws ServiceException {
        JoinGroupInviteService service = new JoinGroupInviteService(session("uid"), groupId, token);
        if (service.execute()) {
            return redirect("/group/" + groupId);
        }
        return badRequest();
    }
}
