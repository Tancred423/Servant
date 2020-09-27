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
        var messageEmbed = new EmbedBuilder()
                .setColor(Color.decode(new MyUser(event.getAuthor()).getColorCode()))
                .setDescription("Measuring ...")
                .build();

        event.reply(messageEmbed, message -> {
            var servantPing = event.getMessage().getTimeCreated().until(message.getTimeCreated(), ChronoUnit.MILLIS);
            var webSocketPing = event.getJDA().getGatewayPing();
            var sm = event.getJDA().getShardManager();
            var avgWebSocketPing = sm == null ? 0 : (int) Math.round(sm.getAverageGatewayPing());
            event.getJDA().getRestPing().queue(restPing -> {
                var membed = message.getEmbeds().get(0);
                var messageEmbedEdited = new EmbedBuilder()
                        .setColor(membed.getColor())
                        .addField("Servant Ping: " + servantPing + " ms", "The time that Servant took to respond to your ping command.", false)
                        .addField("REST Ping: " + restPing + " ms", "The time that Discord took to respond to an API request.", false)
                        .addField("WebSocket Ping (Shard: " + event.getJDA().getShardInfo().getShardId() + "): " + webSocketPing + " ms", "The time that Discord took to respond to the current shard's last heartbeat.", false)
                        .addField("Avg. WebSocket Ping (All " + (sm == null ? "" : sm.getShardsTotal()) + " shards): " + avgWebSocketPing + " ms", "The average time that Discord took to respond to all shard's last heatbeats.", false)
                        .build();

                message.editMessage(messageEmbedEdited).queue();
            });
        });
    }
}
