package main;

import org.apache.commons.codec.binary.Hex;
import org.jsoup.Jsoup;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Pattern;


/**
 * The Class Tools.
 */
public class Tools {

    /** The Constant FORMAT. */
    public static final DateFormat FORMAT;

    /** The Constant RANDOM. */
    public static final Random RANDOM = new Random();
    
    /** The Constant allChars. */
    private static final String allChars = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM0987654321";
    
    /** The Constant random. */
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

    private static Pattern HTML_PATTERN = Pattern.compile("<(\\w+)>.*?</\\1>");

    public static String getTextFromHtml(String html){
/*
        Matcher m = HTML_PATTERN.matcher(html);
        String text = "";
        while (m.find()) {
            text += m.group();
        }
        return text;
*/

        return Jsoup.parse(html).text();
    }

    /**
     * Friendly time.
     *
     * @param time the time
     * @return the string
     */
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

    /**
     * Gets the current time.
     *
     * @return the current time
     */
    public static String getCurrentTime() {
        return FORMAT.format(new Date());
        //return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC).format(Instant.now());
    }

    public static int clamp(int min, int x, int max) {
        return (x<min? min :(x>=max? max : x));

    }

    public static String md5(String content) {
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(content.getBytes(Charset.forName("UTF8")));
            final byte[] resultByte = messageDigest.digest();
             return new String(Hex.encodeHex(resultByte));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
