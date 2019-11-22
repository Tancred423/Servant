// Author: Tancred423 (https://github.com/Tancred423)
package moderation.livestream;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.toggle.Toggle;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.user.UserActivityEndEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import owner.blacklist.Blacklist;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class LivestreamListener extends ListenerAdapter {
    public void onUserActivityStart(@Nonnull UserActivityStartEvent event) {
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, "livestream")) return;

            var guild = event.getGuild();
            var author = event.getUser();
            var newActivity = event.getNewActivity();

            var lang = new Guild(guild.getIdLong()).getLanguage(guild, author);

            if (author.isBot()) return;
            if (Blacklist.isBlacklisted(event.getUser(), event.getGuild())) return;

            // Users can hide themselves from this feature.
            if (new moderation.user.User(author.getIdLong()).isStreamHidden(event.getGuild().getIdLong(), guild, author)) return;

            var isStreamerMode = new Guild(guild.getIdLong()).isStreamerMode(guild, author);

            // Check if user is streamer if guild is in streamer mode.
            if (isStreamerMode)
                if (!new Guild(guild.getIdLong()).getStreamers(guild, author).contains(author.getIdLong())) return;

            var type = newActivity.getType().name();

            if (type.equalsIgnoreCase("streaming")) {
                sendNotification(author, newActivity, guild, new Guild(guild.getIdLong()), isStreamerMode, lang);
                addRole(guild, event.getMember(), guild.getRoleById(new Guild(guild.getIdLong()).getStreamingRoleId(guild, author)));
            }

            removeRole(guild, event.getMember(), guild.getRoleById(new Guild(guild.getIdLong()).getStreamingRoleId(guild, author)));
        });
    }

    public void onUserActivityEnd(@Nonnull UserActivityEndEvent event) {
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, "livestream")) return;

            var guild = event.getGuild();
            var author = event.getUser();
            var oldActivity = event.getOldActivity();

            if (author.isBot()) return;
            if (Blacklist.isBlacklisted(event.getUser(), event.getGuild())) return;

            // Users can hide themselves from this feature.
            if (new moderation.user.User(author.getIdLong()).isStreamHidden(event.getGuild().getIdLong(), guild, author)) return;

            var isStreamerMode = new Guild(guild.getIdLong()).isStreamerMode(guild, author);

            // Check if user is streamer if guild is in streamer mode.
            if (isStreamerMode)
                if (!new Guild(guild.getIdLong()).getStreamers(guild, author).contains(author.getIdLong())) return;

            var type = oldActivity.getType().name();

            if (!type.equalsIgnoreCase("streaming")) {
                removeRole(guild, event.getMember(), guild.getRoleById(new Guild(guild.getIdLong()).getStreamingRoleId(guild, author)));
            }
        });
    }

    private static void addRole(net.dv8tion.jda.api.entities.Guild guild, Member member, Role role) {
        var selfMember = guild.getMemberById(guild.getJDA().getSelfUser().getIdLong());
        if (selfMember == null) return; // todo: always null?

        if (role != null && selfMember.canInteract(member)) {
            var rolesToAdd = new ArrayList<Role>();
            rolesToAdd.add(role);
            guild.modifyMemberRoles(member, rolesToAdd, null).queue();
        }
    }

    private static void removeRole(net.dv8tion.jda.api.entities.Guild guild, Member member, Role role) {
        var selfMember = guild.getMemberById(guild.getJDA().getSelfUser().getIdLong());
        if (selfMember == null) return; // todo: always null?

        if (role != null && selfMember.canInteract(member)) {
            var rolesToRemove = new ArrayList<Role>();
            rolesToRemove.add(role);
            guild.modifyMemberRoles(member, null, rolesToRemove).queue();
        }
    }

    private static void sendNotification(User author, Activity newActuvuty, net.dv8tion.jda.api.entities.Guild guild, Guild internalGuild, boolean isStreamerMode, String lang) {
        if (internalGuild.getStreamChannelId(guild, author) != 0) {
            var tc = guild.getTextChannelById(internalGuild.getStreamChannelId(guild, author));
            if (tc != null) tc.sendMessage(getNotifyMessage(author, newActuvuty, new moderation.user.User(author.getIdLong()), isStreamerMode, lang, guild)).queue();
        }
    }

    private static Message getNotifyMessage(User author, Activity newActivity, moderation.user.User internalUser, boolean isStreamerMode, String lang, net.dv8tion.jda.api.entities.Guild guild) {
        MessageBuilder mb = new MessageBuilder();
        EmbedBuilder eb = new EmbedBuilder();
        if (isStreamerMode) mb.setContent("@here");
        eb.setColor(Color.decode(internalUser.getColorCode(guild, author)));
        eb.setAuthor(LanguageHandler.get(lang, "livestream_announcement_title"), newActivity.getUrl(), "https://i.imgur.com/BkMsIdz.png"); // Twitch Logo
        eb.setTitle(newActivity.getName());
        eb.setDescription(String.format(LanguageHandler.get(lang, "livestream_announcement"), author.getAsMention(), newActivity.getUrl()));
        eb.setThumbnail(author.getAvatarUrl());
        var rp = newActivity.asRichPresence();
        eb.setFooter(String.format(LanguageHandler.get(lang, "livestream_announcement_game"), rp == null ? "" :  rp.getDetails()), null);
        mb.setEmbed(eb.build());
        return mb.build();
    }
}
