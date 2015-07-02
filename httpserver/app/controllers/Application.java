package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public Result index() {
    	if(session("email")!=null){
    		return ok(index.render());
    	} else {
    		return ok(sign.render());
    		
    	}
    	
    }

}
