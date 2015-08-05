package main;

import java.util.List;

import org.kurento.client.KurentoClient;
import org.kurento.client.MediaPipeline;

import exceptions.ServiceException;
import models.Group;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.F.Promise;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;
import services.Service;

public class Global extends GlobalSettings {

	public final static KurentoClient kurento = KurentoClient.create("ws://enter4ward.dtdns.net:8888/kurento");
	public final static RoomManager manager = new RoomManager();
	
	static {
		Service.init("webrtc");
		System.out.println(Service.getCurrentTime());
	//	Setup.main(null);
	

		// release previously created pipelines
		for(MediaPipeline mp : kurento.getServerManager().getPipelines()) {
			mp.release();
		}
		
		List<Group> allGroups = Group.listAll();
		for(Group g : allGroups){
			manager.getRoom(g.getId().toString());
		}
		

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