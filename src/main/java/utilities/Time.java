// Author: Tancred423 (https://github.com/Tancred423)
package utilities;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimerTask;

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

    public static long getDelayToNext5MinutesInMillis() {
        var now = new Date();
        var calendar = Calendar.getInstance();
        calendar.setTime(now);

        var hour = calendar.get(Calendar.HOUR);
        var minute = calendar.get(Calendar.MINUTE);

        if (minute >= 55) {
            hour++;
            minute = 0;
        } else {
            var lastDigit = 0;

            if (String.valueOf(minute).length() > 1) // e.g. 32
                lastDigit = Integer.parseInt(String.valueOf(minute).substring(1));
            else // e.g. 8
                lastDigit = minute;

            switch (lastDigit) {
                case 0:
                case 5:
                    minute += 5;
                    break;
                case 1:
                case 6:
                    minute += 4;
                    break;
                case 2:
                case 7:
                    minute += 3;
                    break;
                case 3:
                case 8:
                    minute += 2;
                    break;
                case 4:
                case 9:
                    minute += 1;
                    break;
            }
        }

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

    public static long getDelayToNextDayInMillis() {
        var tomorrow = new GregorianCalendar();
        tomorrow.set(Calendar.HOUR_OF_DAY, 0);
        tomorrow.set(Calendar.MINUTE, 0);
        tomorrow.set(Calendar.SECOND, 0);
        tomorrow.set(Calendar.MILLISECOND, 0);
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);

        return tomorrow.getTimeInMillis() - System.currentTimeMillis();
    }

    // This exist, so I can use lambdas in java.util.Timer#schedule.
    public static TimerTask wrap(Runnable r) {
        return new TimerTask() {
            @Override
            public void run() {
                r.run();
            }
        };
    }
}
