package utilities;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import servant.Servant;

import java.awt.*;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

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

    public static boolean hasMentionedUser(Message message) {
        return !message.getMentionedMembers().isEmpty();
    }

    // z1 should be the saved time; z2 should be the current time.
    public static long getTimeDifferenceInMillis(ZonedDateTime z1, ZonedDateTime z2) {
        return ChronoUnit.MILLIS.between(z1, z2);
    }

    // Level
//    private static int multiplicator = 5;
//
//    public static int getLevelFromExp(int y) {
//        double p = (double) 50 / multiplicator;
//        double q = ((double) 100 - y) / multiplicator;
//        int currentLevel = (int) Math.floor(-(p/2) + Math.sqrt(Math.pow((p/2), 2)-q));
//        if (currentLevel < 0) return 0;
//        else return currentLevel;
//    }
//
//    public static int getExpFromLevel(int x) {
//        return multiplicator*((int) Math.pow(x, 2))+50*x+100;
//    }
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
}
