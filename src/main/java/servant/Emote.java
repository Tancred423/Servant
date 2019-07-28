package servant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Emote {
    public static net.dv8tion.jda.core.entities.Emote getEmote(String emoteName) throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT guild_id, emote_id FROM emote WHERE emote_name=?");
        select.setString(1, emoteName);
        ResultSet resultSet = select.executeQuery();
        net.dv8tion.jda.core.entities.Emote emote = null;
        if (resultSet.first())
            emote = Servant.jda
                    .getGuildById(resultSet.getLong("guild_id"))
                    .getEmoteById(resultSet.getLong("emote_id"));
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

            default:
                return null;
        }
    }
}
