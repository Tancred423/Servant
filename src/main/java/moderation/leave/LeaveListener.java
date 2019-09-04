// Author: Tancred423 (https://github.com/Tancred423)
package moderation.leave;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.toggle.Toggle;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import servant.Log;
import servant.Servant;

import java.sql.SQLException;

public class LeaveListener extends ListenerAdapter {
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
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

        if (channel != null)
            channel.sendMessage(
                    String.format(LanguageHandler.get(lang, "leave_left"), leftUser.getName(), leftUser.getDiscriminator(), guild.getName())
            ).queue();
    }
}
