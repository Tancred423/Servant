package listeners;

import commands.owner.blacklist.Blacklist;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.user.UserActivityEndEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import plugins.moderation.livestream.LivestreamHandler;
import plugins.moderation.livestream.Livestreamer;
import servant.MyGuild;
import servant.MyUser;
import servant.Servant;
import utilities.Constants;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class UserActivityEndListener extends ListenerAdapter {
    // This event will be thrown if a user ends an activity.
    public void onUserActivityEnd(@Nonnull UserActivityEndEvent event) {
        var guild = event.getGuild();
        var user = event.getUser();

        // Blacklist
        if (guild.getIdLong() == Constants.DISCORD_BOT_LIST_ID) return;
        if (user.isBot() && user.getIdLong() != Constants.STREAM_TEST_BOT_ID) return;
        if (Blacklist.isBlacklisted(guild, user)) return;

        CompletableFuture.runAsync(() -> {
            var myGuild = new MyGuild(guild);

            // Livestream
            if (myGuild.pluginIsEnabled("livestream") && myGuild.categoryIsEnabled("moderation")) {
                processLivestream(event, guild, user);
            }
        }, Servant.fixedThreadPool);
    }

    private static void processLivestream(UserActivityEndEvent event, net.dv8tion.jda.api.entities.Guild guild, User user) {
        // Check if user is streamer if guild is in streamer mode.
        var myGuild = new MyGuild(guild);
        var isPublic = myGuild.streamIsPublic();
        var myUser = new MyUser(user);
        var isStreamer = myUser.isStreamer(guild);

        // In streamer mode only streamers get the role removed.
        // In public mode everyone is getting the role removed.
        if (!isPublic && !isStreamer) return;

        var isNotStreamingAnymore = event.getMember().getActivities().stream().map(Activity::getType).noneMatch(it -> it == Activity.ActivityType.STREAMING);
        var isActiveStreamer = false;
        Livestreamer livestreamer = null;

        for (var activeStreamer : LivestreamHandler.activeStreamers) {
            if (activeStreamer.getUserId() == user.getIdLong() && activeStreamer.getGuildId() == guild.getIdLong()) {
                livestreamer = activeStreamer;
                isActiveStreamer = true;
                break;
            }
        }

        if (isNotStreamingAnymore && isActiveStreamer) {
            LivestreamHandler.removeRole(guild, event.getMember(), guild.getRoleById(new MyGuild(guild).getStreamRoleId()));

            // Tracker
            LivestreamHandler.activeStreamers.remove(livestreamer);
        }
    }
}
