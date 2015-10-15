package services;

import java.util.Date;

import org.bson.types.ObjectId;

import exceptions.ServiceException;
import models.TimeTag;

public class CreateTagService extends Service<TimeTag> {
	
	private ObjectId gid;
	private String title;
	private String content;
	private Date time;
	
	public CreateTagService(String gid, Date time, String title, String content) {
		this.gid = new ObjectId(gid);
		this.time = time;
		this.title = title;
		this.content = content;
	}
	
	@Override
	public TimeTag dispatch() throws ServiceException {
		TimeTag tag = new TimeTag(gid,time,title,content);
		tag.save();
		return tag;
	}

	@Override
	public boolean canExecute() {
		return true;
	}

}
