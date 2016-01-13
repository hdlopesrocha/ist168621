package main;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import exceptions.ServiceException;
import org.slf4j.LoggerFactory;
import play.Application;
import play.GlobalSettings;
import play.libs.F.Promise;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;
import services.Service;

public class Global extends GlobalSettings {

    public static KurentoManager manager;
    private static Logger log;

    @Override
    public Promise<Result> onError(RequestHeader arg0, Throwable arg1) {
        if (arg1 instanceof ServiceException) {
            return Promise.<Result>pure(Results.forbidden());
        }

        return super.onError(arg0, arg1);
    }

    @Override
    public void onStart(Application app) {
        System.out.println("Application has started");
        log = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
       // log.setLevel(Level.ALL);
        Service.init("webrtc");
        manager = new KurentoManager();
    }

    @Override
    public void onStop(Application app) {

        System.out.println("Application shutdown...");
    }

}