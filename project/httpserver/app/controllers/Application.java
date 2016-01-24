package controllers;

import dtos.AttributeDto;
import exceptions.ServiceException;
import models.Group;
import models.Membership;
import models.Relation;
import models.User;
import org.bson.Document;
import org.bson.types.ObjectId;
import play.mvc.Controller;
import play.mvc.Result;
import services.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



/**
 * The Class Application.
 */
public class Application extends Controller {

    /**
     * Index.
     *
     * @return the result
     */
    public Result index() {
        if (session("uid") != null) {
            return ok(views.html.index.render());
        } else {
            return ok(views.html.sign.render());
        }
    }


    /**
     * Stream.
     *
     * @param path the path
     * @return the result
     */
    public Result stream(String path) {
        File file = new File(path);
        Result res = ok(file);
        response().setHeader("Content-type", "video/webm");
        return res;
    }

    /**
     * Reset.
     *
     * @return the result
     * @throws ServiceException the service exception
     */
    public Result reset() throws ServiceException {
        System.out.println("RESET!");
        Service.reset();

        User user1, user2, user3, user4;
        {
            List<AttributeDto> attributes = new ArrayList<AttributeDto>();
            attributes.add(new AttributeDto("email", "hdlopesrocha", AttributeDto.Access.READ, AttributeDto.Visibility.PUBLIC, true, true, false));
            attributes.add(new AttributeDto("name", "Henrique Rocha", AttributeDto.Access.READ, AttributeDto.Visibility.PUBLIC, false, true, false));
            attributes.add(new AttributeDto("photo", "/assets/images/user1.jpeg", AttributeDto.Access.READ, AttributeDto.Visibility.PUBLIC, false, false, false));
            user1 = new RegisterUserService("qazokm", attributes).execute();
        }

        {
            List<AttributeDto> attributes = new ArrayList<AttributeDto>();
            attributes.add(new AttributeDto("email", "nbhatt", AttributeDto.Access.READ, AttributeDto.Visibility.PUBLIC, true, true, false));
            attributes.add(new AttributeDto("name", "Nikhil Bhatt", AttributeDto.Access.READ, AttributeDto.Visibility.PUBLIC, false, true, false));
            attributes.add(new AttributeDto("photo", "/assets/images/user2.jpeg", AttributeDto.Access.READ, AttributeDto.Visibility.PUBLIC, false, false, false));
            user2 = new RegisterUserService("qazokm", attributes).execute();
        }

        {
            List<AttributeDto> attributes = new ArrayList<AttributeDto>();
            attributes.add(new AttributeDto("email", "grocha", AttributeDto.Access.READ, AttributeDto.Visibility.PUBLIC, true, true, false));
            attributes.add(new AttributeDto("name", "Gonçalo Rocha", AttributeDto.Access.READ, AttributeDto.Visibility.PUBLIC, false, true, false));
            attributes.add(new AttributeDto("photo", "/assets/images/user3.jpeg", AttributeDto.Access.READ, AttributeDto.Visibility.PUBLIC, false, false, false));
            user3 = new RegisterUserService("qazokm", attributes).execute();
        }

        {
            List<AttributeDto> attributes = new ArrayList<AttributeDto>();
            attributes.add(new AttributeDto("email", "dvd-r", AttributeDto.Access.READ, AttributeDto.Visibility.PUBLIC, true, true, false));
            attributes.add(new AttributeDto("name", "David Rocha", AttributeDto.Access.READ, AttributeDto.Visibility.PUBLIC, false, true, false));
            attributes.add(new AttributeDto("photo", "/assets/images/user4.jpeg", AttributeDto.Access.READ, AttributeDto.Visibility.PUBLIC, false, false, false));
            user4 = new RegisterUserService("qazokm", attributes).execute();
        }


        new CreateRelationService(user1.getId().toString(), user2.getId().toString()).execute();
        new CreateRelationService(user2.getId().toString(), user1.getId().toString()).execute();

        new CreateRelationService(user1.getId().toString(), user3.getId().toString()).execute();
        new CreateRelationService(user3.getId().toString(), user1.getId().toString()).execute();

//        new CreateRelationService(user1.getId().toString(), user4.getId().toString()).execute();
        new CreateRelationService(user4.getId().toString(), user1.getId().toString()).execute();


        List<AttributeDto> attributes0 = new ArrayList<AttributeDto>();
        attributes0.add(new AttributeDto("name", "WebRTC", AttributeDto.Access.WRITE, AttributeDto.Visibility.PUBLIC, false, true, false));
        Group group0 = new CreateGroupService(user1.getId().toString(), Group.Visibility.PRIVATE, attributes0).execute();


        List<AttributeDto> attributes1 = new ArrayList<AttributeDto>();
        attributes1.add(new AttributeDto("name", "Group1", AttributeDto.Access.WRITE, AttributeDto.Visibility.PUBLIC, false, true, false));
        new CreateGroupService(user1.getId().toString(), Group.Visibility.PUBLIC, attributes1).execute();

        List<AttributeDto> attributes2 = new ArrayList<AttributeDto>();
        attributes2.add(new AttributeDto("name", "Group2", AttributeDto.Access.WRITE, AttributeDto.Visibility.PUBLIC, false, true, false));
        new CreateGroupService(user2.getId().toString(), Group.Visibility.PRIVATE, attributes2).execute();

        List<AttributeDto> attributes3 = new ArrayList<AttributeDto>();
        attributes3.add(new AttributeDto("name", "Group3", AttributeDto.Access.WRITE, AttributeDto.Visibility.PUBLIC, false, true, false));
        new CreateGroupService(user3.getId().toString(), Group.Visibility.PUBLIC, attributes3).execute();


        AddGroupMemberService joinService = new AddGroupMemberService(user1.getId().toString(), group0.getId().toString(), user2.getId().toString());
        joinService.execute();
        session().clear();
        return redirect("/");
    }


    /**
     * Group.
     *
     * @param groupId the group id
     * @return the result
     * @throws ServiceException the service exception
     */
    public Result group(String groupId) throws ServiceException {
        if (session("uid") != null) {
            ObjectId oid = new ObjectId(groupId);
            Group group = Group.findById(oid);
            User user = User.findById(new ObjectId(session("uid")));
            GetGroupInviteService service = new GetGroupInviteService(session("uid"), groupId);
            String token = service.execute();
            if (group != null && user != null) {
                Membership membership = Membership.findByUserGroup(user.getId(), group.getId());
                boolean isPublic = false;

                if (group.getVisibility().equals(Group.Visibility.PUBLIC)) {
                    isPublic = true;
                    if (membership == null) {
                        new Membership(user.getId(), group.getId()).save();
                    }
                }

                if (membership != null || isPublic) {
                    Document service1 = new ListOwnerAttributesService(session("uid"), groupId, Arrays.asList(new String[]{"name"})).execute();
                    String name = service1.getString("name");
                    return ok(views.html.group.render(groupId, name != null ? name : "", session("uid"), token));
                }
            }
            return redirect("/");
        } else {
            return ok(views.html.sign.render());

        }
    }

    /**
     * User profile.
     *
     * @param userId the user id
     * @return the result
     */
    public Result userProfile(String userId) {
        if (session("uid") != null) {
            boolean from = Relation.findByEndpoint(new ObjectId(session("uid")), new ObjectId(userId)) != null;
            boolean to = Relation.findByEndpoint(new ObjectId(userId), new ObjectId(session("uid"))) != null;
            return ok(views.html.profile.render(userId, from, to));
        } else {
            return ok(views.html.sign.render());
        }
    }

    /**
     * Join.
     *
     * @param groupId the group id
     * @param token the token
     * @return the result
     * @throws ServiceException the service exception
     */
    public Result join(String groupId, String token) throws ServiceException {
        JoinGroupInviteService service = new JoinGroupInviteService(session("uid"), groupId, token);
        if (service.execute()) {
            return redirect("/group/" + groupId);
        }
        return badRequest();
    }
}
