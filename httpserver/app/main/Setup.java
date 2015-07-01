package main;
import org.json.JSONObject;

import exceptions.ServiceException;
import services.AuthenticateUserService;
import services.GetUserProfileService;
import services.ListUsersService;
import services.RegisterUserService;
import services.Service;


public class Setup {
	
	public static void main(String [] args){
		Service.init();
		
		if (Service.users.count() == 0) {
			try {
				RegisterUserService regService = new RegisterUserService(
						"admin", "Administrator", "admin",
						new JSONObject("{'test':'123'}"), null);
				regService.addPermission("ADMIN");
				regService.execute();

			} catch (ServiceException e) {
				e.printStackTrace();
			}


		}

		AuthenticateUserService auth = new AuthenticateUserService(
				"admin", "admin");
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

		GetUserProfileService details = new GetUserProfileService("admin",
				"admin");
		try {
			System.out.println(details.execute());
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

}
