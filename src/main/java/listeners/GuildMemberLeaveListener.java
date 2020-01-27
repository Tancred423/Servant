// Author: Tancred423 (https://github.com/Tancred423)
package listeners;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import owner.blacklist.Blacklist;
import servant.Servant;
import utilities.Image;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.CompletableFuture;

public class GuildMemberLeaveListener extends ListenerAdapter {
    // This event will be thrown if a user leaves a guild the bot is on.
    public void onGuildMemberLeave(@NotNull GuildMemberLeaveEvent event) {
        var guild = event.getGuild();
        var user = event.getUser();

        /* Certain conditions must meet, so this event is allowed to be executed:
         * 1.   Ignore any request from the Discord Bot List as this big guild
         *      invoke a lot of events, but never use this bot actively.
         * 2.   Ignore any request from bots to prevent infinite loops.
         * 3.   Ignore any request from blacklisted users and guilds.
         */
        if (guild.getIdLong() == 264445053596991498L) return; // Discord Bot List
        if (user.isBot()) return;
        if (Blacklist.isBlacklisted(user, guild)) return;

        CompletableFuture.runAsync(() -> {
            var internalGuild = new Guild(guild.getIdLong());
            var internalUser = new User(user.getIdLong());
            var member = event.getMember();
            var selfMember = guild.getMemberById(event.getJDA().getSelfUser().getIdLong());
            if (selfMember == null) return; // To eliminate errors. Will never occur.

            // Leave
            if (Toggle.isEnabled(event, "leave")) {
                var lang = new Guild(event.getGuild().getIdLong()).getLanguage(guild, user);
                var leaveNotifierChannel = internalGuild.getLeaveNotifierChannel(guild, user);

                if (leaveNotifierChannel != null && leaveNotifierChannel.canTalk(selfMember)) {
                    var description = internalGuild.getLeaveMessage(guild, user);
                    leaveNotifierChannel.sendMessage(
                            new EmbedBuilder()
                                    .setColor(internalUser.getColor(guild, user))
                                    .setAuthor(String.format(LanguageHandler.get(lang, "leave_author"), user.getName(), user.getDiscriminator()), null, guild.getIconUrl())
                                    .setDescription(description == null ? LanguageHandler.get(lang, "leave_embeddescription") : description)
                                    .setThumbnail(user.getEffectiveAvatarUrl())
                                    .setFooter(LanguageHandler.get(lang, "leave_footer"), Image.getImageUrl("clock", guild, user))
                                    .setTimestamp(OffsetDateTime.now(ZoneOffset.of(internalGuild.getOffset(guild, user))))
                                    .build()
                    ).queue();
                }
            }
        }, Servant.threadPool);
    }
}
