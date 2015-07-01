package controllers;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import exceptions.ConflictException;
import exceptions.ServiceException;
import exceptions.UnauthorizedException;
import models.KeyValueFile;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import services.AuthenticateTokenService;
import services.AuthenticateUserService;
import services.ChangeUserPasswordService;
import services.DownloadFileService;
import services.GetUserPhotoService;
import services.GetUserProfileService;
import services.ListUsersService;
import services.RegisterUserService;
import services.UpdateUserService;

public class Rest extends Controller {

	public Result updateUser() {
		try {
			MultipartFormData multipart = request().body()
					.asMultipartFormData();
			JSONObject info = new JSONObject(multipart.asFormUrlEncoded().get(
					"json")[0]);

			FilePart photo = multipart.getFile("photo");

			UpdateUserService service = new UpdateUserService(session("email"),
					info, new KeyValueFile("photo", photo.getFilename(),
							photo.getFile()));

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

	public Result changePassword() {
		Map<String, String[]> info = request().body().asFormUrlEncoded();
		String oldPassword = info.get("oldPassword")[0];
		String password1 = info.get("newPassword")[0];

		ChangeUserPasswordService service = new ChangeUserPasswordService(
				session("email"), oldPassword, password1);
		try {
			service.execute();
			return ok();

		} catch (ServiceException e) {

		}
		return badRequest();
	}


	public Result listUsers()  {
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
			return ok(new GetUserProfileService(session("email"), userId)
					.execute());
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return unauthorized();

	}

	public Result auth() {
		try {

			Map<String, String[]> map = request().body().asFormUrlEncoded();
			if (map.containsKey("token")) {
				String token = map.get("token")[0];

				AuthenticateTokenService service = new AuthenticateTokenService(
						token);
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

				AuthenticateUserService service = new AuthenticateUserService(
						email, password);
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
		String name = info.getString("name");
		String password = info.getString("password");

		info.remove("email");
		info.remove("name");
		info.remove("password");

		FilePart photo = multipart.getFile("photo");

		RegisterUserService service = new RegisterUserService(email, name,
				password, info, new KeyValueFile("photo", photo.getFilename(),
						photo.getFile()));
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

		response().setHeader("Content-disposition",
				"attachment;filename=" + tokens[tokens.length - 1]);
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

	

}
