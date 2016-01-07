package services;

import exceptions.ServiceException;
import models.TimeTag;
import org.bson.types.ObjectId;

import java.util.Date;

public class CreateTimeTagService extends Service<TimeTag> {

    private final ObjectId gid;
    private final String title;
    private final String content;
    private final Date time;

    public CreateTimeTagService(String gid, Date time, String title, String content) {
        this.gid = new ObjectId(gid);
        this.time = time;
        this.title = title;
        this.content = content;
    }

    @Override
    public TimeTag dispatch() throws ServiceException {
        TimeTag tag = new TimeTag(gid, time, title, content);
        tag.save();
        return tag;
    }

    @Override
    public boolean canExecute() {
        return true;
    }

}
