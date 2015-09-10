package main;


import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import exceptions.ServiceException;
import play.Application;
import play.GlobalSettings;
import play.libs.F.Promise;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;
import services.Service;

public class Global extends GlobalSettings {

	public final static KurentoManager manager;
	public static final Logger log = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
	static {
		log.setLevel(Level.ALL);
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
		System.out.println("Application has started");
	}

	@Override
	public void onStop(Application app) {
		System.out.println("Application shutdown...");
	}

}