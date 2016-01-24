package controllers;

import dtos.AttributeDto;
import dtos.KeyValue;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * The Class Rest.
 */
public class Rest extends Controller {

    /** The Constant MONGODB_SDP_LOCK. */
    private static final ObjectId MONGODB_SDP_LOCK = new ObjectId();

    /**
     * Accept relation.
     *
     * @param uid the uid
     * @return the result
     */
    public Result acceptRelation(String uid) {
        if (session("uid") != null) {
            CreateRelationService service = new CreateRelationService(session("uid"), uid);
            try {
                service.execute();
            } catch (ServiceException e) {
                e.printStackTrace();
            }
            return ok("OK");
        }
        return forbidden();
    }

    /**
     * Search on group.
     *
     * @param groupId the group id
     * @return the result
     */
    public Result searchOnGroup(String groupId) {
        String query = request().getQueryString("query");

        JSONArray array = new JSONArray();
        try {
            SearchTimeTagsService service = new SearchTimeTagsService(session("uid"), groupId, query);
            List<TimeTag> tags = service.execute();

            for (TimeTag tag : tags) {
                JSONObject msg = tag.toJson();
                msg.put("type", "tag");
                array.put(msg);
            }

            SearchHyperContentService service2 = new SearchHyperContentService(session("uid"), groupId, query);
            List<HyperContent> contents = service2.execute();

            Pattern pattern = Pattern.compile("<(\\w+)>.*?</\\1>");

            for (HyperContent content : contents) {
                JSONObject msg = content.toJson();
                msg.put("type", "html");

                String text = content.getContent();
                Matcher m = pattern.matcher(text);


                String newText = "";
                while (m.find()) {
                    newText += m.group();
                }


                msg.put("content", newText);


                array.put(msg);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        return ok(array.toString());
    }

    /**
     * Change password.
     *
     * @return the result
     */
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

    /**
     * Creates the group.
     *
     * @return the result
     */
    public Result createGroup() {
        if (session("uid") != null) {
            Map<String, String[]> qs = request().queryString();
            String name = qs.get("n")[0];

            Group.Visibility visibility = Group.Visibility.valueOf(qs.get("v")[0]);

            List<AttributeDto> attributes = new ArrayList<AttributeDto>();
            attributes.add(new AttributeDto("name", name, AttributeDto.Access.WRITE, AttributeDto.Visibility.PUBLIC, false, true, false));

            CreateGroupService service = new CreateGroupService(session("uid"), visibility, attributes);
            try {
                service.execute();
            } catch (ServiceException e) {
                e.printStackTrace();
            }
            return ok("OK");
        }
        return forbidden();
    }

    /**
     * Deny relation.
     *
     * @param email the email
     * @return the result
     */
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

    /**
     * Sets the photo headers.
     *
     * @param fileName the new photo headers
     */
    private void setPhotoHeaders(String fileName) {
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


    /**
     * Gets the file.
     *
     * @param fileName the file name
     * @return the file
     */
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
            e.printStackTrace();
        }

        return notFound();
    }

    /**
     * Gets the photo.
     *
     * @param uid the uid
     * @return the photo
     */
    public Result getPhoto(String uid) {
        if (uid != null && uid.length() > 0) {
            try {
                Document document = new ListOwnerAttributesService(session("uid"), uid, Arrays.asList(new String[]{"photo"})).execute();
                String photo = document.getString("photo");
                if (photo != null) {
                    System.out.println("GET " + photo);
                    photo = photo.replace("%20", " ");
                    return redirect(photo);
                }
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }
        return redirect("/assets/images/default.png");

    }

    /**
     * Gets the user.
     *
     * @param userId the user id
     * @return the user
     * @throws ServiceException the service exception
     */
    public Result getUser(String userId) throws ServiceException {
        Document attributes = new ListOwnerAttributesService(session("uid"), userId, null).execute();
        JSONObject result = new JSONObject(attributes.toJson());

        return ok(result.toString());
    }


    /**
     * List group members properties.
     *
     * @param groupId the group id
     * @return the result
     */
    public Result listGroupMembersProperties(String groupId) {
        JSONObject obj = new JSONObject();
        List<Membership> members = Membership.listByGroup(new ObjectId(groupId));
        for (Membership member : members) {
            obj.put(member.getUserId().toString(), new JSONObject(member.getProperties().toJson()));
        }
        return ok(obj.toString());
    }

    /**
     * List groups.
     *
     * @return the result
     */
    public Result listGroups() {
        if (session("uid") != null) {
            ListGroupsService service = new ListGroupsService(session("uid"));
            try {
                List<Group> ans = service.execute();
                JSONArray array = new JSONArray();
                for (Group g : ans) {
                    Document attributes = new ListOwnerAttributesService(session("uid"), g.getId().toString(), null).execute();
                    JSONObject obj = new JSONObject(attributes.toJson());
                    obj.put("id", g.getId());
                    obj.put("visibility", g.getVisibility());
                    array.put(obj);
                }
                return ok(array.toString());
            } catch (ServiceException e) {
                e.printStackTrace();
            }
            return ok("[]");
        }
        return forbidden();
    }

    /**
     * List relation requests.
     *
     * @return the result
     */
    public Result listRelationRequests() {
        if (session("uid") != null) {
            ListRelationRequestsService service = new ListRelationRequestsService(session("uid"));
            try {
                JSONArray array = new JSONArray();
                List<User> res = service.execute();
                for (User user : res) {

                    Document attributes = new ListOwnerAttributesService(session("uid"), user.getId().toString(), null).execute();
                    JSONObject obj = new JSONObject(attributes.toJson());
                    obj.put("id", user.getId().toString());
                    array.put(obj);
                }
                return ok(array.toString());

            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }
        return forbidden();
    }

    /**
     * List relations.
     *
     * @return the result
     */
    public Result listRelations() {
        if (session("uid") != null) {
            ListRelationsService service = new ListRelationsService(session("uid"));
            try {
                JSONArray array = new JSONArray();
                List<Relation> relations = service.execute();
                for (Relation relation : relations) {
                    Document attributes = new ListOwnerAttributesService(session("uid"), relation.getTo().toString(), null).execute();
                    JSONObject result = new JSONObject(attributes.toJson());
                    result.put("id", relation.getTo().toString());
                    array.put(result);
                }
                return ok(array.toString());
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }
        return forbidden();
    }

    /**
     * List users.
     *
     * @return the result
     */
    public Result listUsers() {
        try {
            return ok(new ListUsersService(session("uid")).execute());
        } catch (ServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return unauthorized();
    }

    /**
     * Login.
     *
     * @return the result
     */
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

    /**
     * Logout.
     *
     * @return the result
     */
    public Result logout() {
        session().clear();
        return ok();
    }


    /**
     * Register.
     *
     * @return the result
     * @throws ServiceException the service exception
     */
    public Result register() throws ServiceException {

        MultipartFormData multipart = request().body().asMultipartFormData();
        Map<String, String[]> form = multipart.asFormUrlEncoded();

        String email = form.get("email")[0];

        String password = form.get("password")[0];
        List<AttributeDto> attributes = new ArrayList<AttributeDto>();
        attributes.add(new AttributeDto("type", User.class.getName(), AttributeDto.Access.WRITE, AttributeDto.Visibility.PUBLIC, false, false, true));


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

                if (obj != null) {
                    boolean searchable = false;
                    boolean identifiable = false;

                    if (s.getKey().equals("name")) {
                        searchable = true;
                    } else if (s.getKey().equals("email")) {
                        searchable = identifiable = true;
                    }
                    attributes.add(new AttributeDto(s.getKey(), obj, AttributeDto.Access.READ, AttributeDto.Visibility.PUBLIC, identifiable, searchable, false));
                }
            }
        }

        for (FilePart fp : multipart.getFiles()) {
            KeyValueFile kvf = new KeyValueFile(fp.getKey(), fp.getFilename(), fp.getFile());
            UploadFileService service = new UploadFileService(kvf);
            try {
                attributes.add(new AttributeDto(kvf.getKey(), service.execute(), AttributeDto.Access.READ, AttributeDto.Visibility.PUBLIC, false, false, false));
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }

        String existingUser = new GetOwnerByAttributeService("email", email).execute();
        if (existingUser != null) {
            return Rest.status(409);
        }

        User ret = new RegisterUserService(password, attributes).execute();
        return ok(ret.getToken());

    }


    /**
     * Search.
     *
     * @return the result
     */
    public Result search() {
        String query = request().queryString().get("s")[0];

        try {
            ObjectId me = new ObjectId(session("uid"));
            JSONArray array = new JSONArray();
            // Search User
            {
                List<List<KeyValue<String>>> filters = new ArrayList<>();


                SearchDataService service = new SearchDataService(session("uid"), null, null, query.toLowerCase(), filters);
                List<String> users = service.execute();

                for (String user : users) {
                    ObjectId userId = new ObjectId(user);
                    if (!userId.equals(me)) {
                        Document attributes = new ListOwnerAttributesService(session("uid"), user, null).execute();
                        JSONObject result = new JSONObject(attributes.toJson());
                        result.put("id", userId);
                        array.put(result);
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

    /**
     * Search group candidates.
     *
     * @param groupId the group id
     * @return the result
     */
    public Result searchGroupCandidates(String groupId) {
        if (session("uid") != null) {
            String query = request().queryString().get("s")[0];


            SearchGroupCandidatesService service = new SearchGroupCandidatesService(session("uid"), groupId, query);
            JSONArray array = new JSONArray();
            try {
                array = service.execute();
            } catch (ServiceException e) {
                e.printStackTrace();
            }

            System.out.println("SEARCH: " + groupId + " | " + query + " | " + array.toString());

            return ok(array.toString());
        }
        return forbidden();
    }


    /**
     * Update user.
     *
     * @return the result
     */
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

    /**
     * Creates the group invite.
     *
     * @param groupId the group id
     * @return the result
     * @throws ServiceException the service exception
     */
    public Result createGroupInvite(String groupId) throws ServiceException {
        CreateGroupInviteService service = new CreateGroupInviteService(session("uid"), groupId);
        return ok(service.execute());
    }

    /**
     * Gets the group invite.
     *
     * @param groupId the group id
     * @return the group invite
     * @throws ServiceException the service exception
     */
    public Result getGroupInvite(String groupId) throws ServiceException {
        GetGroupInviteService service = new GetGroupInviteService(session("uid"), groupId);
        String token = service.execute();
        if (token != null) {
            return ok(token);
        }
        return badRequest();
    }

    /**
     * Delete group invite.
     *
     * @param groupId the group id
     * @return the result
     * @throws ServiceException the service exception
     */
    public Result deleteGroupInvite(String groupId) throws ServiceException {
        DeleteGroupInviteService service = new DeleteGroupInviteService(session("uid"), groupId);
        service.execute();
        return ok();
    }

    /**
     * Join group invite.
     *
     * @param groupId the group id
     * @param token the token
     * @return the result
     * @throws ServiceException the service exception
     */
    public Result joinGroupInvite(String groupId, String token) throws ServiceException {
        JoinGroupInviteService service = new JoinGroupInviteService(session("uid"), groupId, token);
        if (service.execute()) {
            return ok(groupId);
        }
        return badRequest();
    }

}
