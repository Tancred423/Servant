// Author: Tancred423 (https://github.com/Tancred423)
package moderation.join;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.toggle.Toggle;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import utilities.Image;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.CompletableFuture;

public class JoinListener extends ListenerAdapter {
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        if (event.getGuild().getIdLong() == 264445053596991498L) return; // Discord Bot List
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, "join")) return;

            var joinedUser = event.getUser();
            var guild = event.getGuild();
            var internalGuild = new moderation.guild.Guild(guild.getIdLong());
            var lang = new Guild(event.getGuild().getIdLong()).getLanguage(guild, joinedUser);
            var channel = internalGuild.getJoinNotifierChannel(guild, joinedUser);

            if (channel != null) channel.sendMessage(getEmbed(joinedUser, guild, internalGuild, lang)).queue();
        });
    }

    private MessageEmbed getEmbed(User joinedUser, net.dv8tion.jda.api.entities.Guild guild, Guild internalGuild, String lang) {
        var eb = new EmbedBuilder();
        var internalJoinedUser = new moderation.user.User(joinedUser.getIdLong());
        eb.setColor(internalJoinedUser.getColor(guild, joinedUser));
        eb.setAuthor(String.format(LanguageHandler.get(lang, "join_author"), joinedUser.getName(), joinedUser.getDiscriminator(), guild.getName()), null, guild.getIconUrl());
        eb.setDescription(LanguageHandler.get(lang, "join_embeddescription"));
        eb.setThumbnail(joinedUser.getEffectiveAvatarUrl());
        eb.setFooter(LanguageHandler.get(lang, "join_footer"), Image.getImageUrl("clock", guild, joinedUser));
        eb.setTimestamp(OffsetDateTime.now(ZoneOffset.of(internalGuild.getOffset(guild, joinedUser))));
        return eb.build();
    }
}
