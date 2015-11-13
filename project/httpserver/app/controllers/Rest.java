package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import exceptions.ConflictException;
import exceptions.ServiceException;
import exceptions.UnauthorizedException;
import models.Group;
import models.KeyValueFile;
import models.KeyValuePair;
import models.Membership;
import models.Relation;
import models.User;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import services.AddGroupMemberService;
import services.AuthenticateTokenService;
import services.AuthenticateUserService;
import services.ChangeUserPasswordService;
import services.CreateGroupInviteService;
import services.CreateGroupService;
import services.CreateRelationService;
import services.CreateUserService;
import services.DeleteGroupInviteService;
import services.DenyRelationService;
import services.DownloadFileService;
import services.GetGroupInviteService;
import services.GetUserPhotoService;
import services.GetUserProfileService;
import services.JoinGroupInviteService;
import services.ListGroupMembersService;
import services.ListGroupsService;
import services.ListRelationRequestsService;
import services.ListRelationsService;
import services.ListUsersService;
import services.PostIceCandidateService;
import services.PostSdpService;
import services.PublishService;
import services.RemoveGroupMemberService;
import services.SearchGroupCandidatesService;
import services.SearchUserService;
import services.SubscribeService;
import services.UpdateUserService;
import views.html.defaultpages.error;

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
				// TODO Auto-generated catch block
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

	/**
	 * Gets the photo.
	 *
	 * @param username
	 *            the username
	 * @return the photo
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return notFound();
	}

	public Result getPhoto(String uid) {
		if (uid != null && uid.length() > 0) {

			try {
				String fileName = new GetUserPhotoService(uid).execute();
				if (fileName != null) {
					System.out.println("GET "+fileName);
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

	public Result getUser(String userId) {
		try {
			return ok(new GetUserProfileService(session("uid"), userId).execute());
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return unauthorized();
	}

	public Result listGroupMembers(String groupId) {
		if (session("uid") != null) {
			ListGroupMembersService service = new ListGroupMembersService(session("uid"), groupId);
			try {
				List<KeyValuePair<Membership, User>> ans = service.execute();
				JSONArray array = new JSONArray();
				for (KeyValuePair<Membership, User> kvp : ans) {
					JSONObject obj = new JSONObject();
					obj.put("uid", kvp.getKey().getUserId());
					obj.put("email", kvp.getValue().getEmail());
					obj.put("mid", kvp.getKey().getId());
					obj.put("name", kvp.getValue().getPublicProperties().getString("name"));
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
					obj.put("email", user.getEmail());
					obj.put("uid", user.getId().toString());
					array.put(obj);
				}
				return ok(array.toString());

			} catch (ServiceException e) {
				// TODO Auto-generated catch block
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
				List<User> res = service.execute();
				for (User user : res) {
					JSONObject obj = new JSONObject();
					obj.put("email", user.getEmail());
					obj.put("uid", user.getId().toString());
					array.put(obj);
				}
				return ok(array.toString());

			} catch (ServiceException e) {
				// TODO Auto-generated catch block
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
			if (map.containsKey("token")) {
				String token = map.get("token")[0];

				AuthenticateTokenService service = new AuthenticateTokenService(token);
				if (service.execute()) {
					session("email", service.getEmail());
					session("uid", service.getUserId());
					return ok(service.getToken());
				} else {
					if (service.userExists())
						return Rest.status(409);
					else
						return unauthorized();
				}
			} else if (map.containsKey("email") && map.containsKey("password")) {
				String email = map.get("email")[0];

				String password = map.get("password")[0];

				AuthenticateUserService service = new AuthenticateUserService(email, password);
				if (service.execute()) {

					session("email", email);
					session("uid", service.getUserId());
					return ok(service.getToken());
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

	public Result register() {

		MultipartFormData multipart = request().body().asMultipartFormData();
		Map<String, String[]> form = multipart.asFormUrlEncoded();

		String email = form.get("email")[0];

		String password = form.get("password")[0];

		JSONObject info = new JSONObject();
		for (Entry<String, String[]> s : form.entrySet()) {
			if (!s.getKey().equals("email") && !s.getKey().equals("password") && !s.getKey().equals("password2")) {
				String[] value = s.getValue();
				if (value.length == 1) {
					info.put(s.getKey(), value[0]);
				} else if (value.length > 1) {
					JSONArray array = new JSONArray();
					for (int i = 0; i < value.length; ++i) {
						array.put(value[i]);
					}
					info.put(s.getKey(), array);
				}
			}
		}

		List<KeyValueFile> files = new ArrayList<KeyValueFile>();
		for (FilePart fp : multipart.getFiles()) {
			KeyValueFile kvf = new KeyValueFile(fp.getKey(), fp.getFilename(), fp.getFile());
			files.add(kvf);
		}

		CreateUserService service = new CreateUserService(email, password, info, files);
		try {
			User ret = service.execute();
			// session("email", email);
			return ok(ret.getToken());
		} catch (ConflictException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Rest.status(409);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return unauthorized();
		}

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

			List<User> res = new SearchUserService(session("uid"), query).execute();
			JSONArray array = new JSONArray();
			for (User u : res) {
				if (!u.getId().equals(me.getId())) {
					JSONObject obj = new JSONObject();
					obj.put("email", u.getEmail());
					obj.put("uid", u.getId().toString());
					Relation rel = Relation.findByEndpoint(me.getId(), u.getId());

					if (rel == null) {
						rel = Relation.findByEndpoint(u.getId(), me.getId());
					}
					if (rel != null) {
						obj.put("state", me.getId().equals(rel.getFrom()) ? rel.getToState() : rel.getFromState());
					}
					array.put(obj);
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
			try {
				List<User> ans = service.execute();
				JSONArray array = new JSONArray();
				for (User u : ans) {
					JSONObject obj = new JSONObject();
					obj.put("uid", u.getId().toString());
					obj.put("email", u.getEmail());
					array.put(obj);
					obj.put("name", u.getPublicProperties().getString("name"));

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

			UpdateUserService service = new UpdateUserService(session("uid"), info, files);

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
	
	public Result createGroupInvite(String groupId) throws ServiceException{
		CreateGroupInviteService service = new CreateGroupInviteService(session("uid"), groupId);
		return ok(service.execute());
	}
	
	public Result getGroupInvite(String groupId) throws ServiceException{
		GetGroupInviteService service = new GetGroupInviteService(session("uid"), groupId);
		String token = service.execute();
		if(token!=null){
			return ok(token);
		}
		return badRequest();
	}

	public Result deleteGroupInvite(String groupId) throws ServiceException{
		DeleteGroupInviteService service = new DeleteGroupInviteService(session("uid"), groupId);
		service.execute();
		return ok();
	}
	
	public Result joinGroupInvite(String groupId, String token) throws ServiceException{
		JoinGroupInviteService service = new JoinGroupInviteService(session("uid"), groupId, token);
		if(service.execute()){			
			return ok(groupId);
		}
		return badRequest();
	}
	
	
}