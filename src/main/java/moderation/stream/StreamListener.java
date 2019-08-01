// Author: Tancred423 (https://github.com/Tancred423)
package moderation.stream;

import moderation.guild.Guild;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.user.update.UserUpdateGameEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import servant.Log;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class StreamListener extends ListenerAdapter {
    public void onUserUpdateGame(UserUpdateGameEvent event) {
        try {
            if (!new Guild(event.getGuild().getIdLong()).getToggleStatus("stream")) return;
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getUser(), "stream", null).sendLog(false);
            return;
        }

        var guild = event.getGuild();
        var author = event.getUser();
        var oldGame = event.getOldGame();
        var newGame = event.getNewGame();

        // Get guilds and users
        List<Long> streamers;
        try {
            streamers = new Guild(guild.getIdLong()).getStreamers();
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getUser(), "stream", null).sendLog(false);
            return;
        }

        // Only the streamers get a mention.
        if (author.isBot()) return;
        if (!streamers.contains(author.getIdLong())) return;

        // Check if guild owner started streaming.
        if (oldGame != null && newGame != null) {
            if (!oldGame.getType().toString().equalsIgnoreCase("streaming")
                    && newGame.getType().toString().equalsIgnoreCase("streaming")) {
                try {
                    sendNotification(author, newGame, guild, new Guild(guild.getIdLong()));
                    addRole(guild, event.getMember(), guild.getRoleById(new Guild(guild.getIdLong()).getStreamingRoleId()));
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getUser(), "stream", null).sendLog(false);
                }
            } else if (oldGame.getType().toString().equalsIgnoreCase("streaming") &&
                    !newGame.getType().toString().equalsIgnoreCase("streaming")) {
                try {
                    removeRole(guild, event.getMember(), guild.getRoleById(new Guild(guild.getIdLong()).getStreamingRoleId()));
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getUser(), "stream", null).sendLog(false);
                }
            }
        } else if (newGame != null) {
            if (newGame.getType().toString().equalsIgnoreCase("streaming")) {
                try {
                    sendNotification(author, newGame, guild, new Guild(guild.getIdLong()));
                    addRole(guild, event.getMember(), guild.getRoleById(new Guild(guild.getIdLong()).getStreamingRoleId()));
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getUser(), "stream", null).sendLog(false);
                }
            }
        }
    }

    private static void addRole(net.dv8tion.jda.core.entities.Guild guild, Member member, Role role) {
        guild.getController().addSingleRoleToMember(member, role).queue();
    }

    private static void removeRole(net.dv8tion.jda.core.entities.Guild guild, Member member, Role role) {
        guild.getController().removeSingleRoleFromMember(member, role).queue();
    }

    private static void sendNotification(User author, Game newGame, net.dv8tion.jda.core.entities.Guild guild, Guild internalGuild) throws SQLException {
        guild.getTextChannelById(internalGuild.getStreamChannelId()).sendMessage(getNotifyMessage(author, newGame, guild, new servant.User(author.getIdLong()))).queue();
    }

    private static Message getNotifyMessage(User author, Game newGame, net.dv8tion.jda.core.entities.Guild guild, servant.User internalUser) throws SQLException {
        MessageBuilder mb = new MessageBuilder();
        EmbedBuilder eb = new EmbedBuilder();
        mb.setContent(guild.getPublicRole().getAsMention());
        eb.setColor(Color.decode(internalUser.getColorCode()));
        eb.setAuthor("Livestream!", newGame.getUrl(), "https://i.imgur.com/BkMsIdz.png"); // Twitch Logo
        eb.setTitle(newGame.getName());
        eb.setDescription(String.format("%s just went live on [Twitch (click me)](%s)!", author.getAsMention(), newGame.getUrl()));
        eb.setThumbnail(author.getAvatarUrl());
        eb.setFooter("Streaming " + newGame.asRichPresence().getDetails(), null);
        mb.setEmbed(eb.build());
        return mb.build();
    }
}