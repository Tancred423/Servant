// Author: Tancred423 (https://github.com/Tancred423)
package moderation.leave;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.toggle.Toggle;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import utilities.Image;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.CompletableFuture;

public class LeaveListener extends ListenerAdapter {
    public void onGuildMemberLeave(@NotNull GuildMemberLeaveEvent event) {
        if (event.getGuild().getIdLong() == 264445053596991498L) return; // Discord Bot List
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, "leave")) return;

            var leftUser = event.getUser();
            var guild = event.getGuild();
            var internalGuild = new moderation.guild.Guild(guild.getIdLong());
            var lang = new Guild(event.getGuild().getIdLong()).getLanguage(guild, leftUser);
            var channel = internalGuild.getLeaveNotifierChannel(guild, leftUser);

            if (channel != null) channel.sendMessage(getEmbed(leftUser, guild, internalGuild, lang)).queue();
        });
    }

    private MessageEmbed getEmbed(User leftUser, net.dv8tion.jda.api.entities.Guild guild, Guild internalGuild, String lang) {
        var eb = new EmbedBuilder();
        var internalLeftUser = new moderation.user.User(leftUser.getIdLong());
        eb.setColor(internalLeftUser.getColor(guild, leftUser));
        eb.setAuthor(String.format(LanguageHandler.get(lang, "leave_author"), leftUser.getName(), leftUser.getDiscriminator()), null, guild.getIconUrl());
        eb.setDescription(LanguageHandler.get(lang, "leave_embeddescription"));
        eb.setThumbnail(leftUser.getEffectiveAvatarUrl());
        eb.setFooter(LanguageHandler.get(lang, "leave_footer"), Image.getImageUrl("clock", guild, leftUser));
        eb.setTimestamp(OffsetDateTime.now(ZoneOffset.of(internalGuild.getOffset(guild, leftUser))));
        return eb.build();
    }
}
