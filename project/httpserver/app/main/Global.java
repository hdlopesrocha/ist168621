package main;


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


/**
 * The Class Global.
 */
public class Global extends GlobalSettings {

    /** The manager. */
    public static KurentoManager manager;
    
    /** The log. */
    private static Logger log;

    /* (non-Javadoc)
     * @see play.GlobalSettings#onError(play.mvc.Http.RequestHeader, java.lang.Throwable)
     */
    @Override
    public Promise<Result> onError(RequestHeader arg0, Throwable arg1) {
        if (arg1 instanceof ServiceException) {
            return Promise.<Result>pure(Results.forbidden());
        }

        return super.onError(arg0, arg1);
    }

    /* (non-Javadoc)
     * @see play.GlobalSettings#onStart(play.Application)
     */
    @Override
    public void onStart(Application app) {
        System.out.println("Application has started");
        log = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        // log.setLevel(Level.ALL);
        Service.init("webrtc");
        manager = new KurentoManager();
    }

    /* (non-Javadoc)
     * @see play.GlobalSettings#onStop(play.Application)
     */
    @Override
    public void onStop(Application app) {

        System.out.println("Application shutdown...");
    }

}