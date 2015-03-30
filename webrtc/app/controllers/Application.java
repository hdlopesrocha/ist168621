package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {

	public static List<String> ips = new ArrayList<String>();

	public static Result index() {
		if (session("user") != null) {
			return ok(views.html.index.render());
		} else {
			return redirect(routes.Application.getRegister());
		}
	}

	public static Result login() {
		Map<String, String[]> body = request().body().asFormUrlEncoded();
		String user = body.get("user")[0];
		String pass = body.get("pass")[0];
		session("user",user);
		return redirect(routes.Application.index());
	}

	public static Result logout() {
		session().remove("user");
		return redirect(routes.Application.index());
	}
	
	public static Result getRegister() {
		return ok(views.html.register.render());
	}

	public static Result postRegister() {
		return ok(views.html.index.render());
	}
	

	public static Result sample() {
		return ok(views.html.sample.render());
	}

	public static Result smil() {
		return ok(views.html.smil.render());
	}

	public static Result add(String ip) {
		ips.add(ip);
		return ok();
	}

}
