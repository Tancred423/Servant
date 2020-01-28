// Modified by: Tancred423 (https://github.com/Tancred423)
package information;

import net.dv8tion.jda.api.Permission;
import utilities.Constants;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;
import zJdaUtilsLib.com.jagrosh.jdautilities.examples.doc.Author;

import java.time.temporal.ChronoUnit;

@Author("John Grosh (jagrosh)")
public class PingCommand extends Command {
    public PingCommand() {
        this.name = "ping";
        this.aliases = new String[] { "pong" };
        this.help = "Bot's latency.";
        this.category = new Category("Information");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply("Ping: ...", message -> {
            var ping = event.getMessage().getTimeCreated().until(message.getTimeCreated(), ChronoUnit.MILLIS);
            message.editMessage("Ping: " + ping + "ms | Websocket: " + event.getJDA().getGatewayPing() + "ms").queue();
        });
    }
}
