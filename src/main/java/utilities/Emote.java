// Author: Tancred423 (https://github.com/Tancred423)
package utilities;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import servant.Log;
import servant.Servant;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import static utilities.DatabaseConn.closeQuietly;

public class Emote {
    public static String getEmoteMention(JDA jda, String emoteName, Guild guild, User user) {
        Connection connection = null;
        var emoteMention = "";

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT guild_id, emote_id FROM emote WHERE emote_name=?");
            select.setString(1, emoteName);
            var resultSet = select.executeQuery();

            if (resultSet.first()) {
                var thisGuild = jda.getGuildById(resultSet.getLong("guild_id"));
                if (thisGuild == null) return emoteMention;
                var emote = thisGuild.getEmoteById(resultSet.getLong("emote_id"));
                if (emote == null) return emoteMention;

                emoteMention = emote.getAsMention();
            }
            else emoteMention = getEmoji(emoteName);
        } catch (SQLException e) {
            new Log(e, guild, user, "emote", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return emoteMention;
    }

    public static net.dv8tion.jda.api.entities.Emote getEmote(String emoteName, Guild guild, User user) {
        Connection connection = null;
        net.dv8tion.jda.api.entities.Emote emote = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT guild_id, emote_id FROM emote WHERE emote_name=?");
            select.setString(1, emoteName);
            var resultSet = select.executeQuery();

            if (resultSet.first()) {
                var thisGuild = user.getJDA().getGuildById(resultSet.getLong("guild_id"));
                if (thisGuild != null) emote = thisGuild.getEmoteById(resultSet.getLong("emote_id"));
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "emote", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return emote;
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

            case "love":
                return "♥";

            default:
                return null;
        }
    }

    public static String[] getVoteEmotes(Guild guild, User user) {
        Connection connection = null;
        var emotes = new ArrayList<String>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM emote");
            var resultSet = select.executeQuery();
            if (resultSet.first())
                do {
                    if (resultSet.getString("emote_name").startsWith("vote")) {
                        var thisGuild = user.getJDA().getGuildById(resultSet.getLong("guild_id"));
                        if (thisGuild != null) {
                            var emote = thisGuild.getEmoteById(resultSet.getLong("emote_id"));
                            if (emote != null) emotes.add(emote.getAsMention());
                        }
                    }
                } while (resultSet.next());

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
        } catch (SQLException e) {
            new Log(e, guild, user, "emote", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return emotes.toArray(new String[0]);
    }
}
