package listeners;

import commands.owner.blacklist.Blacklist;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import plugins.moderation.livestream.LivestreamHandler;
import servant.MyGuild;
import servant.MyUser;
import servant.Servant;
import utilities.Constants;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class UserActivityStartListener extends ListenerAdapter {
    // This event will be thrown if a user starts a new activity.
    public void onUserActivityStart(@Nonnull UserActivityStartEvent event) {
        var guild = event.getGuild();
        var user = event.getUser();

        // Blacklist
        if (guild.getIdLong() == Constants.DISCORD_BOT_LIST_ID) return;
        if (user.isBot()) return;
        if (Blacklist.isBlacklisted(guild, user)) return;

        CompletableFuture.runAsync(() -> {
            var newActivity = event.getNewActivity();
            var lang = new MyGuild(guild).getLanguageCode();
            var myGuild = new MyGuild(guild);

            // Livestream
            if (myGuild.pluginIsEnabled("livestream") && myGuild.categoryIsEnabled("moderation"))
                processLivestream(event, guild, user, newActivity, lang);
        }, Servant.fixedThreadPool);
    }

    private static void processLivestream(UserActivityStartEvent event, net.dv8tion.jda.api.entities.Guild guild, User user, Activity newActivity, String lang) {
        // Check if user is streamer if guild is in streamer mode.
        var myGuild = new MyGuild(guild);
        var isPublic = myGuild.streamIsPublic();
        var myUser = new MyUser(user);
        var isStreamer = myUser.isStreamer(guild);

        // In streamer mode only streamers get a role and an announcement.
        // In public mode, only streamers get annoucements, but everyone is getting a role.
        if (!isPublic && !isStreamer) return;

        if (event.getMember().getActivities().stream().map(Activity::getType).anyMatch(it -> it == Activity.ActivityType.STREAMING)
            && !LivestreamHandler.activeStreamerIds.contains(user.getIdLong())) {
            if (isStreamer) LivestreamHandler.sendNotification(user, newActivity, guild, new MyGuild(guild), lang);
            LivestreamHandler.addRole(guild, event.getMember(), guild.getRoleById(new MyGuild(guild).getStreamRoleId()));

            // Tracker
            LivestreamHandler.activeStreamerIds.add(user.getIdLong());
        }
    }
}
