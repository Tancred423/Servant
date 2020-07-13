// Author: Tancred423 (https://github.com/Tancred423)
package utilities;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import servant.LoggingTask;
import servant.MyUser;
import servant.Servant;

import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import static servant.Database.closeQuietly;

public class MessageUtil {
    public void sendAndExpire(MessageChannel channel, Message message, long cooldown) {
        channel.sendMessage(message).queue(sentMessage -> {
            Servant.myDeletedMessageCache.put(sentMessage.getIdLong(), "");

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    sentMessage.delete().queue(s -> {}, f -> {});
                }
            }, cooldown);
        });
    }

    public void reactAchievement(Message message) {
        var jda = message.getJDA();
        var emote = EmoteUtil.getEmote(jda, "achievement");
        if (emote != null) message.addReaction(emote).queue(s -> {}, f -> {});
        else {
            var emoji = EmoteUtil.getEmoji(jda, "achievement");
            if (emoji != null) message.addReaction(emoji).queue();
        }
    }

    public static MessageEmbed createUsageEmbed(String commandName, User author, String lang, String description, String[] aliases, String usage, String hint) {
        var myUser = new MyUser(author);

        var sb = new StringBuilder();
        for (var alias : aliases) sb.append(alias).append("\n");
        var alias = sb.toString();

        var eb = new EmbedBuilder()
                .setColor(Color.decode(myUser.getColorCode()))
                .setTitle(commandName.substring(0, 1).toUpperCase() + commandName.substring(1))
                .addField(LanguageHandler.get(lang, "description"), description == null ? " " : description, true)
                .addField(aliases.length > 1 ? LanguageHandler.get(lang, "aliases") : LanguageHandler.get(lang, "alias"), alias.isEmpty() ? " " : alias, true)
                .addField(LanguageHandler.get(lang, "usage"), usage == null ? " " : usage, false);

        if (hint != null) eb.addField(LanguageHandler.get(lang, "hint"), hint, false);

        if (commandName.equalsIgnoreCase("birthday")) {
            eb.setFooter(LanguageHandler.get(lang, "usageembed_birthday_settings"));
        }

        return eb.build();
    }

    public static String removePrefix(JDA jda, long id, boolean isFromGuild, String invoke) {
        Connection connection = null;
        var prefix = Servant.config.getDefaultPrefix();

        try {
            connection = Servant.db.getHikari().getConnection();
            var preparedStatement = connection.prepareStatement(
                    "SELECT prefix " +
                            "FROM " + (isFromGuild ? "guilds" : "users") + " " +
                            "WHERE " + (isFromGuild ? "guild_id" : "user_id") + "=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) {
                var tmpPrefix = resultSet.getString("prefix");
                if (!tmpPrefix.trim().isEmpty()) prefix = tmpPrefix;
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MessageUtil#removePrefix"));
        } finally {
            closeQuietly(connection);
        }

        return invoke.replaceFirst(Pattern.quote(prefix), "");
    }
}
