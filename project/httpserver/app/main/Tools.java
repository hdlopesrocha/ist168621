package main;

import models.User;
import org.apache.commons.codec.binary.Hex;
import org.jsoup.Jsoup;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
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



    public static boolean userExists(String uid){
        User u = uid!=null ? User.findById(uid) : null;
        return u!=null;
    }

}
