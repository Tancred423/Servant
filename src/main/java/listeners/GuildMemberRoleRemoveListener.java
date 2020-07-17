package listeners;

import commands.owner.blacklist.Blacklist;
import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import servant.MyGuild;
import servant.MyUser;
import servant.Servant;
import utilities.Constants;
import utilities.ImageUtil;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class GuildMemberRoleRemoveListener extends ListenerAdapter {
    // This event will be thrown if a user loses a role in a guild.
    public void onGuildMemberRoleRemove(@Nonnull GuildMemberRoleRemoveEvent event) {
        var guild = event.getGuild();
        var user = event.getUser();
        var jda = event.getJDA();

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

            // Log
            if (myGuild.pluginIsEnabled("log") && myGuild.categoryIsEnabled("moderation") && myGuild.logIsEnabled("role_remove")) {
                var logChannel = guild.getTextChannelById(myGuild.getLogChannelId());
                if (logChannel != null) {
                    var sb = new StringBuilder().append("\n");
                    var roles = event.getRoles();
                    for (var role : roles)
                        sb.append(role.getName()).append(" (").append(role.getIdLong()).append(")\n");

                    logChannel.sendMessage(
                            new EmbedBuilder()
                                    .setColor(myUser.getColor())
                                    .setTitle(LanguageHandler.get(lang, "log_role_remove_title"))
                                    .addField(LanguageHandler.get(lang, "member"), event.getMember().getUser().getName() + "#" + event.getMember().getUser().getDiscriminator(), false)
                                    .addField(LanguageHandler.get(lang, "role_s"), sb.toString(), false)
                                    .setFooter(LanguageHandler.get(lang, "log_at"), ImageUtil.getUrl(jda, "clock"))
                                    .setTimestamp(Instant.now())
                                    .build()
                    ).queue();
                }
            }

            // Patreon
            processPatreon(event);
        }, Servant.fixedThreadPool);
    }

    private static void processPatreon(GuildMemberRoleRemoveEvent event) {
        if (!event.getGuild().getId().equals(Servant.config.getSupportGuildId())) return;

        var supporterRole = event.getGuild().getRoleById(Constants.SUPPORTER_ROLE_ID);
        var receivedRoleId = event.getRoles().get(0).getIdLong();

        if (receivedRoleId == Constants.DONATOR_ROLE_ID) {
            if (supporterRole != null) event.getGuild().removeRoleFromMember(event.getMember(), supporterRole).queue();
        } else if (receivedRoleId == Constants.PATREON_ROLE_ID) {
            if (supporterRole != null) event.getGuild().removeRoleFromMember(event.getMember(), supporterRole).queue();
        } else if (receivedRoleId == Constants.BOOSTER_ROLE_ID) {
            if (supporterRole != null) event.getGuild().removeRoleFromMember(event.getMember(), supporterRole).queue();
        }
    }
}
