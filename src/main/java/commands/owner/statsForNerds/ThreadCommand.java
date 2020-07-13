// Author: Tancred423 (https://github.com/Tancred423)
package commands.owner.statsForNerds;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import servant.MyUser;
import servant.Servant;
import utilities.Constants;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.awt.*;
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
        this.modCommand = false;
        this.cooldown = Constants.OWNER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var lang = LanguageHandler.getLanguage(event);

        var fixedThreadPool = new ThreadPoolDescription(Servant.fixedThreadPool.toString());
        var cachedThreadPool = new ThreadPoolDescription(Servant.cachedThreadPool.toString());
        var remindMeService = new ThreadPoolDescription(Servant.scheduledService.toString());
        var periodService = new ThreadPoolDescription(Servant.periodService.toString());

        event.reply(new EmbedBuilder()
                .setColor(Color.decode(new MyUser(event.getAuthor()).getColorCode()))
                .setTitle(LanguageHandler.get(lang, "thread_title"))

                .addField(LanguageHandler.get(lang, "thread_stats_k"),
                        String.format(
                                LanguageHandler.get(lang, "thread_stats_v"),
                                Runtime.getRuntime().availableProcessors(),
                                ManagementFactory.getThreadMXBean().getThreadCount()
                        ), true)

                .addField(LanguageHandler.get(lang, "thread_fixed_k"),
                        String.format(
                                LanguageHandler.get(lang, "thread_pool_service_v"),
                                fixedThreadPool.isRunning(),
                                fixedThreadPool.getPoolSize(),
                                fixedThreadPool.getActiveThreads(),
                                fixedThreadPool.getQueuedTasks(),
                                fixedThreadPool.getCompletedTasks()
                        ), true)

                .addField(LanguageHandler.get(lang, "thread_cached_k"),
                        String.format(
                                LanguageHandler.get(lang, "thread_pool_service_v"),
                                cachedThreadPool.isRunning(),
                                cachedThreadPool.getPoolSize(),
                                cachedThreadPool.getActiveThreads(),
                                cachedThreadPool.getQueuedTasks(),
                                cachedThreadPool.getCompletedTasks()
                        ), true)

                .addField(LanguageHandler.get(lang, "thread_database_k"),
                        String.format(
                                LanguageHandler.get(lang, "thread_database_v"),
                                Servant.db.getHikari().getHikariPoolMXBean().getActiveConnections(),
                                Servant.db.getHikari().getHikariPoolMXBean().getTotalConnections(),
                                Servant.db.getHikari().getMaximumPoolSize()
                        ), true)

                .addField(LanguageHandler.get(lang, "thread_scheduled_k"),
                        String.format(
                                LanguageHandler.get(lang, "thread_pool_service_v"),
                                remindMeService.isRunning(),
                                remindMeService.getPoolSize(),
                                remindMeService.getActiveThreads(),
                                remindMeService.getQueuedTasks(),
                                remindMeService.getCompletedTasks()
                        ), true)

                .addField(LanguageHandler.get(lang, "thread_period_k"),
                        String.format(
                                LanguageHandler.get(lang, "thread_pool_service_v"),
                                periodService.isRunning(),
                                periodService.getPoolSize(),
                                periodService.getActiveThreads(),
                                periodService.getQueuedTasks(),
                                periodService.getCompletedTasks()
                        ), true)

                .addField(LanguageHandler.get(lang, "thread_msg_cache_k"), String.format(LanguageHandler.get(lang, "thread_cache_v"), Servant.myMessageCache.size()), true)

                .addField(LanguageHandler.get(lang, "thread_deleted_msg_cache_k"), String.format(LanguageHandler.get(lang, "thread_cache_v"), Servant.myDeletedMessageCache.size()), true)
                .build()
        );
    }
}
