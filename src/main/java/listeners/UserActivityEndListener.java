package listeners;

import moderation.guild.Server;
import moderation.livestream.Livestream;
import moderation.user.Master;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.user.UserActivityEndEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import owner.blacklist.Blacklist;
import servant.Servant;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class UserActivityEndListener extends ListenerAdapter {
    // This event will be thrown if a user ends an activity.
    public void onUserActivityEnd(@Nonnull UserActivityEndEvent event) {
        var guild = event.getGuild();
        var user = event.getUser();

        /* Certain conditions must meet, so this event is allowed to be executed:
         * 1.   Ignore any request from the Discord Bot List as this big guild
         *      invoke a lot of events, but never use this bot actively.
         * 2.   Ignore any request from bots to prevent infinite loops.
         * 3.   Ignore any request from blacklisted users and guilds.
         */
        if (guild.getIdLong() == 264445053596991498L) return; // Discord Bot List
        if (user.isBot()) return;
        if (Blacklist.isBlacklisted(user, guild)) return;

        CompletableFuture.runAsync(() -> {
            var oldActivity = event.getOldActivity();

            // Livestream
            processLivestream(event, guild, user, oldActivity);
        }, Servant.threadPool);
    }

    private static void processLivestream(UserActivityEndEvent event, net.dv8tion.jda.api.entities.Guild guild, User user, Activity oldActivity) {
        // Users can hide themselves from this feature.
        if (new Master(user).isStreamHidden(guild.getIdLong())) return;

        // Check if user is streamer if guild is in streamer mode.
        var isStreamerMode = new Server(guild).isStreamerMode();
        if (isStreamerMode)
            if (!new Server(guild).getStreamers().contains(user.getIdLong())) return;

        if (oldActivity.getType().name().equalsIgnoreCase("streaming")) {
            Livestream.removeRole(guild, event.getMember(), guild.getRoleById(new Server(guild).getStreamingRoleId()));
        }
    }
}
