package controllers;

import dtos.AttributeDto;
import exceptions.ServiceException;
import exceptions.UnauthorizedException;
import models.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import services.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Rest extends Controller {

    private static final ObjectId MONGODB_SDP_LOCK = new ObjectId();

    public Result acceptRelation(String uid) {
        if (session("uid") != null) {
            CreateRelationService service = new CreateRelationService(session("uid"), uid);
            try {
                service.execute();
            } catch (ServiceException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return ok("OK");
        }
        return forbidden();
    }

    public Result searchOnGroup(String groupId) {
        String query = request().getQueryString("query");

        JSONArray array = new JSONArray();
        try {
            SearchTagsService service = new SearchTagsService(session("uid"), groupId, query);
            List<TimeTag> tags = service.execute();

            for (TimeTag tag : tags) {
                JSONObject msg = tag.toJson();
                msg.put("type", "tag");
                array.put(msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        return ok(array.toString());
    }

    public Result addGroupMember(String groupId, String memberId) {
        if (session("uid") != null) {
            System.out.println("ADDMEMBER: " + groupId + " | " + memberId);
            AddGroupMemberService service = new AddGroupMemberService(session("uid"), groupId, memberId);
            try {
                service.execute();
            } catch (ServiceException e) {
                e.printStackTrace();
            }
            return ok("OK");
        }
        return forbidden();
    }

    public Result changePassword() {
        Map<String, String[]> info = request().body().asFormUrlEncoded();
        String oldPassword = info.get("oldPassword")[0];
        String password1 = info.get("newPassword")[0];

        ChangeUserPasswordService service = new ChangeUserPasswordService(session("uid"), oldPassword, password1);
        try {
            service.execute();
            return ok();

        } catch (ServiceException e) {

        }
        return badRequest();
    }

    public Result createGroup() {
        if (session("uid") != null) {
            Map<String, String[]> qs = request().queryString();
            String name = qs.get("n")[0];
            CreateGroupService service = new CreateGroupService(session("uid"), name);
            try {
                service.execute();
            } catch (ServiceException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return ok("OK");
        }
        return forbidden();
    }

    public Result denyRelation(String email) {
        if (session("uid") != null) {
            DenyRelationService service = new DenyRelationService(session("uid"), email);
            try {
                service.execute();
            } catch (ServiceException e) {
                e.printStackTrace();
            }
            return ok("OK");
        }
        return forbidden();
    }

    void setPhotoHeaders(String fileName) {
        String[] tokens = fileName.split("/");

        response().setHeader("Content-disposition", "attachment;filename=" + tokens[tokens.length - 1]);
        String ext = fileName.substring(fileName.lastIndexOf('.') + 1);
        if ("jpg".equals(ext) || "jpeg".equals(ext)) {
            response().setHeader(CONTENT_TYPE, "image/png");
        } else if ("png".equals(ext)) {
            response().setHeader(CONTENT_TYPE, "image/jpg");
        } else if ("zip".equals(ext)) {
            response().setHeader(CONTENT_TYPE, "application/zip");
        } else {
            response().setHeader(CONTENT_TYPE, "application/octet-stream");
        }
    }


    public Result getFile(String fileName) {
        fileName = fileName.replace("%20", " ");

        setPhotoHeaders(fileName);

        try {

            DownloadFileService service = new DownloadFileService(fileName);
            byte[] bytes = service.execute();
            if (bytes != null) {
                response().setHeader(CONTENT_LENGTH, bytes.length + "");
                return ok(bytes);
            }
        } catch (ServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return notFound();
    }

    public Result getPhoto(String uid) {
        if (uid != null && uid.length() > 0) {

            try {
                Attribute attr = new GetOwnerAttributeService(uid,"photo").execute();

                if (attr != null && attr.getValue()!=null) {
                    String fileName = attr.getValue().toString();
                    System.out.println("GET " + fileName);
                    fileName = fileName.replace("%20", " ");

                    setPhotoHeaders(fileName);
                    DownloadFileService service = new DownloadFileService(fileName);
                    byte[] bytes = service.execute();
                    if (bytes != null) {
                        response().setHeader(CONTENT_LENGTH, bytes.length + "");
                        return ok(bytes);
                    }
                }
            } catch (ServiceException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return redirect("/assets/images/default.png");

    }

    public Result getUser(String userId) throws ServiceException {
        List<Attribute> attributes = new ListOwnerAttributesService(session("uid"),userId).execute();
        JSONObject result = new JSONObject();
        for(Attribute attribute : attributes){
            result.put(attribute.getKey(),attribute.getValue());
        }
        return ok(result.toString());
    }


    public Result listGroupMembersProperties(String groupId) {
        JSONObject obj = new JSONObject();
        List<Membership> members = Membership.listByGroup(new ObjectId(groupId));
        for (Membership member : members) {
            obj.put(member.getUserId().toString(), new JSONObject(member.getProperties().toJson()));
        }
        return ok(obj.toString());
    }

    public Result listGroups() {
        if (session("uid") != null) {
            ListGroupsService service = new ListGroupsService(session("uid"));
            try {
                List<Group> ans = service.execute();
                JSONArray array = new JSONArray();
                for (Group g : ans) {
                    JSONObject obj = new JSONObject();
                    obj.put("id", g.getId());
                    obj.put("name", g.getName());
                    array.put(obj);
                }
                return ok(array.toString());
            } catch (ServiceException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return ok("[]");
        }
        return forbidden();
    }

    public Result listRelationRequests() {
        if (session("uid") != null) {
            ListRelationRequestsService service = new ListRelationRequestsService(session("uid"));
            try {
                JSONArray array = new JSONArray();
                List<User> res = service.execute();
                for (User user : res) {
                    JSONObject obj = new JSONObject();
                    obj.put("uid", user.getId().toString());
                    array.put(obj);
                }
                return ok(array.toString());

            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }
        return forbidden();
    }

    public Result listRelations() {
        if (session("uid") != null) {
            ListRelationsService service = new ListRelationsService(session("uid"));
            try {
                JSONArray array = new JSONArray();
                List<Relation> relations = service.execute();
                for (Relation relation : relations) {
                    List<Attribute> attributes = new ListOwnerAttributesService(session("uid"), relation.getTo().toString()).execute();
                    JSONObject result = new JSONObject();
                    result.put("id",relation.getTo().toString());
                    for(Attribute attribute : attributes){
                        result.put(attribute.getKey(),attribute.getValue());
                    }
                    array.put(result);
                }
                return ok(array.toString());
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }
        return forbidden();
    }

    public Result listUsers() {
        try {
            return ok(new ListUsersService(session("uid")).execute());
        } catch (ServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return unauthorized();
    }

    public Result login() {
        try {

            Map<String, String[]> map = request().body().asFormUrlEncoded();
            if (map.containsKey("email") && map.containsKey("password")) {
                String email = map.get("email")[0];

                String password = map.get("password")[0];


                String identity = new GetOwnerByAttributeService("email", email).execute();
                AuthenticateUserService service = new AuthenticateUserService(identity, password);
                User user = service.execute();


                if (user != null) {
                    session("uid", user.getId().toString());
                    return ok(user.getToken());
                } else {
                    return unauthorized();
                }

            }
        } catch (UnauthorizedException e) {
            return unauthorized();
        } catch (ServiceException e) {
            return badRequest();
        } catch (JSONException e) {
            e.printStackTrace();
            return badRequest();
        }
        return badRequest();
    }

    public Result logout() {
        session().clear();
        return ok();
    }

    public Result postIceCandidate(String groupId, String token) {
        if (session("uid") != null) {
            synchronized (MONGODB_SDP_LOCK) {
                String data = request().body().asText();
                try {
                    PostIceCandidateService service = new PostIceCandidateService(session("uid"), groupId, token, data);
                    service.execute();
                    return ok("OK");
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
            }
            return badRequest();
        }
        return forbidden();
    }

    public Result postSdp(String groupId) {
        if (session("uid") != null) {
            synchronized (MONGODB_SDP_LOCK) {
                String data = request().body().asText();
                try {
                    PostSdpService service = new PostSdpService(session("uid"), groupId, data);
                    service.execute();
                    return ok("OK");
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
            }
            return badRequest();
        }
        return forbidden();
    }

    public Result publish(String groupId) throws ServiceException {
        PublishService service2 = new PublishService(groupId);
        service2.execute();
        return ok("OK");

    }

    public Result register() throws ServiceException {

        MultipartFormData multipart = request().body().asMultipartFormData();
        Map<String, String[]> form = multipart.asFormUrlEncoded();

        String email = form.get("email")[0];

        String password = form.get("password")[0];
        List<AttributeDto> attributes = new ArrayList<AttributeDto>();

        for (Entry<String, String[]> s : form.entrySet()) {
            if (!s.getKey().equals("password") && !s.getKey().equals("password2")) {
                String[] value = s.getValue();
                Object obj = null;

                if (value.length == 1) {
                    obj = value[0];
                } else if (value.length > 1) {
                    JSONArray array = new JSONArray();
                    for (int i = 0; i < value.length; ++i) {
                        array.put(value[i]);
                    }
                    obj = array;
                }

                if(obj!=null){
                    boolean searchable = false;
                    boolean identifiable = false;

                    if(s.getKey().equals("name")){
                        searchable = true;
                    }
                    else if(s.getKey().equals("email")){
                        searchable = identifiable = true;
                    }

                    attributes.add(new AttributeDto(s.getKey(), obj,identifiable,searchable));


                }

            }
        }

        for (FilePart fp : multipart.getFiles()) {
            KeyValueFile kvf = new KeyValueFile(fp.getKey(), fp.getFilename(), fp.getFile());
            UploadFileService service = new UploadFileService(kvf);
            try {
                attributes.add(new AttributeDto(kvf.getKey(), service.execute(),false,false));
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }

        String existingUser = new GetOwnerByAttributeService("email",email).execute();
        if(existingUser!=null){
            return Rest.status(409);
        }

        CreateUserService service = new CreateUserService(password, attributes);

        User ret = service.execute();
        return ok(ret.getToken());

    }

    public Result removeGroupMember(String groupId, String memberId) {
        if (session("uid") != null) {
            RemoveGroupMemberService service = new RemoveGroupMemberService(session("uid"), groupId, memberId);
            try {
                service.execute();
            } catch (ServiceException e) {
                e.printStackTrace();
            }
            return ok("OK");
        }
        return forbidden();
    }

    public Result search() {
        String query = request().queryString().get("s")[0];

        try {
            User me = User.findById(new ObjectId(session("uid")));
            JSONArray array = new JSONArray();
            // Search User
            {
                ListOwnersService service = new ListOwnersService(session("uid"), null,null);
                service.setSearch(query);
                List<ObjectId> res = service.execute();


                for (ObjectId userId : res) {
                    if (!userId.equals(me.getId())) {
                        List<Attribute> attributes = new ListOwnerAttributesService(session("uid"),userId.toString()).execute();
                        JSONObject result = new JSONObject();
                        for(Attribute attribute : attributes){
                            result.put(attribute.getKey(),attribute.getValue());
                        }



                        result.put("type", "user");
                        Relation rel1 = Relation.findByEndpoint(me.getId(), userId);
                        Relation rel2 = Relation.findByEndpoint(userId, me.getId());


                        if (rel1 != null) {
                            result.put("state", rel2 != null);
                        }
                        array.put(result);
                    }
                }
            }
            // Search Group
            {
                List<Group> res = new SearchGroupService(session("uid"), query).execute();
                for (Group u : res) {
                    if (!u.getId().equals(me.getId())) {
                        JSONObject obj = new JSONObject();
                        obj.put("name", u.getName());
                        obj.put("id", u.getId().toString());
                        obj.put("type", "group");
                        array.put(obj);
                    }
                }
            }

            return ok(array.toString());

        } catch (ServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return ok("[]");
    }

    public Result searchGroupCandidates(String groupId) {
        if (session("uid") != null) {
            String query = request().queryString().get("s")[0];

            System.out.println("SEARCH: " + groupId + " | " + query);

            SearchGroupCandidatesService service = new SearchGroupCandidatesService(session("uid"), groupId, query);
            JSONArray array = new JSONArray();
            try {
                array = service.execute();
            } catch (ServiceException e) {
                e.printStackTrace();
            }
            return ok(array.toString());
        }
        return forbidden();
    }

    public Result subscribe(String groupId, Long ts) throws ServiceException {
        SubscribeService service = new SubscribeService(groupId, ts);

        Document doc = service.execute();
        if (doc == null || !doc.containsKey("ts")) {
            return ok("");
        }
        return ok(new JSONObject().put("ts", doc.getLong("ts")).toString());
    }

    public Result updateUser() {
        try {
            MultipartFormData multipart = request().body().asMultipartFormData();
            Map<String, String[]> form = multipart.asFormUrlEncoded();
            JSONObject info = new JSONObject(form.get("json")[0]);
            List<KeyValueFile> files = new ArrayList<KeyValueFile>();
            for (FilePart fp : multipart.getFiles()) {
                KeyValueFile kvf = new KeyValueFile(fp.getKey(), fp.getFilename(), fp.getFile());
                files.add(kvf);
            }


            List<AttributeDto> attributes = new ArrayList<AttributeDto>();

            UpdateUserService service = new UpdateUserService(session("uid"), attributes);

            service.execute();
            return ok();

        } catch (UnauthorizedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return unauthorized();
        } catch (ServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        return badRequest();
    }

    public Result createGroupInvite(String groupId) throws ServiceException {
        CreateGroupInviteService service = new CreateGroupInviteService(session("uid"), groupId);
        return ok(service.execute());
    }

    public Result getGroupInvite(String groupId) throws ServiceException {
        GetGroupInviteService service = new GetGroupInviteService(session("uid"), groupId);
        String token = service.execute();
        if (token != null) {
            return ok(token);
        }
        return badRequest();
    }

    public Result deleteGroupInvite(String groupId) throws ServiceException {
        DeleteGroupInviteService service = new DeleteGroupInviteService(session("uid"), groupId);
        service.execute();
        return ok();
    }

    public Result joinGroupInvite(String groupId, String token) throws ServiceException {
        JoinGroupInviteService service = new JoinGroupInviteService(session("uid"), groupId, token);
        if (service.execute()) {
            return ok(groupId);
        }
        return badRequest();
    }

}
