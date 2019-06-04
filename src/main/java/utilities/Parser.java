package utilities;

import java.awt.*;
import java.time.OffsetDateTime;

public class Parser {
    public static String parseColor(String colorCode) {
        switch (colorCode.length()) {
            case 6:
                colorCode = "0x" + colorCode;
                try {
                    Color.decode(colorCode);
                } catch (NumberFormatException e) {
                    colorCode = null;
                }
                break;

            case 7:
                colorCode = "#" + colorCode;
                try {
                    Color.decode(colorCode);
                } catch (NumberFormatException e) {
                    colorCode = null;
                }
                break;

            case 8:
                try {
                    Color.decode(colorCode);
                } catch (NumberFormatException e) {
                    colorCode = null;
                }
                break;

            default:
                colorCode = null;
                break;
        }

        return colorCode;
    }

    public static boolean isOlderThanTwoWeeks(OffsetDateTime creationTime) {
        long twoWeeksAgo = System.currentTimeMillis() - 1000 * 3600 * 24 * 14; // 2 weeks
        return creationTime.toEpochSecond() * 1000 < twoWeeksAgo;
    }
}
