// Author: Tancred423 (https://github.com/Tancred423)
package utilities;

import java.util.Calendar;
import java.util.Date;

public class Time {
    public static long getDelayToNextMinuteInMillis() {
        var now = new Date();
        var calendar = Calendar.getInstance();
        calendar.setTime(now);

        var hour = calendar.get(Calendar.HOUR);
        var minute = calendar.get(Calendar.MINUTE);

        if (minute >= 59) {
            hour++;
            minute = 0;
        } else minute++;

        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis() - System.currentTimeMillis();
    }

    public static long getDelayToNextQuarterInMillis() {
        var now = new Date();
        var calendar = Calendar.getInstance();
        calendar.setTime(now);

        var hour = calendar.get(Calendar.HOUR);
        var minute = calendar.get(Calendar.MINUTE);

        if (minute >= 45) {
            hour++;
            minute = 0;
        } else if (minute >= 30) minute = 45;
        else if (minute >= 15) minute = 30;
        else minute = 15;

        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis() - System.currentTimeMillis();
    }
}
