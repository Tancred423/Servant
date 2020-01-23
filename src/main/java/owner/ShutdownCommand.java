// Modified by: Tancred423 (https://github.com/Tancred423)
package owner;

import net.dv8tion.jda.api.Permission;
import utilities.Constants;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;
import zJdaUtilsLib.com.jagrosh.jdautilities.examples.doc.Author;

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
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reactSuccess();
        event.getJDA().shutdown();
    }
}
