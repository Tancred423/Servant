// Author: Tancred423 (https://github.com/Tancred423)
package moderation.livestream;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.toggle.Toggle;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.user.update.UserUpdateGameEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import owner.blacklist.Blacklist;
import servant.Log;
import servant.Servant;

import java.awt.*;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class LivestreamListener extends ListenerAdapter {
    public void onUserUpdateGame(UserUpdateGameEvent event) {
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, "livestream")) return;

            var guild = event.getGuild();
            var author = event.getUser();
            var oldGame = event.getOldGame();
            var newGame = event.getNewGame();

            String lang;
            try {
                lang = new Guild(guild.getIdLong()).getLanguage();
            } catch (SQLException e) {
                lang = Servant.config.getDefaultLanguage();
            }

            if (author.isBot()) return;
            if (Blacklist.isBlacklisted(event.getUser(), event.getGuild())) return;

            // Users can hide themselves from this feature.
            try {
                if (new moderation.user.User(author.getIdLong()).isStreamHidden(event.getGuild().getIdLong())) return;
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getUser(), "stream", null).sendLog(false);
                return;
            }

            boolean isStreamerMode;
            try {
                isStreamerMode = new Guild(guild.getIdLong()).isStreamerMode();
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getUser(), "stream", null).sendLog(false);
                return;
            }

            // Check if user is streamer if guild is in streamer mode.
            if (isStreamerMode) {
                try {
                    if (!new Guild(guild.getIdLong()).getStreamers().contains(author.getIdLong())) return;
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getUser(), "stream", null).sendLog(false);
                    return;
                }
            }

            // Check if guild owner started streaming.
            if (oldGame != null && newGame != null) {
                if (!oldGame.getType().toString().equalsIgnoreCase("streaming")
                        && newGame.getType().toString().equalsIgnoreCase("streaming")) {
                    try {
                        sendNotification(author, newGame, guild, new Guild(guild.getIdLong()), isStreamerMode, lang);
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
            } else if (oldGame == null && newGame != null) {
                if (newGame.getType().toString().equalsIgnoreCase("streaming")) {
                    try {
                        sendNotification(author, newGame, guild, new Guild(guild.getIdLong()), isStreamerMode, lang);
                        addRole(guild, event.getMember(), guild.getRoleById(new Guild(guild.getIdLong()).getStreamingRoleId()));
                    } catch (SQLException e) {
                        new Log(e, event.getGuild(), event.getUser(), "stream", null).sendLog(false);
                    }
                }
            } else if (oldGame != null) {
                if (oldGame.getType().toString().equalsIgnoreCase("streaming")) {
                    try {
                        removeRole(guild, event.getMember(), guild.getRoleById(new Guild(guild.getIdLong()).getStreamingRoleId()));
                    } catch (SQLException e) {
                        new Log(e, event.getGuild(), event.getUser(), "stream", null).sendLog(false);
                    }
                }
            }
        });
    }

    private static void addRole(net.dv8tion.jda.core.entities.Guild guild, Member member, Role role) {
        if (role != null && guild.getMemberById(guild.getJDA().getSelfUser().getIdLong()).canInteract(member)) guild.getController().addSingleRoleToMember(member, role).queue();
    }

    private static void removeRole(net.dv8tion.jda.core.entities.Guild guild, Member member, Role role) {
        if (role != null && guild.getMemberById(guild.getJDA().getSelfUser().getIdLong()).canInteract(member)) guild.getController().removeSingleRoleFromMember(member, role).queue();
    }

    private static void sendNotification(User author, Game newGame, net.dv8tion.jda.core.entities.Guild guild, Guild internalGuild, boolean isStreamerMode, String lang) throws SQLException {
        if (internalGuild.getStreamChannelId() != 0)
            guild.getTextChannelById(internalGuild.getStreamChannelId()).sendMessage(getNotifyMessage(author, newGame, new moderation.user.User(author.getIdLong()), isStreamerMode, lang)).queue();
    }

    private static Message getNotifyMessage(User author, Game newGame, moderation.user.User internalUser, boolean isStreamerMode, String lang) throws SQLException {
        MessageBuilder mb = new MessageBuilder();
        EmbedBuilder eb = new EmbedBuilder();
        if (isStreamerMode) mb.setContent("@here");
        eb.setColor(Color.decode(internalUser.getColorCode()));
        eb.setAuthor(LanguageHandler.get(lang, "livestream_announcement_title"), newGame.getUrl(), "https://i.imgur.com/BkMsIdz.png"); // Twitch Logo
        eb.setTitle(newGame.getName());
        eb.setDescription(String.format(LanguageHandler.get(lang, "livestream_announcement"), author.getAsMention(), newGame.getUrl()));
        eb.setThumbnail(author.getAvatarUrl());
        eb.setFooter(String.format(LanguageHandler.get(lang, "livestream_announcement_game"), newGame.asRichPresence().getDetails()), null);
        mb.setEmbed(eb.build());
        return mb.build();
    }
}
