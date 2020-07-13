// Modified by: Tancred423 (https://github.com/Tancred423)
package commands.standard.ping;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import servant.MyUser;
import utilities.Constants;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;
import zJdaUtilsLib.com.jagrosh.jdautilities.examples.doc.Author;

import java.awt.*;
import java.time.temporal.ChronoUnit;

@Author("John Grosh (jagrosh)")
public class PingCommand extends Command {
    public PingCommand() {
        this.name = "ping";
        this.aliases = new String[] { "pong", "latency" };
        this.help = "Servant's ping and API latency";
        this.category = new Category("Standard");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.modCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var eb = new EmbedBuilder()
                .setColor(Color.decode(new MyUser(event.getAuthor()).getColorCode()))
                .setDescription("Ping: ...");
        event.reply(eb.build(), message -> {
            var ping = event.getMessage().getTimeCreated().until(message.getTimeCreated(), ChronoUnit.MILLIS);
            var membed = message.getEmbeds().get(0);
            var ueb = new EmbedBuilder()
                    .setColor(membed.getColor())
                    .setDescription("Ping: " + ping + "ms | Websocket: " + event.getJDA().getGatewayPing() + "ms");
            message.editMessage(ueb.build()).queue();
        });
    }
}
