// Author: Tancred423 (https://github.com/Tancred423)
package moderation.birthday;

import moderation.guild.Guild;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import utilities.Time;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BirthdayListener extends ListenerAdapter {
    public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
        CompletableFuture.runAsync(() -> {
            var guild = event.getGuild();
            var internalGuild = new Guild(guild.getIdLong());
            var guildOwner = guild.getOwner().getUser();
            if (internalGuild.getBirthdayMessageMessageId(guild, guildOwner) == event.getMessageIdLong())
                internalGuild.unsetBirthdayMessage(guild, guildOwner);
        });
    }

    public void onGuildLeave(GuildLeaveEvent event) {
        CompletableFuture.runAsync(() -> {
            var guild = event.getGuild();
            new Guild(guild.getIdLong()).purgeBirthday(guild, guild.getOwner().getUser());
        });
    }

    public void onReady(ReadyEvent event) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> {
            BirthdayHandler.checkBirthdays(event.getJDA());
            BirthdayHandler.updateLists(event.getJDA());
        }, Time.getDelayToNextQuarterInMillis(), 15 * 60 * 1000, TimeUnit.MILLISECONDS); // 15 minute period
    }
}
