// Author: Tancred423 (https://github.com/Tancred423)
package moderation.guild;

import servant.Log;
import servant.Servant;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;

public class GuildHandler {
    public static String getPrefix(CommandEvent event, String name) {
        if (event.getGuild() != null) {
            try {
                return new Guild(event.getGuild().getIdLong()).getPrefix();
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
            }
        }
        return Servant.config.getDefaultPrefix();
    }
}
