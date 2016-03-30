package controllers;

import dtos.AttributeDto;
import dtos.PermissionDto;
import exceptions.ServiceException;
import main.Tools;
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
        if (Tools.userExists(session("uid"))) {
            return ok(views.html.index.render());
        } else {
            return ok(views.html.sign.render());
        }
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

        User user1, user2, user3, user4 ,td1,td2;
        {
            List<AttributeDto> attributes = new ArrayList<AttributeDto>();
            attributes.add(new AttributeDto("email", "test", true, true, false));
            attributes.add(new AttributeDto("name", "test", false, true, false));
            attributes.add(new AttributeDto("photo", "/assets/images/user1.jpeg", false, false, false));
            user1 = new RegisterUserService("test",new ArrayList<PermissionDto>() ,attributes).execute();
        }

        {
            List<AttributeDto> attributes = new ArrayList<AttributeDto>();
            attributes.add(new AttributeDto("email", "rjflp", true, true, false));
            attributes.add(new AttributeDto("name", "Ricardo Pereira", false, true, false));
            attributes.add(new AttributeDto("photo", "/assets/images/ricardo.png", false, false, false));
            user2 = new RegisterUserService("qazokm",new ArrayList<PermissionDto>(), attributes).execute();
        }

        {
            List<AttributeDto> attributes = new ArrayList<AttributeDto>();
            attributes.add(new AttributeDto("email", "grocha", true, true, false));
            attributes.add(new AttributeDto("name", "Gonçalo Rocha", false, true, false));
            attributes.add(new AttributeDto("photo", "/assets/images/goncalo.jpg", false, false, false));
            user3 = new RegisterUserService("qazokm", new ArrayList<PermissionDto>(),attributes).execute();
        }

        {
            List<AttributeDto> attributes = new ArrayList<AttributeDto>();
            attributes.add(new AttributeDto("email", "hdlopesrocha", true, true, false));
            attributes.add(new AttributeDto("name", "Henrique Rocha", false, true, false));
            attributes.add(new AttributeDto("photo", "/assets/images/me.png", false, false, false));
            user4 = new RegisterUserService("qazokm",new ArrayList<PermissionDto>(), attributes).execute();
        }
/*
        {
            List<AttributeDto> attributes = new ArrayList<AttributeDto>();
            attributes.add(new AttributeDto("email", "talkdesk1", true, true, false));
            attributes.add(new AttributeDto("name", "Talkdesk 1", false, true, false));
            attributes.add(new AttributeDto("photo", "/assets/images/td1.png", false, false, false));
            td1 = new RegisterUserService("talkdesk",new ArrayList<PermissionDto>() ,attributes).execute();
        }

        {
            List<AttributeDto> attributes = new ArrayList<AttributeDto>();
            attributes.add(new AttributeDto("email", "talkdesk2", true, true, false));
            attributes.add(new AttributeDto("name", "Talkdesk 2", false, true, false));
            attributes.add(new AttributeDto("photo", "/assets/images/td2.png", false, false, false));
            td2 = new RegisterUserService("talkdesk",new ArrayList<PermissionDto>() ,attributes).execute();
        }

        new CreateRelationService(td1.getId().toString(), td2.getId().toString()).execute();
        new CreateRelationService(td2.getId().toString(), td1.getId().toString()).execute();
*/

        new CreateRelationService(user1.getId().toString(), user2.getId().toString()).execute();
        new CreateRelationService(user2.getId().toString(), user1.getId().toString()).execute();

    //    new CreateRelationService(user1.getId().toString(), user3.getId().toString()).execute();
    //    new CreateRelationService(user3.getId().toString(), user1.getId().toString()).execute();

//        new CreateRelationService(user1.getId().toString(), user4.getId().toString()).execute();
        new CreateRelationService(user4.getId().toString(), user1.getId().toString()).execute();


        List<AttributeDto> attributes3 = new ArrayList<AttributeDto>();
        attributes3.add(new AttributeDto("name", "Task 3", false, true, false));
        Group group3 = new CreateGroupService(user1.getId().toString(), Group.Visibility.PUBLIC, new ArrayList<PermissionDto>(), attributes3).execute();

        List<AttributeDto> attributes4 = new ArrayList<AttributeDto>();
        attributes4.add(new AttributeDto("name", "Task 4", false, true, false));
        Group group4 = new CreateGroupService(user1.getId().toString(), Group.Visibility.PUBLIC, new ArrayList<PermissionDto>(), attributes4).execute();

        List<AttributeDto> attributes5 = new ArrayList<AttributeDto>();
        attributes5.add(new AttributeDto("name", "Task 5", false, true, false));
        Group group5 = new CreateGroupService(user1.getId().toString(), Group.Visibility.PUBLIC, new ArrayList<PermissionDto>(), attributes5).execute();



/*
        List<AttributeDto> attributes1 = new ArrayList<AttributeDto>();
        attributes1.add(new AttributeDto("name", "Group1", false, true, false));
        new CreateGroupService(user1.getId().toString(), Group.Visibility.PUBLIC, new ArrayList<PermissionDto>(), attributes1).execute();

        List<AttributeDto> attributes2 = new ArrayList<AttributeDto>();
        attributes2.add(new AttributeDto("name", "Group2", false, true, false));
        new CreateGroupService(user2.getId().toString(), Group.Visibility.PRIVATE, new ArrayList<PermissionDto>(), attributes2).execute();

        List<AttributeDto> attributes3 = new ArrayList<AttributeDto>();
        attributes3.add(new AttributeDto("name", "Group3", false, true, false));
        new CreateGroupService(user3.getId().toString(), Group.Visibility.PUBLIC, new ArrayList<PermissionDto>(), attributes3).execute();
*/

        new AddGroupMemberService(user1.getId().toString(), group3.getId().toString(), user2.getId().toString()).execute();
        new AddGroupMemberService(user1.getId().toString(), group3.getId().toString(), user4.getId().toString()).execute();

        new AddGroupMemberService(user1.getId().toString(), group4.getId().toString(), user2.getId().toString()).execute();
        new AddGroupMemberService(user1.getId().toString(), group4.getId().toString(), user4.getId().toString()).execute();

        new AddGroupMemberService(user1.getId().toString(), group5.getId().toString(), user2.getId().toString()).execute();
        new AddGroupMemberService(user1.getId().toString(), group5.getId().toString(), user4.getId().toString()).execute();

/*
        new AddGroupMemberService(user1.getId().toString(), group0.getId().toString(), td1.getId().toString()).execute();
        new AddGroupMemberService(user1.getId().toString(), group0.getId().toString(), td2.getId().toString()).execute();
*/

        Date end = new Date();
        Date start = new Date(end.getTime()-835000);


        RecordingInterval interval = new CreateIntervalService(group3.getId().toString(), start).execute();
        interval.setEnd(end);
        interval.save();


        for(int i=0; i < 84 ; ++i) {
            String formatted = String.format("%02d", i);
            URL url = this.getClass().getClassLoader().getResource("video"+formatted+".mp4");
            Date da = new Date(start.getTime()+i*10000);
            RecordingChunk rec = new RecordingChunk(group3.getId(),interval.getId(), da,(long)i);
            rec.setEnd(new Date(da.getTime()+10000));
            rec.setUrl(group3.getId().toString(), "file://"+ url.getFile());
            rec.save();
        }

        new CreateTimeTagService(group3.getId().toString(),start,"OSI Model").execute();

        Integer [] time = new Integer[8];
        String [] captions = new String[8];



        captions[1] = "1 - Application Layer";
        time[1] = 31;

        captions[2] = "2 - Presentation Layer";
        time[2] = 1*60+13;

        captions[3] = "3 - Session Layer";
        time[3] = 1*60+38;

        captions[4] = "4 - Transport Layer";
        time[4] = 2*60+12;

        captions[5] = "5 - Network Layer";
        time[5] = 2*60+39;

        captions[6] = "6 - Data Link Layer";
        time[6] = 3*60+04;

        captions[7] = "7 - Physical Layer";
        time[7] = 3*60+28;

        captions[0] = "<br><ul>";
        time[0] = 12;

        for(int i =1 ; i < 8 ;++i) {
            Date da = new Date(start.getTime()+time[i]*1000);
            captions[0] += "<li><a style='font-size:24px;color:yellow;' onclick='timeline.setHistoric(new Date("+da.getTime()+"))'>"+captions[i]+"</a></li>";
            captions[i] = "<div class='caption'><span>"+captions[i]+"</span></div>";

        }

        captions[0] += "</ul>";

        for(int i =0 ; i < 8 ;++i){
            Date da = new Date(start.getTime()+time[i]*1000);
            Date db = new Date(da.getTime()+15000);

            new CreateHyperContentService(user1.getId().toString(),group3.getId().toString(),da,db,captions[i]).execute();
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
        if (Tools.userExists(session("uid"))) {
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
        if (Tools.userExists(session("uid"))) {
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
