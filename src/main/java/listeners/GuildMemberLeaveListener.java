// Author: Tancred423 (https://github.com/Tancred423)
package listeners;

import commands.owner.blacklist.Blacklist;
import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import servant.MyGuild;
import servant.MyUser;
import servant.Servant;
import utilities.Constants;
import utilities.ImageUtil;

import java.awt.*;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class GuildMemberLeaveListener extends ListenerAdapter {
    // This event will be thrown if a user leaves a guild the bot is on.
    public void onGuildMemberLeave(@NotNull GuildMemberLeaveEvent event) {
        var guild = event.getGuild();
        var user = event.getUser();

        // Blacklist
        if (guild.getIdLong() == Constants.DISCORD_BOT_LIST_ID) return;
        if (user.isBot()) return;
        if (Blacklist.isBlacklisted(guild, user)) return;

        CompletableFuture.runAsync(() -> {
            var myGuild = new MyGuild(guild);
            var myUser = new MyUser(user);
            var selfMember = guild.getMemberById(event.getJDA().getSelfUser().getIdLong());
            if (selfMember == null) return; // To eliminate errors. Will never occur.
            var lang = myGuild.getLanguageCode();
            var jda = event.getJDA();
            var guildOwner = guild.getOwner();
            if (guildOwner == null) return;
            var myGuildOwnerUser = new MyUser(guildOwner.getUser());

            // Leave
            if (myGuild.pluginIsEnabled("leave") && myGuild.categoryIsEnabled("moderation")) {
                var leaveTc = myGuild.getLeaveTc();

                if (leaveTc != null && leaveTc.canTalk(selfMember)) {
                    var description = myGuild.getLeaveMessage();
                    leaveTc.sendMessage(
                            new EmbedBuilder()
                                    .setColor(Color.decode(myUser.getColorCode()))
                                    .setAuthor(String.format(LanguageHandler.get(lang, "leave_author"), user.getName()), null, guild.getIconUrl())
                                    .setDescription(description == null ? LanguageHandler.get(lang, "leave_embeddescription") : description)
                                    .setThumbnail(user.getEffectiveAvatarUrl())
                                    .setFooter(LanguageHandler.get(lang, "leave_footer"), ImageUtil.getUrl(event.getJDA(), "clock"))
                                    .setTimestamp(Instant.now())
                                    .build()
                    ).queue();
                }
            }

            // Log
            if (myGuild.pluginIsEnabled("log") && myGuild.categoryIsEnabled("moderation") && myGuild.logIsEnabled("user_leave")) {
                var logChannel = guild.getTextChannelById(myGuild.getLogChannelId());
                if (logChannel != null) {
                    logChannel.sendMessage(
                            new EmbedBuilder()
                                    .setColor(myGuildOwnerUser.getColor())
                                    .setTitle(LanguageHandler.get(lang, "log_user_leave_title"))
                                    .addField(LanguageHandler.get(lang, "user"), event.getMember().getUser().getName() + "#" + event.getMember().getUser().getDiscriminator() + "\n" + event.getMember().getIdLong(), true)
                                    .setFooter(LanguageHandler.get(lang, "log_at"), ImageUtil.getUrl(jda, "clock"))
                                    .setTimestamp(Instant.now())
                                    .build()
                    ).queue();
                }
            }
        }, Servant.fixedThreadPool);
    }
}
