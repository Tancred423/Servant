// Author: Tancred423 (https://github.com/Tancred423)
package moderation.birthday;

import moderation.guild.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import utilities.Time;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BirthdayListener extends ListenerAdapter {
    public void onGuildMessageDelete(@NotNull GuildMessageDeleteEvent event) {
        CompletableFuture.runAsync(() -> {
            var guild = event.getGuild();
            var internalGuild = new Guild(guild.getIdLong());
            var guildOwner = guild.getOwner();
            if (guildOwner == null) return; // todo: always null?
            var guildOwnerUser = guildOwner.getUser();
            if (internalGuild.getBirthdayMessageMessageId(guild, guildOwnerUser) == event.getMessageIdLong())
                internalGuild.unsetBirthdayMessage(guild, guildOwnerUser);
        });
    }

    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        CompletableFuture.runAsync(() -> {
            var guild = event.getGuild();
            var guildOwner = guild.getOwner();
            if (guildOwner == null) return; // todo: always null?
            new Guild(guild.getIdLong()).purgeBirthday(guild, guildOwner.getUser());
        });
    }

    public void onReady(@NotNull ReadyEvent event) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> {
            BirthdayHandler.checkBirthdays(event.getJDA());
            BirthdayHandler.updateLists(event.getJDA());
        }, Time.getDelayToNextQuarterInMillis(), 15 * 60 * 1000, TimeUnit.MILLISECONDS); // 15 minute period
    }
}
