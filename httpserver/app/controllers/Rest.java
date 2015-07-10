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
import services.CreateGroupService;
import services.CreateRelationService;
import services.DenyRelationService;
import services.DownloadFileService;
import services.GetSdpService;
import services.GetUserPhotoService;
import services.GetUserProfileService;
import services.ListGroupMembersService;
import services.ListGroupsService;
import services.ListRelationRequestsService;
import services.ListRelationsService;
import services.ListUsersService;
import services.PostSdpService;
import services.PublishService;
import services.RegisterUserService;
import services.RemoveGroupMemberService;
import services.SearchGroupCandidatesService;
import services.SearchUserService;
import services.SubscribeService;
import services.UpdateUserService;

public class Rest extends Controller {

	public Result updateUser() {
		try {
			MultipartFormData multipart = request().body().asMultipartFormData();
			JSONObject info = new JSONObject(multipart.asFormUrlEncoded().get("json")[0]);

			FilePart photo = multipart.getFile("photo");

			UpdateUserService service = new UpdateUserService(session("email"), info,
					new KeyValueFile("photo", photo.getFilename(), photo.getFile()));

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

	public Result denyRelation(String email) {
		if (session("email") != null) {
			DenyRelationService service = new DenyRelationService(session("email"), email);
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

	public Result listGroups() {
		if (session("email") != null) {
			ListGroupsService service = new ListGroupsService(session("email"));
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

	public Result searchGroupCandidates(String groupId) {
		if (session("email") != null) {
			String query = request().queryString().get("s")[0];
			SearchGroupCandidatesService service = new SearchGroupCandidatesService(session("email"), groupId, query);
			try {
				List<User> ans = service.execute();
				JSONArray array = new JSONArray();
				for (User g : ans) {
					JSONObject obj = new JSONObject();
					obj.put("id", g.getId());
					obj.put("email", g.getEmail());
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

	private static final ObjectId MONGODB_SDP_LOCK = new ObjectId();

	public Result publish(String groupId, String token) {
		if (session("email") != null) {
			synchronized (MONGODB_SDP_LOCK) {
				String sdpjson = request().body().asText();
				try {
					PostSdpService service = new PostSdpService(session("email"), groupId, token, sdpjson);
					service.execute();
					PublishService service2 = new PublishService(groupId, "");
					service2.execute();
					return ok("OK");
				} catch (ServiceException e) {
					e.printStackTrace();
				}
			}
			return badRequest();
		}
		return forbidden();

	}

	public Result subscribe(String groupId, Long ts) {
		SubscribeService service = new SubscribeService(groupId, ts);
		try {
			Document doc = service.execute();
			JSONObject obj = new JSONObject();
			obj.put("ts", doc.getLong("ts"));
			JSONObject data = new JSONObject();
			List<Membership> members = Membership.listByGroup(new ObjectId(groupId));
			for (Membership member : members) {
				data.put(member.getId().toString(), new JSONObject(member.getProperties().toJson()));
			}
			obj.put("data", data);
			obj.put("key", doc.getString("key"));
			return ok(obj.toString());
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return badRequest();
	}

	public Result addGroupMember(String groupId, String memberId) {
		if (session("email") != null) {
			AddGroupMemberService service = new AddGroupMemberService(session("email"), groupId, memberId);
			try {
				service.execute();
			} catch (ServiceException e) {
				e.printStackTrace();
			}
			return ok("OK");
		}
		return forbidden();
	}

	public Result removeGroupMember(String groupId, String memberId) {
		if (session("email") != null) {
			RemoveGroupMemberService service = new RemoveGroupMemberService(session("email"), groupId, memberId);
			try {
				service.execute();
			} catch (ServiceException e) {
				e.printStackTrace();
			}
			return ok("OK");
		}
		return forbidden();
	}

	public Result listGroupMembers(String groupId) {
		if (session("email") != null) {
			ListGroupMembersService service = new ListGroupMembersService(session("email"), groupId);
			try {
				List<KeyValuePair<Membership, User>> ans = service.execute();
				JSONArray array = new JSONArray();
				for (KeyValuePair<Membership, User> kvp : ans) {
					if (!kvp.getValue().getEmail().equals(session("email"))) {
						JSONObject obj = new JSONObject();

						obj.put("id", kvp.getKey().getUserId());
						obj.put("email", kvp.getValue().getEmail());
						obj.put("mid", kvp.getKey().getId());

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
		return forbidden();
	}

	public Result createGroup() {
		if (session("email") != null) {
			Map<String, String[]> qs = request().queryString();
			String name = qs.get("n")[0];
			CreateGroupService service = new CreateGroupService(session("email"), name);
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

	public Result acceptRelation(String email) {
		if (session("email") != null) {
			CreateRelationService service = new CreateRelationService(session("email"), email);
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

	public Result listRelations() {
		if (session("email") != null) {
			ListRelationsService service = new ListRelationsService(session("email"));
			try {
				JSONArray array = new JSONArray();
				List<User> res = service.execute();
				for (User user : res) {
					JSONObject obj = new JSONObject();
					obj.put("email", user.getEmail());
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

	public Result listRelationRequests() {
		if (session("email") != null) {
			ListRelationRequestsService service = new ListRelationRequestsService(session("email"));
			try {
				JSONArray array = new JSONArray();
				List<User> res = service.execute();
				for (User user : res) {
					JSONObject obj = new JSONObject();
					obj.put("email", user.getEmail());
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

	public Result changePassword() {
		Map<String, String[]> info = request().body().asFormUrlEncoded();
		String oldPassword = info.get("oldPassword")[0];
		String password1 = info.get("newPassword")[0];

		ChangeUserPasswordService service = new ChangeUserPasswordService(session("email"), oldPassword, password1);
		try {
			service.execute();
			return ok();

		} catch (ServiceException e) {

		}
		return badRequest();
	}

	public Result listUsers() {
		try {
			return ok(new ListUsersService(session("email")).execute());
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return unauthorized();
	}

	public Result getUser(String userId) {
		try {
			return ok(new GetUserProfileService(session("email"), userId).execute());
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

	public Result register() {
		MultipartFormData multipart = request().body().asMultipartFormData();
		Map<String, String[]> form = multipart.asFormUrlEncoded();

		JSONObject info = new JSONObject(form.get("json")[0]);

		String email = info.getString("email");

		String password = info.getString("password");

		info.remove("email");
		info.remove("password");

		List<KeyValueFile> files = new ArrayList<KeyValueFile>();
		for (FilePart fp : multipart.getFiles()) {
			KeyValueFile kvf = new KeyValueFile(fp.getKey(), fp.getFilename(), fp.getFile());
			files.add(kvf);
		}

		RegisterUserService service = new RegisterUserService(email, password, info, files);
		try {
			String ret = service.execute();
			session("email", email);
			return ok(ret);
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

	/**
	 * Gets the photo.
	 *
	 * @param username
	 *            the username
	 * @return the photo
	 */
	public Result getFile(String fileName) {
		fileName = fileName.replace("%20", " ");
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

	public Result getPhoto(String email) {
		if (email != null && email.length() > 0) {

			try {
				return getFile(new GetUserPhotoService(email).execute());
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return notFound();

	}

	public Result search() {
		String query = request().queryString().get("s")[0];

		try {
			User me = User.findByEmail(session("email"));

			List<User> res = new SearchUserService(session("email"), query).execute();
			JSONArray array = new JSONArray();
			for (User u : res) {
				if (!u.getId().equals(me.getId())) {
					JSONObject obj = new JSONObject();
					obj.put("email", u.getEmail());
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

	public Result logout() {
		session().clear();
		return ok();
	}

	public Result register2() {

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

		RegisterUserService service = new RegisterUserService(email, password, info, files);
		try {
			String ret = service.execute();
			// session("email", email);
			return ok(ret);
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

}
