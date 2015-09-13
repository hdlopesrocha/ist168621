package main;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class Tools {

public static final DateFormat FORMAT;
    
	static {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		FORMAT.setTimeZone(tz);
	}
}
