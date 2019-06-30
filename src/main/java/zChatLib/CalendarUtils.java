package zChatLib;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CalendarUtils {
    static int timeZoneOffset() {
        Calendar cal = Calendar.getInstance();
        return (cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET)) / '\uea60';
    }

    public static String year() {
        Calendar cal = Calendar.getInstance();
        return String.valueOf(cal.get(Calendar.YEAR));
    }

    public static String date() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMMMMMMM dd, yyyy");
        dateFormat.setCalendar(cal);
        return dateFormat.format(cal.getTime());
    }

    public static String date(String jformat, String locale, String timezone) {
        if (jformat == null) {
            jformat = "EEE MMM dd HH:mm:ss zzz yyyy";
        }

        if (locale == null) {
            locale = Locale.US.getISO3Country();
        }

        if (timezone == null) {
            timezone = TimeZone.getDefault().getDisplayName();
        }

        String dateAsString = (new Date()).toString();

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(jformat);
            dateAsString = simpleDateFormat.format(new Date());
        } catch (Exception var5) {
            System.out.println("CalendarUtils.date Bad date: Format = " + jformat + " Locale = " + locale + " Timezone = " + timezone);
        }

        System.out.println("CalendarUtils.date: " + dateAsString);
        return dateAsString;
    }
}
