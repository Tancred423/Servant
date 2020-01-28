// Author: Tancred423 (https://github.com/Tancred423)
package owner;

import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import servant.Servant;
import utilities.Constants;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.lang.management.ManagementFactory;

public class ThreadCommand extends Command {
    public ThreadCommand() {
        this.name = "thread";
        this.aliases = new String[]{ "threads", "connection", "connections" };
        this.help = "Shows current thread.";
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
        event.reply(new EmbedBuilder()
                .setColor(new Master(event.getAuthor()).getColor())
                .setTitle("Stats for Nerds")
                .addField("Current Threads", String.valueOf(ManagementFactory.getThreadMXBean().getThreadCount()), true)
                .addField("Available Processors", String.valueOf(Runtime.getRuntime().availableProcessors()), true)
                .addField("Fixed Thread Pool", Servant.threadPool.toString(), false)
                .addField("Cached Thread Pool", Servant.profilePool.toString(), false)
                .addField("Hikari", "Active Connections: " + Servant.db.getHikari().getHikariPoolMXBean().getActiveConnections() +
                        "\nTotal Connections: " + Servant.db.getHikari().getHikariPoolMXBean().getTotalConnections() +
                        "\nMaximum Pool Size: " + Servant.db.getHikari().getMaximumPoolSize(), false)
                .build()
        );
    }
}
