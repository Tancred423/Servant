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

import java.awt.*;
import java.util.concurrent.CompletableFuture;

public class LivestreamListener extends ListenerAdapter {
    public void onUserUpdateGame(UserUpdateGameEvent event) {
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, "livestream")) return;

            var guild = event.getGuild();
            var author = event.getUser();
            var oldGame = event.getOldGame();
            var newGame = event.getNewGame();
            var lang = new Guild(guild.getIdLong()).getLanguage(guild, author);

            if (author.isBot()) return;
            if (Blacklist.isBlacklisted(event.getUser(), event.getGuild())) return;

            // Users can hide themselves from this feature.
            if (new moderation.user.User(author.getIdLong()).isStreamHidden(event.getGuild().getIdLong(), guild, author)) return;

            var isStreamerMode = new Guild(guild.getIdLong()).isStreamerMode(guild, author);

            // Check if user is streamer if guild is in streamer mode.
            if (isStreamerMode)
                if (!new Guild(guild.getIdLong()).getStreamers(guild, author).contains(author.getIdLong())) return;

            // Check if guild owner started streaming.
            if (oldGame != null && newGame != null) {
                if (!oldGame.getType().toString().equalsIgnoreCase("streaming")
                        && newGame.getType().toString().equalsIgnoreCase("streaming")) {
                    sendNotification(author, newGame, guild, new Guild(guild.getIdLong()), isStreamerMode, lang);
                    addRole(guild, event.getMember(), guild.getRoleById(new Guild(guild.getIdLong()).getStreamingRoleId(guild, author)));
                } else if (oldGame.getType().toString().equalsIgnoreCase("streaming") &&
                        !newGame.getType().toString().equalsIgnoreCase("streaming")) {
                    removeRole(guild, event.getMember(), guild.getRoleById(new Guild(guild.getIdLong()).getStreamingRoleId(guild, author)));
                }
            } else if (oldGame == null && newGame != null) {
                if (newGame.getType().toString().equalsIgnoreCase("streaming")) {
                    sendNotification(author, newGame, guild, new Guild(guild.getIdLong()), isStreamerMode, lang);
                    addRole(guild, event.getMember(), guild.getRoleById(new Guild(guild.getIdLong()).getStreamingRoleId(guild, author)));
                }
            } else if (oldGame != null) {
                if (oldGame.getType().toString().equalsIgnoreCase("streaming")) {
                    removeRole(guild, event.getMember(), guild.getRoleById(new Guild(guild.getIdLong()).getStreamingRoleId(guild, author)));
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

    private static void sendNotification(User author, Game newGame, net.dv8tion.jda.core.entities.Guild guild, Guild internalGuild, boolean isStreamerMode, String lang) {
        if (internalGuild.getStreamChannelId(guild, author) != 0)
            guild.getTextChannelById(internalGuild.getStreamChannelId(guild, author)).sendMessage(getNotifyMessage(author, newGame, new moderation.user.User(author.getIdLong()), isStreamerMode, lang, guild)).queue();
    }

    private static Message getNotifyMessage(User author, Game newGame, moderation.user.User internalUser, boolean isStreamerMode, String lang, net.dv8tion.jda.core.entities.Guild guild) {
        MessageBuilder mb = new MessageBuilder();
        EmbedBuilder eb = new EmbedBuilder();
        if (isStreamerMode) mb.setContent("@here");
        eb.setColor(Color.decode(internalUser.getColorCode(guild, author)));
        eb.setAuthor(LanguageHandler.get(lang, "livestream_announcement_title"), newGame.getUrl(), "https://i.imgur.com/BkMsIdz.png"); // Twitch Logo
        eb.setTitle(newGame.getName());
        eb.setDescription(String.format(LanguageHandler.get(lang, "livestream_announcement"), author.getAsMention(), newGame.getUrl()));
        eb.setThumbnail(author.getAvatarUrl());
        eb.setFooter(String.format(LanguageHandler.get(lang, "livestream_announcement_game"), newGame.asRichPresence().getDetails()), null);
        mb.setEmbed(eb.build());
        return mb.build();
    }
}
