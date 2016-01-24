package services;

import models.Group;
import models.HyperContent;
import models.User;
import org.bson.types.ObjectId;

import java.util.Date;


/**
 * The Class CreateHyperContentService.
 */
public class CreateHyperContentService extends Service<HyperContent> {

    /** The user. */
    private final User user;
    
    /** The group. */
    private final Group group;
    
    /** The start. */
    private final Date start;
    
    /** The end. */
    private final Date end;
    
    /** The content. */
    private String content = null;


    /**
     * Instantiates a new creates the hyper content service.
     *
     * @param uid the uid
     * @param gid the gid
     * @param start the start
     * @param end the end
     * @param content the content
     */
    public CreateHyperContentService(String uid, String gid, Date start, Date end, String content) {
        this.user = User.findById(new ObjectId(uid));
        this.group = Group.findById(new ObjectId(gid));
        this.content = content;
        this.end = end;
        this.start = start;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public HyperContent dispatch() {
        HyperContent hyperContent = new HyperContent(group.getId(), start, end, content);
        hyperContent.save();
        return hyperContent;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {
        return user != null;
    }

}
