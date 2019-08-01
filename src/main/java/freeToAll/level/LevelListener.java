// Author: Tancred423 (https://github.com/Tancred423)
package freeToAll.level;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import servant.Log;
import servant.Servant;
import utilities.Parser;

import java.sql.SQLException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class LevelListener extends ListenerAdapter {
    private static Map<Guild, Map<User, ZonedDateTime>> guildCds = new HashMap<>();

    private static int getLevel(long userId, long guildId) throws SQLException {
        return Parser.getLevelFromExp(new servant.User(userId).getExp(guildId));
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        var author = event.getAuthor();
        var guild = event.getGuild();

        if (author.isBot()) return;

        // Enabled?
        try {
            if (!new moderation.guild.Guild(guild.getIdLong()).getToggleStatus("level")) return;
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), "level", null).sendLog(false);
            return;
        }

        Map<User, ZonedDateTime> userCd = guildCds.get(guild);

        if (userCd != null) {
            var lastMessage = userCd.get(author);
            if (lastMessage != null) {
                // Check if last message is older than the exp cooldown.
                long difference = Parser.getTimeDifferenceInMillis(lastMessage, ZonedDateTime.now(ZoneOffset.UTC));
                long expCooldown = Integer.parseInt(Servant.config.getExpCdMillis());
                if (difference <= expCooldown) return;
            }
        } else {
            userCd = new HashMap<>();
        }

        userCd.put(author, ZonedDateTime.now(ZoneOffset.UTC));
        guildCds.put(guild, userCd);

        var authorId = author.getIdLong();
        var guildId = guild.getIdLong();

        int currentLevel;
        try {
            currentLevel = getLevel(authorId, guildId);
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), "level", null).sendLog(true);
            return;
        }
        var randomExp = ThreadLocalRandom.current().nextInt(15, 26); // Between 15 and 25 inclusively.
        try {
            new servant.User(authorId).addExp(guildId, randomExp);
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), "level", null).sendLog(true);
            return;
        }
        int updatedLevel;
        try {
            updatedLevel = getLevel(authorId, guildId);
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), "level", null).sendLog(true);
            return;
        }

        if (updatedLevel > currentLevel) {
            event.getChannel().sendMessage(author.getAsMention() + " just reached level " + updatedLevel + "! \uD83C\uDF89").queue(); // 🎉
        }
    }
}
