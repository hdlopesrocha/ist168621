package controllers;

import dtos.AttributeDto;
import dtos.KeyValue;
import dtos.PermissionDto;
import exceptions.ServiceException;
import exceptions.UnauthorizedException;
import main.Tools;
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

import java.util.*;
import java.util.Map.Entry;


/**
 * The Class Rest.
 */
public class Rest extends Controller {

    /** The Constant MONGODB_SDP_LOCK. */
    private static final ObjectId MONGODB_SDP_LOCK = new ObjectId();
    private static final String APP_JSON = "application/json";

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
            List<TimeAnnotation> tags = service.execute();

            for (TimeAnnotation tag : tags) {
                JSONObject msg = tag.toJson();
                msg.put("type", "tag");
                array.put(msg);
            }

            String [] tokens = query.split(" ");

            SearchHyperContentService service2 = new SearchHyperContentService(session("uid"), groupId, query);
            List<HyperContent> contents = service2.execute();


            for (HyperContent content : contents) {
                JSONObject msg = content.toJson();
                msg.put("type", "html");
                String html = content.getContent();
                String text = Tools.getTextFromHtml(html);

                int pos = text.toLowerCase().indexOf(tokens[0].toLowerCase());
                int min = pos - 20;
                int max = pos + 20;
                boolean rmin = false;
                boolean rmax = false;

                if (min < 0) {
                    min = 0;
                } else {
                    rmin = true;
                }

                if (max >= text.length()) {
                    max = text.length();
                } else {
                    rmax = true;
                }

                text = text.substring(min, max);
                String result = "";
                if (rmin) {
                    result += "...";
                }
                result += text;
                if (rmax) {
                    result += "...";
                }

                msg.put("content", result);
                array.put(msg);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        return ok(array.toString()).as(APP_JSON);
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
            List<PermissionDto> permissions = new ArrayList<PermissionDto>();

            attributes.add(new AttributeDto("name", name, false, true, false));
            Set<String> writeSet = new HashSet<String>();
            writeSet.add(session("uid"));
            permissions.add(new PermissionDto("name",null,writeSet));
            CreateGroupService service = new CreateGroupService(session("uid"), visibility,permissions, attributes);
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

        return ok(result.toString()).as(APP_JSON);
    }


    /**
     * List groups.
     *
     * @return the result
     */
    public Result listGroups() {
        if (session("uid") != null) {
            JSONArray array = new JSONArray();
            ListGroupsService service = new ListGroupsService(session("uid"));
            try {
                List<Group> ans = service.execute();
                for (Group g : ans) {
                    Document attributes = new ListOwnerAttributesService(session("uid"), g.getId().toString(), null).execute();
                    JSONObject obj = new JSONObject(attributes.toJson());
                    obj.put("id", g.getId());
                    obj.put("visibility", g.getVisibility());
                    array.put(obj);
                }
            } catch (ServiceException e) {
                e.printStackTrace();
            }
            return ok(array.toString()).as(APP_JSON);
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
                return ok(array.toString()).as(APP_JSON);

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
                return ok(array.toString()).as(APP_JSON);
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }
        return forbidden();
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

            return ok(array.toString()).as(APP_JSON);
        }
        return forbidden();
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
            e.printStackTrace();
            return unauthorized();
        } catch (ServiceException e) {
            e.printStackTrace();

        }
        return badRequest();
    }

    /**
     * Register.
     *
     * @return the result
     * @throws ServiceException the service exception
     */
    public Result register(String userId) throws ServiceException {

        MultipartFormData multipart = request().body().asMultipartFormData();
        Map<String, String[]> form = multipart.asFormUrlEncoded();

        String email = form.get("email")[0];

        String password = form.get("password")[0];
        List<AttributeDto> attributes = new ArrayList<AttributeDto>();
        List<PermissionDto> permissions = new ArrayList<PermissionDto>();


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
                    attributes.add(new AttributeDto(s.getKey(), obj, identifiable, searchable, false));
                }
            }
        }

        for (FilePart fp : multipart.getFiles()) {
            KeyValueFile kvf = new KeyValueFile(fp.getKey(), fp.getFilename(), fp.getFile());
            UploadFileService service = new UploadFileService(kvf);
            try {
                attributes.add(new AttributeDto(kvf.getKey(), "/file/"+service.execute(), false, false, false));
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }

        String existingUser = new GetOwnerByAttributeService("email", email).execute();
        if (existingUser != null) {
            return Rest.status(409);
        }

        User ret = new RegisterUserService(password,permissions, attributes).execute();
        session("uid",ret.getId().toString());
        return ok(ret.getToken());

    }

    public Result search() throws ServiceException {
        Map<String, String[]> query = request().queryString();
        Integer limit = query.containsKey("limit") ? Integer.valueOf(query.get("limit")[0]) : null;
        Integer skip = query.containsKey("skip") ? Integer.valueOf(query.get("skip")[0]) : null;
        String search = query.containsKey("query") ? query.get("query")[0] : null;

        String[] attrs = query.containsKey("show") ? query.get("show") : null;
        List<String> projection = new ArrayList<String>();
        if(attrs!=null){
            for(String attr : attrs) {
                projection.add(attr);
            }
        }

        String [] filtersArray =  query.containsKey("filter") ? query.get("filter") : new String[0];

        List<List<KeyValue<String>>> filters = new ArrayList<List<KeyValue<String>>>();
        for(String f : filtersArray) {
            String[] ands = f.split("\\|");

            List<KeyValue<String>> andList = new ArrayList<KeyValue<String>>();
            for(String and : ands) {
                String [] kvp = and.split(":");
                String key = kvp[0];
                String value = kvp.length == 2 ? kvp[1] : null;
                andList.add(new KeyValue<String>(key, value));
                System.out.println("-> " + f);
            }
            filters.add(andList);
        }

        SearchDataService service = new SearchDataService(session("uid"),skip,limit,search,filters);
        List<String> owners = service.execute();
        JSONArray array = new JSONArray();
        for (String owner : owners) {

            try {
                ListOwnerAttributesService attributesService = new ListOwnerAttributesService(session("uid"),owner,projection);
                Document attributes = attributesService.execute();
                attributes.append("id",owner);
                JSONObject doc = new JSONObject(attributes.toJson());
                array.put(doc);

            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }
        JSONObject obj = new JSONObject();
        obj.put("data", array);
        obj.put("count", service.getCount());
        obj.put("total", service.getTotal());

        return ok(obj.toString()).as(APP_JSON);
    }
}
