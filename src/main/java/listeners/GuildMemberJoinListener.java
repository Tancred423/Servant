// Author: Tancred423 (https://github.com/Tancred423)
package listeners;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import owner.blacklist.Blacklist;
import servant.Servant;
import utilities.Image;
import utilities.Time;

import javax.annotation.Nonnull;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Timer;
import java.util.concurrent.CompletableFuture;

public class GuildMemberJoinListener extends ListenerAdapter {
    // This event will be thrown if a user joins a guild the bot is on.
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
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

            // AutoRole
            if (Toggle.isEnabled(event, "autorole") && internalGuild.hasAutorole(guild, user)) {
                var roleAndDelay = internalGuild.getAutorole(guild, user); // Map.Entry<Role, Integer>
                if (roleAndDelay != null) {
                    var role = roleAndDelay.getKey();
                    var delay = roleAndDelay.getValue() * 60 * 1000; // Milliseconds
                    if (selfMember.hasPermission(Permission.MANAGE_ROLES) && role != null && selfMember.canInteract(role))
                        new Timer().schedule(Time.wrap(() -> guild.addRoleToMember(member, role).queue(success -> { /* ignored */ }, failure -> { /* ignored */ })), delay);
                }
            }

            // Join
            if (Toggle.isEnabled(event, "join")) {
                var lang = new Guild(event.getGuild().getIdLong()).getLanguage(guild, user);
                var joinNotifierChannel = internalGuild.getJoinNotifierChannel(guild, user);

                if (joinNotifierChannel != null && joinNotifierChannel.canTalk(selfMember)) {
                    // todo: customizable welcome messages
                    joinNotifierChannel.sendMessage(
                            new EmbedBuilder()
                                    .setColor(internalUser.getColor(guild, user))
                                    .setAuthor(String.format(LanguageHandler.get(lang, "join_author"), user.getName(), user.getDiscriminator(), guild.getName()), null, guild.getIconUrl())
                                    .setDescription(LanguageHandler.get(lang, "join_embeddescription"))
                                    .setThumbnail(user.getEffectiveAvatarUrl())
                                    .setFooter(LanguageHandler.get(lang, "join_footer"), Image.getImageUrl("clock", guild, user))
                                    .setTimestamp(OffsetDateTime.now(ZoneOffset.of(internalGuild.getOffset(guild, user))))
                                    .build()
                    ).queue();
                }
            }
        }, Servant.threadPool);
    }
}
