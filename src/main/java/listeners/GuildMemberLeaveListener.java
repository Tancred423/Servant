// Author: Tancred423 (https://github.com/Tancred423)
package listeners;

import files.language.LanguageHandler;
import moderation.guild.Server;
import moderation.log.Log;
import moderation.toggle.Toggle;
import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import owner.blacklist.Blacklist;
import servant.Servant;
import utilities.ImageUtil;

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
        if (Blacklist.isBlacklisted(guild, user)) return;

        CompletableFuture.runAsync(() -> {
            var server = new Server(guild);
            var master = new Master(user);
            var selfMember = guild.getMemberById(event.getJDA().getSelfUser().getIdLong());
            if (selfMember == null) return; // To eliminate errors. Will never occur.
            var jda = event.getJDA();
            var lang = server.getLanguage();

            // Leave
            if (Toggle.isEnabled(event, "leave")) {
                var leaveNotifierChannel = server.getLeaveNotifierChannel();

                if (leaveNotifierChannel != null && leaveNotifierChannel.canTalk(selfMember)) {
                    var description = server.getLeaveMessage();
                    leaveNotifierChannel.sendMessage(
                            new EmbedBuilder()
                                    .setColor(master.getColor())
                                    .setAuthor(String.format(LanguageHandler.get(lang, "leave_author"), user.getName(), user.getDiscriminator()), null, guild.getIconUrl())
                                    .setDescription(description == null ? LanguageHandler.get(lang, "leave_embeddescription") : description)
                                    .setThumbnail(user.getEffectiveAvatarUrl())
                                    .setFooter(LanguageHandler.get(lang, "leave_footer"), ImageUtil.getImageUrl(event.getJDA(), "clock"))
                                    .setTimestamp(OffsetDateTime.now(ZoneOffset.of(server.getOffset())))
                                    .build()
                    ).queue();
                }
            }

            // Log
            if (server.getLogChannelId() != 0 && server.logIsEnabled("member_leave")) {
                var logChannel = guild.getTextChannelById(server.getLogChannelId());
                if (logChannel != null) {
                    logChannel.sendMessage(Log.getLogEmbed(jda, master.getColor(),
                            LanguageHandler.get(lang, "log_member_leave_title"),
                            String.format(LanguageHandler.get(lang, "log_member_leave_description"), event.getMember().getEffectiveName())
                    )).queue();
                }
            }
        }, Servant.fixedThreadPool);
    }
}
