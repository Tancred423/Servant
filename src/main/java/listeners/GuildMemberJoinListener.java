// Author: Tancred423 (https://github.com/Tancred423)
package listeners;

import commands.owner.blacklist.Blacklist;
import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import servant.MyGuild;
import servant.MyUser;
import servant.Servant;
import utilities.Constants;
import utilities.ImageUtil;
import utilities.TimeUtil;

import javax.annotation.Nonnull;
import java.awt.*;
import java.time.Instant;
import java.util.Timer;
import java.util.concurrent.CompletableFuture;

public class GuildMemberJoinListener extends ListenerAdapter {
    // This event will be thrown if a user joins a guild the bot is on.
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        var guild = event.getGuild();
        var user = event.getUser();

        // Blacklist
        if (guild.getIdLong() == Constants.DISCORD_BOT_LIST_ID) return;
        if (user.isBot()) return;
        if (Blacklist.isBlacklisted(guild, user)) return;

        CompletableFuture.runAsync(() -> {
            var myGuild = new MyGuild(guild);
            var myUser = new MyUser(user);
            var member = event.getMember();
            var selfMember = guild.getMemberById(event.getJDA().getSelfUser().getIdLong());
            if (selfMember == null) return; // To eliminate errors. Will never occur.
            var lang = myGuild.getLanguageCode();
            var jda = event.getJDA();
            var guildOwner = guild.getOwner();
            if (guildOwner == null) return;
            var myGuildOwnerUser = new MyUser(guildOwner.getUser());

            // AutoRole
            if (myGuild.categoryIsEnabled("moderation") && myGuild.pluginIsEnabled("autorole") && myGuild.hasAutorole()) {
                var autoRoles = myGuild.getAutoRoles();
                if (autoRoles != null) {
                    for (var autoRole : autoRoles.entrySet()) {
                        var role = autoRole.getKey();
                        var delay = TimeUtil.minutesToMillis(autoRole.getValue());
                        if (selfMember.hasPermission(Permission.MANAGE_ROLES) && role != null && selfMember.canInteract(role))
                            new Timer().schedule(TimeUtil.wrap(() -> guild.addRoleToMember(member, role).queue(success -> { /* ignored */ }, failure -> { /* ignored */ })), delay);
                    }
                }
            }

            // Join
            if (myGuild.pluginIsEnabled("join") && myGuild.categoryIsEnabled("moderation")) {
                var joinTc = myGuild.getJoinTc();

                if (joinTc != null && joinTc.canTalk(selfMember)) {
                    var description = myGuild.getJoinMessage();
                    joinTc.sendMessage(
                            new EmbedBuilder()
                                    .setColor(Color.decode(myUser.getColorCode()))
                                    .setAuthor(String.format(LanguageHandler.get(lang, "join_author"), user.getName(), guild.getName()), null, guild.getIconUrl())
                                    .setDescription(description == null ? LanguageHandler.get(lang, "join_embeddescription") : description)
                                    .setThumbnail(user.getEffectiveAvatarUrl())
                                    .setFooter(LanguageHandler.get(lang, "join_footer"), ImageUtil.getUrl(event.getJDA(), "clock"))
                                    .setTimestamp(Instant.now())
                                    .build()
                    ).queue();
                }
            }

            // Log
            if (myGuild.pluginIsEnabled("log") && myGuild.categoryIsEnabled("moderation") && myGuild.logIsEnabled("user_join")) {
                var logChannel = guild.getTextChannelById(myGuild.getLogChannelId());
                if (logChannel != null) {
                    logChannel.sendMessage(
                            new EmbedBuilder()
                                    .setColor(myGuildOwnerUser.getColor())
                                    .setTitle(LanguageHandler.get(lang, "log_user_join_title"))
                                    .addField(LanguageHandler.get(lang, "user"), event.getMember().getUser().getName() + "#" + event.getMember().getUser().getDiscriminator(), true)
                                    .setFooter(LanguageHandler.get(lang, "log_at"), ImageUtil.getUrl(jda, "clock"))
                                    .setTimestamp(Instant.now())
                                    .build()
                    ).queue();
                }
            }
        }, Servant.fixedThreadPool);
    }
}
