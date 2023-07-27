package biz.binarysolutions.mindfulnessmeditation.util;

import java.util.Calendar;
import java.util.Date;

/**
 *
 */
public class Timestamp {

    /**
     *
     * @param date
     * @return
     */
    private static Date trim(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND,      0);
        calendar.set(Calendar.MINUTE,      0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);

        return calendar.getTime();
    }

    /**
     *
     * @return
     */
    public static long today() {
        return trim(new Date()).getTime();
    }

    /**
     *
     * @return
     */
    public static long now() {
        return (new Date()).getTime();
    }
}
