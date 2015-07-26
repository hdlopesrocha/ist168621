package main;

import java.util.ArrayList;

import org.json.JSONObject;

import exceptions.ServiceException;
import models.Group;
import models.KeyValueFile;
import models.User;
import services.AddGroupMemberService;
import services.AuthenticateUserService;
import services.CreateGroupService;
import services.CreateRelationService;
import services.GetUserProfileService;
import services.ListUsersService;
import services.RegisterUserService;
import services.Service;

public class Setup {

	public static void main(String[] args) {
		Service.init("webrtc");
		Service.getDatabase().drop();

		try {
			RegisterUserService regService = new RegisterUserService("admin", "admin", new JSONObject("{'test':'123'}"), new ArrayList<KeyValueFile>());
			regService.addPermission("ADMIN");
			regService.execute();
		} catch (ServiceException e) {
			e.printStackTrace();
		}

		try {
			User u1 = new RegisterUserService("hdlopesrocha", "qazokm", new JSONObject(), new ArrayList<KeyValueFile>()).execute();
			User u2 = new RegisterUserService("nbhatt", "qazokm", new JSONObject(), new ArrayList<KeyValueFile>()).execute();
			User u3 = new RegisterUserService("vils", "qazokm", new JSONObject(), new ArrayList<KeyValueFile>()).execute();
			 
			 
			new CreateRelationService(u1.getId().toString(), u2.getId().toString()).execute();
			new CreateRelationService(u1.getId().toString(), u3.getId().toString()).execute();
			new CreateRelationService(u2.getId().toString(), u1.getId().toString()).execute();
			new CreateRelationService(u3.getId().toString(), u1.getId().toString()).execute();

			Group g1 = new CreateGroupService(u1.getId().toString(), "WebRTC").execute();
			 
			new AddGroupMemberService(u1.getId().toString(), g1.getId().toString(), u2.getId().toString()).execute();
			new AddGroupMemberService(u1.getId().toString(), g1.getId().toString(), u3.getId().toString()).execute();
		} catch (ServiceException e) {
			e.printStackTrace();
		}

		
		
		
		
		AuthenticateUserService auth = new AuthenticateUserService("admin", "admin");
		try {
			if (auth.execute()) {
				System.out.println("AUTH OK!");
			} else {
				System.out.println("AUTH ERROR!");
			}
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ListUsersService listUsers = new ListUsersService("admin");
		try {
			System.out.println(listUsers.execute());
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		GetUserProfileService details = new GetUserProfileService("admin", "admin");
		try {
			System.out.println(details.execute());
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
