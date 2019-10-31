// Author: Tancred423 (https://github.com/Tancred423)
package moderation.guild;

import servant.Servant;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

public class GuildHandler {
    public static String getPrefix(CommandEvent event) {
        if (event.getGuild() != null) return new Guild(event.getGuild().getIdLong()).getPrefix(event.getGuild(), event.getAuthor());
        else return Servant.config.getDefaultPrefix();
    }
}
