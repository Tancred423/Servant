// Modified by: Tancred423 (https://github.com/Tancred423)
package owner;

import moderation.guild.Guild;
import moderation.user.User;
import net.dv8tion.jda.core.Permission;
import servant.Log;
import utilities.Constants;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;
import zJdaUtilsLib.com.jagrosh.jdautilities.examples.doc.Author;

import java.sql.SQLException;

@Author("John Grosh (jagrosh)")
public class ShutdownCommand extends Command {
    public ShutdownCommand() {
        this.name = "shutdown";
        this.aliases = new String[0];
        this.help = "Shuts down bot.";
        this.category = new Category("Owner");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = true;
        this.cooldown = Constants.OWNER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[0];
    }

    @Override
    protected void execute(CommandEvent event) {
        // Statistics.
        try {
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            if (event.getGuild() != null) new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }

        event.reactSuccess();
        event.getJDA().shutdown();
    }
}
