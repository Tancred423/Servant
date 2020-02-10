// Author: Tancred423 (https://github.com/Tancred423)
package owner.statsForNerds;

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
        this.aliases = new String[]{ "threads" };
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
        var fixedThreadPool = new ThreadPoolDescription(Servant.fixedThreadPool.toString());
        var cachedThreadPool = new ThreadPoolDescription(Servant.cachedThreadPool.toString());
        var remindMeService = new ThreadPoolDescription(Servant.remindMeService.toString());
        var periodService = new ThreadPoolDescription(Servant.periodService.toString());

        event.reply(new EmbedBuilder()
                .setColor(new Master(event.getAuthor()).getColor())
                .setTitle("Stats for Nerds")
                .addField("General Stats", "Available Processors: " + Runtime.getRuntime().availableProcessors() +
                        "\nCurrent Threads: " + ManagementFactory.getThreadMXBean().getThreadCount(), true)

                .addField("Fixed Thread Pool", "Is Running: " + fixedThreadPool.isRunning() +
                        "\nPool Size: " + fixedThreadPool.getPoolSize() +
                        "\nActive Threads: " + fixedThreadPool.getActiveThreads() +
                        "\nQueued Tasks: " + fixedThreadPool.getQueuedTasks() +
                        "\nCompleted Tasks: " + fixedThreadPool.getCompletedTasks(), true)
                .addField("Cached Thread Pool", "Is Running: " + cachedThreadPool.isRunning() +
                        "\nPool Size: " + cachedThreadPool.getPoolSize() +
                        "\nActive Threads: " + cachedThreadPool.getActiveThreads() +
                        "\nQueued Tasks: " + cachedThreadPool.getQueuedTasks() +
                        "\nCompleted Tasks: " + cachedThreadPool.getCompletedTasks(), true)

                .addField("Database", "Active Connections: " + Servant.db.getHikari().getHikariPoolMXBean().getActiveConnections() +
                        "\nTotal Connections: " + Servant.db.getHikari().getHikariPoolMXBean().getTotalConnections() +
                        "\nMaximum Pool Size: " + Servant.db.getHikari().getMaximumPoolSize(), true)

                .addField("RemindMe Service", "Is Running: " + remindMeService.isRunning() +
                        "\nPool Size: " + remindMeService.getPoolSize() +
                        "\nActive Threads: " + remindMeService.getActiveThreads() +
                        "\nQueued Tasks: " + remindMeService.getQueuedTasks() +
                        "\nCompleted Tasks: " + remindMeService.getCompletedTasks(), true)
                .addField("Period Service", "Is Running: " + periodService.isRunning() +
                        "\nPool Size: " + periodService.getPoolSize() +
                        "\nActive Threads: " + periodService.getActiveThreads() +
                        "\nQueued Tasks: " + periodService.getQueuedTasks() +
                        "\nCompleted Tasks: " + periodService.getCompletedTasks(), true)
                .build()
        );
    }
}
