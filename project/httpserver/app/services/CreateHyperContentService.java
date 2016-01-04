package services;

import models.Group;
import models.HyperContent;
import models.User;
import org.bson.types.ObjectId;

import java.util.Date;

// TODO: Auto-generated Javadoc

/**
 * The Class AuthenticateUserService.
 */
public class CreateHyperContentService extends Service<HyperContent> {

    private User user;
    private Group group;
    private Date start, end;
    private String content = null;


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
