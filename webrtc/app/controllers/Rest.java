package controllers;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import play.mvc.Controller;
import play.mvc.Result;

public class Rest extends Controller {

		public static List<String> ips = new ArrayList<String>(); 
	
    public static Result addAddress() {
    		String msg = request().body().asText();
    		ips.add(msg);
    		System.out.println(msg);
        return ok();
    }

    public static Result getAddresses(){
    		JSONArray array = new JSONArray();
    		for(String ip : ips){
    			array.put(ip);
    		}
    		return ok(array.toString());
    }
    

    
}
