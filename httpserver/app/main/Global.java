package main;

import exceptions.ServiceException;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.F.Promise;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;
import services.Service;

public class Global extends GlobalSettings {

	public final static KurentoManager manager;
	static {
		Service.init("webrtc");
		System.out.println(Service.getCurrentTime());
	//	Setup.main(null);
	
		manager = new KurentoManager();

		
	}

	@Override
	public Promise<Result> onError(RequestHeader arg0, Throwable arg1) {
		if (arg1 instanceof ServiceException) {
			return Promise.<Result> pure(Results.forbidden());
		}

		return super.onError(arg0, arg1);
	}

	@Override
	public void onStart(Application app) {
		Logger.info("Application has started");
	}

	@Override
	public void onStop(Application app) {
		Logger.info("Application shutdown...");
	}

}