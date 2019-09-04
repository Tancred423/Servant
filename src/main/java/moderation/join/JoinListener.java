// Author: Tancred423 (https://github.com/Tancred423)
package moderation.join;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.toggle.Toggle;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import servant.Log;
import servant.Servant;

import java.sql.SQLException;

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

        if (channel != null)
            channel.sendMessage(
                    String.format(LanguageHandler.get(lang, "leave_left"), joinedUser.getName(), joinedUser.getDiscriminator(), guild.getName())
            ).queue();
    }
}
