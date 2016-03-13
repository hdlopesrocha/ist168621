package controllers;

import dtos.AttributeDto;
import dtos.PermissionDto;
import exceptions.ServiceException;
import models.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import play.mvc.Controller;
import play.mvc.Result;
import services.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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


    public Result test() {
        return ok(views.html.test.render());
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
            attributes.add(new AttributeDto("email", "hdlopesrocha", true, true, false));
            attributes.add(new AttributeDto("name", "Henrique Rocha", false, true, false));
            attributes.add(new AttributeDto("photo", "/assets/images/user1.jpeg", false, false, false));
            user1 = new RegisterUserService("qazokm",new ArrayList<PermissionDto>() ,attributes).execute();
        }

        {
            List<AttributeDto> attributes = new ArrayList<AttributeDto>();
            attributes.add(new AttributeDto("email", "nbhatt", true, true, false));
            attributes.add(new AttributeDto("name", "Nikhil Bhatt", false, true, false));
            attributes.add(new AttributeDto("photo", "/assets/images/user2.jpeg", false, false, false));
            user2 = new RegisterUserService("qazokm",new ArrayList<PermissionDto>(), attributes).execute();
        }

        {
            List<AttributeDto> attributes = new ArrayList<AttributeDto>();
            attributes.add(new AttributeDto("email", "grocha", true, true, false));
            attributes.add(new AttributeDto("name", "Gonçalo Rocha", false, true, false));
            attributes.add(new AttributeDto("photo", "/assets/images/user3.jpeg", false, false, false));
            user3 = new RegisterUserService("qazokm", new ArrayList<PermissionDto>(),attributes).execute();
        }

        {
            List<AttributeDto> attributes = new ArrayList<AttributeDto>();
            attributes.add(new AttributeDto("email", "dvd-r", true, true, false));
            attributes.add(new AttributeDto("name", "David Rocha", false, true, false));
            attributes.add(new AttributeDto("photo", "/assets/images/user4.jpeg", false, false, false));
            user4 = new RegisterUserService("qazokm",new ArrayList<PermissionDto>(), attributes).execute();
        }


        new CreateRelationService(user1.getId().toString(), user2.getId().toString()).execute();
        new CreateRelationService(user2.getId().toString(), user1.getId().toString()).execute();

        new CreateRelationService(user1.getId().toString(), user3.getId().toString()).execute();
        new CreateRelationService(user3.getId().toString(), user1.getId().toString()).execute();

//        new CreateRelationService(user1.getId().toString(), user4.getId().toString()).execute();
        new CreateRelationService(user4.getId().toString(), user1.getId().toString()).execute();


        List<AttributeDto> attributes0 = new ArrayList<AttributeDto>();
        attributes0.add(new AttributeDto("name", "WebRTC", false, true, false));
        Group group0 = new CreateGroupService(user1.getId().toString(), Group.Visibility.PRIVATE, new ArrayList<PermissionDto>(), attributes0).execute();


        List<AttributeDto> attributes1 = new ArrayList<AttributeDto>();
        attributes1.add(new AttributeDto("name", "Group1", false, true, false));
        new CreateGroupService(user1.getId().toString(), Group.Visibility.PUBLIC, new ArrayList<PermissionDto>(), attributes1).execute();

        List<AttributeDto> attributes2 = new ArrayList<AttributeDto>();
        attributes2.add(new AttributeDto("name", "Group2", false, true, false));
        new CreateGroupService(user2.getId().toString(), Group.Visibility.PRIVATE, new ArrayList<PermissionDto>(), attributes2).execute();

        List<AttributeDto> attributes3 = new ArrayList<AttributeDto>();
        attributes3.add(new AttributeDto("name", "Group3", false, true, false));
        new CreateGroupService(user3.getId().toString(), Group.Visibility.PUBLIC, new ArrayList<PermissionDto>(), attributes3).execute();


        AddGroupMemberService joinService = new AddGroupMemberService(user1.getId().toString(), group0.getId().toString(), user2.getId().toString());
        joinService.execute();

        Date end = new Date();
        Date start = new Date(end.getTime()-835000);


        RecordingInterval interval = new CreateIntervalService(group0.getId().toString(), start).execute();
        interval.setEnd(end);
        interval.save();


        for(int i=0; i < 84 ; ++i) {
            String formatted = String.format("%02d", i);
            URL url = this.getClass().getClassLoader().getResource("video"+formatted+".mp4");
            Date da = new Date(start.getTime()+i*10000);
            RecordingChunk rec = new RecordingChunk(group0.getId(), da);
            rec.setEnd(new Date(da.getTime()+10000));
            rec.setUrl(group0.getId().toString(), url.getFile());
            rec.save();
        }

        new CreateTimeTagService(group0.getId().toString(),start,"OSI Model").execute();

        Integer [] time = new Integer[8];
        String [] captions = new String[8];



        captions[1] = "Application Layer";
        time[1] = 31;

        captions[2] = "Presentation Layer";
        time[2] = 1*60+13;

        captions[3] = "Session Layer";
        time[3] = 1*60+38;

        captions[4] = "Transport Layer";
        time[4] = 2*60+12;

        captions[5] = "Network Layer";
        time[5] = 2*60+39;

        captions[6] = "Data Link Layer";
        time[6] = 3*60+04;

        captions[7] = "Physical Layer";
        time[7] = 3*60+28;

        captions[0] = "<br><ul>";
        time[0] = 12;

        for(int i =1 ; i < 8 ;++i) {
            Date da = new Date(start.getTime()+time[i]*1000);
            captions[0] += "<li> <a onclick='timeline.setHistoric(new Date("+da.getTime()+"))'>"+captions[i]+"</a></li>";
            captions[i] = "<div class='caption'><span>"+captions[i]+"</span></div>";

        }

        captions[0] += "</ul>";

        for(int i =0 ; i < 8 ;++i){
            Date da = new Date(start.getTime()+time[i]*1000);
            Date db = new Date(da.getTime()+5000);

            new CreateHyperContentService(user1.getId().toString(),group0.getId().toString(),da,db,captions[i]).execute();
        }





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
                GroupMembership membership = GroupMembership.findByUserGroup(user.getId(), group.getId());
                boolean isPublic = false;

                if (group.getVisibility().equals(Group.Visibility.PUBLIC)) {
                    isPublic = true;
                    if (membership == null) {
                        new GroupMembership(user.getId(), group.getId()).save();
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
