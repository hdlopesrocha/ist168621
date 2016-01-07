package main;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

public class Tools {

    public static final DateFormat FORMAT;

    public static final Random RANDOM = new Random();
    private static final String allChars = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM0987654321";
    private static final Random random = new Random();

    static {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        FORMAT.setTimeZone(tz);
    }

    /**
     * Gets the random salt.
     *
     * @param size the size
     * @return the random salt
     */
    public static String getRandomString(int size) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size; ++i) {
            builder.append(allChars.charAt(random.nextInt(allChars.length())));
        }
        return builder.toString();
    }

    static public String friendlyTime(String time) {
        try {

            if (time.charAt(time.length() - 1) != 'Z') {
                time += "Z";
            }


            ZonedDateTime zdt = ZonedDateTime.parse(time);
            LocalDateTime ldt = zdt.toLocalDateTime();


            return String.format("%02d", ldt.getDayOfMonth()) + "/" + String.format("%02d", ldt.getMonthValue()) + "/" + String.format("%04d", ldt.getYear())
                    + " " + String.format("%02d", ldt.getHour()) + ":" + String.format("%02d", ldt.getMinute()) + ":" + String.format("%02d", ldt.getSecond());
        } catch (Exception e) {
            e.printStackTrace();
            // return "00/00/0000 00:00:00";
        }
        return time;
    }

    public static String getCurrentTime() {
        return FORMAT.format(new Date());
        //return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC).format(Instant.now());
    }

}
