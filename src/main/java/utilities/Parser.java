package utilities;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.entities.*;
import servant.Servant;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

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
                colorCode = colorCode.replaceAll("#", "0x");
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

    public static boolean hasMentionedUser(Message message) {
        return !message.getMentionedMembers().isEmpty();
    }

    // z1 should be the saved time; z2 should be the current time.
    public static long getTimeDifferenceInMillis(ZonedDateTime z1, ZonedDateTime z2) {
        return ChronoUnit.MILLIS.between(z1, z2);
    }

    public static int getTotalLevelExp(int level) {
        int totalExp = 0;

        for (int i = level; i >= 0; i--) {
            totalExp += getLevelExp(i);
        }

        return totalExp;
    }

    public static int getLevelExp(int level) {
        return 5*(level*level)+50*level+100;
    }

    public static int getLevelFromExp(int exp) {
        int remaining_exp = exp;
        int level = 0;
        while (remaining_exp >= getLevelExp(level)) {
            remaining_exp -= getLevelExp(level);
            level++;
        }
        return level;
    }

    // Autorole
    public static Role getRoleFromMessage(CommandEvent event) {
        Role role;
        try {
            role = event.getMessage().getMentionedRoles().get(0);
        } catch (IndexOutOfBoundsException e) {
            String[] args = event.getArgs().split(" ");
            if (args.length < 2) return null;
            String id = args[1];
            if (!id.matches("[0-9]+") || id.length() != 18) {
                return null;
            }
            long roleId = Long.parseLong(id);
            role = Servant.jda.getGuildById(event.getGuild().getIdLong()).getRoleById(roleId);
        }

        return role;
    }

    // Guild
    public static boolean isValidOffset(String offset) {
        boolean isValidOffset = true;
        if (offset.length() != 6) isValidOffset = false;
        if (!offset.startsWith("+") && !offset.startsWith("-")) isValidOffset = false;
        if (!offset.substring(1, 3).matches("[0-9]+")) isValidOffset = false;
        int hours = Integer.parseInt(offset.substring(1, 3));
        if (hours < 0 || hours > 14) isValidOffset = false;
        if (!offset.substring(3, 4).equals(":")) isValidOffset = false;
        if (!offset.substring(4, 6).matches("[0-9]+")) isValidOffset = false;
        int minutes = Integer.parseInt(offset.substring(4, 6));
        if (minutes < 0 || minutes > 59) isValidOffset = false;
        return isValidOffset;
    }

    public static boolean isValidUrl(String urlString) {
        // Check for valid url.
        try {
            new URL(urlString).openConnection();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static boolean isValidDirectUrl(String urlString) {
        URLConnection connection;
        try {
            connection = new URL(urlString).openConnection();
        } catch (IOException e) {
            return false;
        }
        String contentType = connection.getHeaderField("Content-Type");
        return contentType.startsWith("image/");
    }
}
