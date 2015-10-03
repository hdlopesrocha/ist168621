package controllers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import exceptions.ServiceException;
import main.Tools;
import models.KeyValueFile;
import models.Recording;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import services.CreateRecordingService;
import services.ListRecordingsForTimeService;
import services.ListRecordingsService;
import services.PublishService;

public class Deprecated extends Controller{

	// POST	/api/rec/:gid					controllers.Rest.saveRecording(gid : String)
	public Result saveRecording(String groupId) throws ParseException {
		MultipartFormData multipart = request().body().asMultipartFormData();
		Map<String, String[]> form = multipart.asFormUrlEncoded();

		String userId = form.get("uid")[0];
		Date start = Tools.FORMAT.parse(form.get("start")[0]);
		Date end = Tools.FORMAT.parse(form.get("end")[0]);

		System.out.println("XXX : " + start + " | " + end);
		String name = form.get("name")[0];
		String type = form.get("type")[0];
		String inter = form.containsKey("inter") ? form.get("inter")[0] : null;

		List<KeyValueFile> files = new ArrayList<KeyValueFile>();
		for (FilePart fp : multipart.getFiles()) {
			KeyValueFile kvf = new KeyValueFile(fp.getKey(), fp.getFilename(), fp.getFile());
			files.add(kvf);
		}

		CreateRecordingService saveService = new CreateRecordingService(files.get(0), null, groupId, userId, start, end,
				name, type, inter);
		PublishService publishService = new PublishService("rec:" + groupId);
		try {
			Recording rec = saveService.execute();
			publishService.execute();
			System.out.println("saved recording!");
			return ok(rec.getInterval().toString());

		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return badRequest();

	}
	
	// GET		/api/rec/:gid/:seq				controllers.Rest.listRecordings(gid : String, seq : Long)
	public Result listRecordings(String groupId, Long sequence) {
		ListRecordingsService service = new ListRecordingsService(session("uid"), groupId, sequence);
		try {
			List<Recording> res = service.execute();
			JSONArray array = new JSONArray();

			for (Recording rec : res) {
				JSONObject jObj = new JSONObject();
				// jObj.put("id", rec.getId());
				jObj.put("seq", rec.getSequence());
				jObj.put("start", Tools.FORMAT.format(rec.getStart()));
				jObj.put("end", Tools.FORMAT.format(rec.getEnd()));
				jObj.put("url", rec.getUrl());
				jObj.put("uid", rec.getUserId().toString());
				if (rec.getInterval() != null) {
					jObj.put("inter", rec.getInterval().toString());
				}
				jObj.put("type", rec.getType());
				jObj.put("name", rec.getName());

				array.put(jObj);
			}
			return ok(array.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return badRequest();
	}

	// GET		/api/rect/:gid/:time/:dur		controllers.Rest.listRecordingsForTime(gid : String, time : String, dur : Long)
	public Result listRecordingsForTime(String groupId, String time, Long duration) {
		System.out.println("listRecordingsForTime(" + groupId + "," + time + "," + duration + ")");
		ListRecordingsForTimeService service = new ListRecordingsForTimeService(session("uid"), groupId, time,
				duration);
		try {
			List<Recording> res = service.execute();
			JSONArray array = new JSONArray();

			for (Recording rec : res) {
				JSONObject jObj = new JSONObject();
				// jObj.put("id", rec.getId());
				jObj.put("seq", rec.getSequence());
				jObj.put("start", Tools.FORMAT.format(rec.getStart()));
				jObj.put("end", Tools.FORMAT.format(rec.getEnd()));
				jObj.put("url", rec.getUrl());
				jObj.put("uid", rec.getUserId().toString());
				if (rec.getInterval() != null) {
					jObj.put("inter", rec.getInterval().toString());
				}
				jObj.put("type", rec.getType());
				jObj.put("name", rec.getName());

				array.put(jObj);
			}
			return ok(array.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return badRequest();

	}
	
	
}
