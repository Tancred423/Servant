// Author: Tancred423 (https://github.com/Tancred423)
package listeners;

import files.language.LanguageHandler;
import moderation.guild.Server;
import moderation.toggle.Toggle;
import moderation.user.Master;
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
            var server = new Server(guild);
            var master = new Master(user);
            var member = event.getMember();
            var selfMember = guild.getMemberById(event.getJDA().getSelfUser().getIdLong());
            if (selfMember == null) return; // To eliminate errors. Will never occur.

            // AutoRole
            if (Toggle.isEnabled(event, "autorole") && server.hasAutorole()) {
                var roleAndDelay = server.getAutorole(); // Map.Entry<Role, Integer>
                if (roleAndDelay != null) {
                    var role = roleAndDelay.getKey();
                    var delay = roleAndDelay.getValue() * 60 * 1000; // Milliseconds
                    if (selfMember.hasPermission(Permission.MANAGE_ROLES) && role != null && selfMember.canInteract(role))
                        new Timer().schedule(Time.wrap(() -> guild.addRoleToMember(member, role).queue(success -> { /* ignored */ }, failure -> { /* ignored */ })), delay);
                }
            }

            // Join
            if (Toggle.isEnabled(event, "join")) {
                var lang = new Server(event.getGuild()).getLanguage();
                var joinNotifierChannel = server.getJoinNotifierChannel();

                if (joinNotifierChannel != null && joinNotifierChannel.canTalk(selfMember)) {
                    var description = server.getJoinMessage();
                    joinNotifierChannel.sendMessage(
                            new EmbedBuilder()
                                    .setColor(master.getColor())
                                    .setAuthor(String.format(LanguageHandler.get(lang, "join_author"), user.getName(), user.getDiscriminator(), guild.getName()), null, guild.getIconUrl())
                                    .setDescription(description == null ? LanguageHandler.get(lang, "join_embeddescription") : description)
                                    .setThumbnail(user.getEffectiveAvatarUrl())
                                    .setFooter(LanguageHandler.get(lang, "join_footer"), Image.getImageUrl("clock", guild, user))
                                    .setTimestamp(OffsetDateTime.now(ZoneOffset.of(server.getOffset())))
                                    .build()
                    ).queue();
                }
            }
        }, Servant.threadPool);
    }
}
