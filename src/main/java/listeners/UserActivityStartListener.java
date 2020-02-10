package listeners;

import moderation.guild.Server;
import moderation.livestream.Livestream;
import moderation.user.Master;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import owner.blacklist.Blacklist;
import servant.Servant;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class UserActivityStartListener extends ListenerAdapter {
    // This event will be thrown if a user starts a new activity.
    public void onUserActivityStart(@Nonnull UserActivityStartEvent event) {
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
            var newActivity = event.getNewActivity();
            var lang = new Server(guild).getLanguage();

            // Livestream
            processLivestream(event, guild, user, newActivity, lang);
        }, Servant.fixedThreadPool);
    }

    private static void processLivestream(UserActivityStartEvent event, net.dv8tion.jda.api.entities.Guild guild, User user, Activity newActivity, String lang) {
        // User can hide themselves
        if (new Master(user).isStreamHidden(event.getGuild().getIdLong())) return;

        // Check if user is streamer if guild is in streamer mode.
        var isStreamerMode = new Server(guild).isStreamerMode();
        if (isStreamerMode)
            if (!new Server(guild).getStreamers().contains(user.getIdLong())) return;

        if (newActivity.getType().name().equalsIgnoreCase("streaming")) {
            Livestream.sendNotification(user, newActivity, guild, new Server(guild), isStreamerMode, lang);
            Livestream.addRole(guild, event.getMember(), guild.getRoleById(new Server(guild).getStreamingRoleId()));
        } else {
            Livestream.removeRole(guild, event.getMember(), guild.getRoleById(new Server(guild).getStreamingRoleId()));
        }
    }
}
