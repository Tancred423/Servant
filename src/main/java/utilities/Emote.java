// Author: Tancred423 (https://github.com/Tancred423)
package utilities;

import servant.Database;
import servant.Servant;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Emote {
    public static String getEmoteMention(String emoteName) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT guild_id, emote_id FROM emote WHERE emote_name=?");
        select.setString(1, emoteName);
        var resultSet = select.executeQuery();
        if (resultSet.first())
            return Servant.jda
                    .getGuildById(resultSet.getLong("guild_id"))
                    .getEmoteById(resultSet.getLong("emote_id")).getAsMention();
        else return getEmoji(emoteName);
    }

    public static net.dv8tion.jda.core.entities.Emote getEmote(String emoteName) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT guild_id, emote_id FROM emote WHERE emote_name=?");
        select.setString(1, emoteName);
        var resultSet = select.executeQuery();
        if (resultSet.first())
            return Servant.jda
                    .getGuildById(resultSet.getLong("guild_id"))
                    .getEmoteById(resultSet.getLong("emote_id"));
        else return null;
    }

    public static String getEmoji(String emojiName) {
        switch (emojiName) {
            case "baguette1":
            case "baguette2":
            case "baguette3":
                return "\uD83E\uDD56"; // 🥖

            case "upvote":
                return "\uD83D\uDC4D"; // 👍

            case "shrug":
                return "\uD83E\uDD37"; // 🤷

            case "downvote":
                return "\uD83D\uDC4E"; // 👎

            case "end":
                return "❌"; // ❌

            case "one":
                return "1⃣";
            case "two":
                return "2⃣";
            case "three":
                return "3⃣";
            case "four":
                return "4⃣";
            case "five":
                return "5⃣";
            case "six":
                return "6⃣";
            case "seven":
                return "7⃣";
            case "eight":
                return "8⃣";
            case "nine":
                return "9⃣";
            case "ten":
                return "\uD83D\uDD1F";

            case "achievement":
                return "✨";

            case "tada":
                return "\uD83C\uDF89";

            default:
                return null;
        }
    }

    public static String[] getVoteEmotes() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM emote");
        var resultSet = select.executeQuery();
        List<String> emotes = new ArrayList<>();
        if (resultSet.first()) {
            do {
                if (resultSet.getString("emote_name").startsWith("vote"))
                    emotes.add(Servant.jda.getGuildById(resultSet.getLong("guild_id")).getEmoteById(resultSet.getLong("emote_id")).getAsMention());
            } while (resultSet.next());
        }

        if (emotes.size() != 10) {
            emotes.clear();
            emotes.add("1⃣");
            emotes.add("2⃣");
            emotes.add("3⃣");
            emotes.add("4⃣");
            emotes.add("5⃣");
            emotes.add("6⃣");
            emotes.add("7⃣");
            emotes.add("8⃣");
            emotes.add("9⃣");
            emotes.add("\uD83D\uDD1F");
        }

        return emotes.toArray(new String[0]);
    }
}
