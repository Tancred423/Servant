// Author: Tancred423 (https://github.com/Tancred423)
package utilities;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Parser {
    public static String parseText(String lang, String args, int maxLength) throws ParseException {
        // other stuff "blah blah" other stuff
        args = args.trim().replaceAll(" +", " ");

        var qmCount = args.length() - args.replace("\"", "").length();
        if (qmCount != 0 && qmCount != 2)
            throw new ParseException(LanguageHandler.get(lang, "no_ending_quotation_mark"), 0);

        if (qmCount == 0) return null; // No topic

        var firstIndex = args.indexOf("\"");
        var lastIndex = args.lastIndexOf("\"");

        args = args.substring(firstIndex + 1, lastIndex).trim();

        if (maxLength != 0 && args.length() > maxLength)
            throw new ParseException(LanguageHandler.get(lang, "topic_too_long"), 0);

        return args;
    }

    public static HashMap<Character, Integer> parseArguments(String lang, String args) throws ParseException {
        // 1 p 1min "prize or topic" 1w   2d a y 3w 5 min  10p a r t i c i p a n t s

        args = args.toLowerCase().replaceAll(" ", "");
        args = args.replaceAll("days", "d")
                .replaceAll("day", "d")
                .replaceAll("hours", "h")
                .replaceAll("hour", "h")
                .replaceAll("minutes", "m")
                .replaceAll("minute", "m")
                .replaceAll("mins", "m")
                .replaceAll("min", "m")
                .replaceAll("winners", "w")
                .replaceAll("winner", "w")
                .replaceAll("participants", "p")
                .replaceAll("participant", "p");

        // 1p1m"prizeortopic"1w2d3w5m10p

        var sb = new StringBuilder();

        var firstIndex = args.indexOf("\"");
        if (firstIndex > 0) sb.append(args, 0, firstIndex);

        var lastIndex = args.lastIndexOf("\"");
        sb.append(args.substring(lastIndex + 1));

        args = sb.toString();

        var argMap = new HashMap<Character, Integer>();

        if (args.isEmpty()) return argMap;

        // 1w2d3w5m10p
        sb = new StringBuilder();
        int integer;
        char character;
        for (var i = 0; i < args.length(); i++) {
            var c = args.charAt(i);
            if (Character.isDigit(c) && sb.toString().matches(".*[a-z].*")) {
                var arg = sb.toString();
                var argSplit = arg.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
                integer = Integer.parseInt(argSplit[0]);
                character = argSplit[1].charAt(0);

                argMap.merge(character, integer, Integer::sum);

                sb = new StringBuilder();
            }
            sb.append(c);
        }

        var arg = sb.toString();
        var argSplit = arg.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

        if (argSplit.length != 2) {
            throw new ParseException(LanguageHandler.get(lang, "missing_time_unit"), 0);
        }

        integer = Integer.parseInt(argSplit[0]);
        character = argSplit[1].charAt(0);
        argMap.merge(character, integer, Integer::sum);

        // [w, 4], [d, 2], [m, 5], [p, 10]

        var validArgs = new ArrayList<>(Arrays.asList('d', 'h', 'm', 'w', 'p'));

        for (var entry : argMap.entrySet()) {
            if (!validArgs.contains(entry.getKey()))
                throw new ParseException(String.format(LanguageHandler.get(lang, "invalid_char"), entry.getKey()), 0);
        }

        return argMap;
    }

    public static boolean isValidMessageId(MessageChannel channel, String id) {
        try {
            channel.retrieveMessageById(id).queue(ISnowflake::getId, Throwable::getMessage);
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

    public static boolean hasMentionedUser(Message message) {
        return !message.getMentionedUsers().isEmpty();
    }

    public static long getTimeDifferenceInMillis(Instant now, Instant futureDate) {
        return ChronoUnit.MILLIS.between(now, futureDate);
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

    // Guild
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isValidUrl(String urlString) {
        // Check for valid url.
        try {
            new URL(urlString).openConnection();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
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

    public static boolean isValidId(String id) {
        return id.matches("[0-9]+") && id.length() == 18;
    }
}
