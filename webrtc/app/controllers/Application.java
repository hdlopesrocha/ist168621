package controllers;


import java.util.ArrayList;
import java.util.List;

import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {

		public static List<String> ips = new ArrayList<String>(); 
	
    public static Result index() {
        return ok(views.html.index.render(ips));
    }

    public static Result sample() {
        return ok(views.html.sample.render());
    }

    public static Result smil() {
      return ok(views.html.smil.render());
  }

    
    public static Result add(String ip){
    	 ips.add(ip);
    	 return ok();
    }
    
}
