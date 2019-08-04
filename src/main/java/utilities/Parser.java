// Author: Tancred423 (https://github.com/Tancred423)
package utilities;

import net.dv8tion.jda.core.entities.*;
import servant.Servant;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    public static boolean isValidMessageId(MessageChannel channel, String id) {
        try {
            channel.getMessageById(id).queue();
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean isValidDateTime(String input) {
        try {
            LocalDateTime.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

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
        var twoWeeksAgo = System.currentTimeMillis() - 1000 * 3600 * 24 * 14; // 2 weeks
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
        var totalExp = 0;
        for (var i = level; i >= 0; i--) totalExp += getLevelExp(i);
        return totalExp;
    }

    public static int getLevelExp(int level) {
        return 5*(level*level)+50*level+100;
    }

    public static int getLevelFromExp(int exp) {
        var remaining_exp = exp;
        var level = 0;
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
            var args = event.getArgs().split(" ");
            if (args.length < 2) return null;
            var id = args[1];
            if (!id.matches("[0-9]+") || id.length() != 18) return null;
            var roleId = Long.parseLong(id);
            role = Servant.jda.getGuildById(event.getGuild().getIdLong()).getRoleById(roleId);
        }

        return role;
    }

    // Guild
    public static boolean isValidOffset(String offset) {
        var isValidOffset = true;
        if (offset.length() != 6) isValidOffset = false;
        if (!offset.startsWith("+") && !offset.startsWith("-")) isValidOffset = false;
        if (!offset.substring(1, 3).matches("[0-9]+")) isValidOffset = false;
        var hours = Integer.parseInt(offset.substring(1, 3));
        if (hours < 0 || hours > 14) isValidOffset = false;
        if (!offset.substring(3, 4).equals(":")) isValidOffset = false;
        if (!offset.substring(4, 6).matches("[0-9]+")) isValidOffset = false;
        var minutes = Integer.parseInt(offset.substring(4, 6));
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
        var contentType = connection.getHeaderField("Content-Type");
        return contentType.startsWith("image/");
    }

    public static boolean isValidPrefix(String prefix) {
        var isValidPrefix = true;
        if (prefix.length() > 32) isValidPrefix = false;

        // I will handle this properly in the future.
        List<String> someHardcodedStuff = new ArrayList<>();
        someHardcodedStuff.add("select");
        someHardcodedStuff.add("drop");
        someHardcodedStuff.add("delete");
        someHardcodedStuff.add("insert");
        someHardcodedStuff.add("update");
        if (someHardcodedStuff.contains(prefix)) isValidPrefix = false;

        return isValidPrefix;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isValidVoiceChannelId(Guild guild, String id) {
        if (!id.matches("[0-9]+") || id.length() != 18) return false;
        guild.getVoiceChannelById(id);
        return true;
    }
}
