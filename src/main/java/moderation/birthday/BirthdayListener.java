// Author: Tancred423 (https://github.com/Tancred423)
package moderation.birthday;

import moderation.guild.Guild;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import servant.Log;
import utilities.Time;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BirthdayListener extends ListenerAdapter {
    public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
        CompletableFuture.runAsync(() -> {
            var guild = event.getGuild();
            var internalGuild = new Guild(guild.getIdLong());
            try {
                if (internalGuild.getBirthdayMessageMessageId() == event.getMessageIdLong()) internalGuild.unsetBirthdayMessage();
            } catch (SQLException e) {
                new Log(e, guild, event.getJDA().getSelfUser(), "BirthdayListener - Message Delete", null).sendLog(false);
            }
        });
    }

    public void onGuildLeave(GuildLeaveEvent event) {
        CompletableFuture.runAsync(() -> {
            var guild = event.getGuild();
            var internalGuild = new Guild(guild.getIdLong());
            try {
                internalGuild.purgeBirthday();
            } catch (SQLException e) {
                new Log(e, guild, event.getJDA().getSelfUser(), "BirthdayListener - Guild Leave", null).sendLog(false);
            }
        });
    }

    public void onReady(ReadyEvent event) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> {
            try {
                BirthdayHandler.checkBirthdays(event.getJDA());
                BirthdayHandler.updateLists(event.getJDA());
            } catch (SQLException e) {
                new Log(e, null, event.getJDA().getSelfUser(), "BirthdayListener - Ready", null).sendLog(false);
            }
        }, Time.getDelayToNextQuarterInMillis(), 15 * 60 * 1000, TimeUnit.MILLISECONDS); // 15 minute period
    }
}
