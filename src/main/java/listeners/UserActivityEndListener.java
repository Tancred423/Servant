package listeners;

import commands.owner.blacklist.Blacklist;
import plugins.moderation.livestream.LivestreamHandler;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.user.UserActivityEndEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import servant.MyGuild;
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
        if (Blacklist.isBlacklisted(guild, user)) return;

        CompletableFuture.runAsync(() -> {
            var oldActivity = event.getOldActivity();
            var myGuild = new MyGuild(guild);

            // Livestream
            if (myGuild.pluginIsEnabled("livestream") && myGuild.categoryIsEnabled("moderation"))
                processLivestream(event, guild, user, oldActivity);
        }, Servant.fixedThreadPool);
    }

    private static void processLivestream(UserActivityEndEvent event, net.dv8tion.jda.api.entities.Guild guild, User user, Activity oldActivity) {
        // Check if user is streamer if guild is in streamer mode.
        var isPublic = new MyGuild(guild).streamIsPublic();
        if (!isPublic)
            if (!new MyGuild(guild).getStreamerRoles().contains(user.getIdLong())) return;

        if (oldActivity.getType().name().equalsIgnoreCase("streaming")) {
            LivestreamHandler.removeRole(guild, event.getMember(), guild.getRoleById(new MyGuild(guild).getStreamRoleId()));
        }
    }
}
