// Author: Tancred423 (https://github.com/Tancred423)
package moderation.leave;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.toggle.Toggle;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import servant.Log;
import servant.Servant;
import utilities.Image;

import java.awt.*;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.CompletableFuture;

public class LeaveListener extends ListenerAdapter {
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, "leave")) return;

            String lang;
            try {
                lang = new Guild(event.getGuild().getIdLong()).getLanguage();
            } catch (SQLException e) {
                lang = Servant.config.getDefaultLanguage();
            }

            var leftUser = event.getUser();
            var guild = event.getGuild();
            var internalGuild = new moderation.guild.Guild(guild.getIdLong());

            MessageChannel channel;
            try {
                channel = internalGuild.getLeaveNotifierChannel();
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getUser(), "leave", null).sendLog(false);
                return;
            }

            if (channel != null) channel.sendMessage(getEmbed(leftUser, guild, internalGuild, lang)).queue();
        });
    }

    private MessageEmbed getEmbed(User leftUser, net.dv8tion.jda.core.entities.Guild guild, Guild internalGuild, String lang) {
        var eb = new EmbedBuilder();
        var internalLeftUser = new moderation.user.User(leftUser.getIdLong());
        try {
            eb.setColor(internalLeftUser.getColor());
        } catch (SQLException e) {
            eb.setColor(Color.decode(Servant.config.getDefaultColorCode()));
        }
        eb.setAuthor(String.format(LanguageHandler.get(lang, "leave_author"), leftUser.getName(), leftUser.getDiscriminator()), null, guild.getIconUrl());
        eb.setDescription(LanguageHandler.get(lang, "leave_embeddescription"));
        eb.setThumbnail(leftUser.getEffectiveAvatarUrl());
        eb.setFooter(LanguageHandler.get(lang, "leave_footer"), Image.getImageUrl("clock"));
        try {
            eb.setTimestamp(OffsetDateTime.now(ZoneOffset.of(internalGuild.getOffset())));
        } catch (SQLException e) {
            eb.setTimestamp(OffsetDateTime.now(ZoneOffset.UTC));
        }
        return eb.build();
    }
}
