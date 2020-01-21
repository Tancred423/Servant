// Author: Tancred423 (https://github.com/Tancred423)
package moderation.livestream;

import moderation.guild.Guild;
import moderation.toggle.Toggle;
import net.dv8tion.jda.api.events.user.UserActivityEndEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import owner.blacklist.Blacklist;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class LivestreamListener extends ListenerAdapter {
    public void onUserActivityStart(@Nonnull UserActivityStartEvent event) {
        if (event.getGuild().getIdLong() == 264445053596991498L) return; // Discord Bot List
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, "livestream")) return;

            var guild = event.getGuild();
            var author = event.getUser();
            var newActivity = event.getNewActivity();

            var lang = new Guild(guild.getIdLong()).getLanguage(guild, author);

            if (author.isBot()) return;
            if (Blacklist.isBlacklisted(event.getUser(), event.getGuild())) return;

            // Users can hide themselves from this feature.
            if (new moderation.user.User(author.getIdLong()).isStreamHidden(event.getGuild().getIdLong(), guild, author)) return;

            var isStreamerMode = new Guild(guild.getIdLong()).isStreamerMode(guild, author);

            // Check if user is streamer if guild is in streamer mode.
            if (isStreamerMode)
                if (!new Guild(guild.getIdLong()).getStreamers(guild, author).contains(author.getIdLong())) return;

            var type = newActivity.getType().name();

            if (type.equalsIgnoreCase("streaming")) {
                Livestream.sendNotification(author, newActivity, guild, new Guild(guild.getIdLong()), isStreamerMode, lang);
                Livestream.addRole(guild, event.getMember(), guild.getRoleById(new Guild(guild.getIdLong()).getStreamingRoleId(guild, author)));
            } else {
                Livestream.removeRole(guild, event.getMember(), guild.getRoleById(new Guild(guild.getIdLong()).getStreamingRoleId(guild, author)));
            }
        });
    }

    public void onUserActivityEnd(@Nonnull UserActivityEndEvent event) {
        if (event.getGuild().getIdLong() == 264445053596991498L) return; // Discord Bot List
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, "livestream")) return;

            var guild = event.getGuild();
            var author = event.getUser();
            var oldActivity = event.getOldActivity();

            if (author.isBot()) return;
            if (Blacklist.isBlacklisted(event.getUser(), event.getGuild())) return;

            // Users can hide themselves from this feature.
            if (new moderation.user.User(author.getIdLong()).isStreamHidden(event.getGuild().getIdLong(), guild, author)) return;

            var isStreamerMode = new Guild(guild.getIdLong()).isStreamerMode(guild, author);

            // Check if user is streamer if guild is in streamer mode.
            if (isStreamerMode)
                if (!new Guild(guild.getIdLong()).getStreamers(guild, author).contains(author.getIdLong())) return;

            var type = oldActivity.getType().name();

            if (type.equalsIgnoreCase("streaming")) {
                Livestream.removeRole(guild, event.getMember(), guild.getRoleById(new Guild(guild.getIdLong()).getStreamingRoleId(guild, author)));
            }
        });
    }
}
