// Author: Tancred423 (https://github.com/Tancred423)
package moderation.join;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.toggle.Toggle;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import servant.Log;
import servant.Servant;
import utilities.Image;

import java.awt.*;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class JoinListener extends ListenerAdapter {
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (!Toggle.isEnabled(event, "join")) return;
        String lang;
        try {
            lang = new Guild(event.getGuild().getIdLong()).getLanguage();
        } catch (SQLException e) {
            lang = Servant.config.getDefaultLanguage();
        }

        var joinedUser = event.getUser();
        var guild = event.getGuild();
        var internalGuild = new moderation.guild.Guild(guild.getIdLong());

        MessageChannel channel;
        try {
            channel = internalGuild.getJoinNotifierChannel();
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getUser(), "join", null).sendLog(false);
            return;
        }

        if (channel != null) channel.sendMessage(getEmbed(joinedUser, guild, internalGuild, lang)).queue();
    }

    private MessageEmbed getEmbed(User joinedUser, net.dv8tion.jda.core.entities.Guild guild, Guild internalGuild, String lang) {
        var eb = new EmbedBuilder();
        var internalJoinedUser = new moderation.user.User(joinedUser.getIdLong());
        try {
            eb.setColor(internalJoinedUser.getColor());
        } catch (SQLException e) {
            eb.setColor(Color.decode(Servant.config.getDefaultColorCode()));
        }
        eb.setAuthor(String.format(LanguageHandler.get(lang, "join_author"), joinedUser.getName(), joinedUser.getDiscriminator(), guild.getName()), null, guild.getIconUrl());
        eb.setDescription(LanguageHandler.get(lang, "join_embeddescription"));
        eb.setThumbnail(joinedUser.getEffectiveAvatarUrl());
        eb.setFooter(LanguageHandler.get(lang, "join_footer"), Image.getImageUrl("clock"));
        try {
            eb.setTimestamp(OffsetDateTime.now(ZoneOffset.of(internalGuild.getOffset())));
        } catch (SQLException e) {
            eb.setTimestamp(OffsetDateTime.now(ZoneOffset.UTC));
        }
        return eb.build();
    }
}
