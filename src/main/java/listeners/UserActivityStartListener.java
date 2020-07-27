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
import utilities.Console;
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
        if (user.isBot() && user.getIdLong() != Constants.STREAM_TEST_BOT_ID) return;
        if (Blacklist.isBlacklisted(guild, user)) return;

//        Console.log("ActivityStart: \"" + user.getName() + "\" & \"" + guild.getName() + "\" NOT blacklisted!");

        CompletableFuture.runAsync(() -> {
            var newActivity = event.getNewActivity();
            var lang = new MyGuild(guild).getLanguageCode();
            var myGuild = new MyGuild(guild);

            // Livestream
            if (myGuild.pluginIsEnabled("livestream") && myGuild.categoryIsEnabled("moderation")) {
//                Console.log("ActivityStart: Plugin & Activity for guild \"" + guild.getName() + "\" enabled!");
                processLivestream(event, guild, user, newActivity, lang);
            }
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

//        Console.log("ActivityStart: \"" + guild.getName() + "\" is public: " + isPublic);
//        Console.log("ActivityStart: \"" + user.getName() + "\" is streamer: " + isStreamer);

        var isStreaming= event.getMember().getActivities().stream().map(Activity::getType).anyMatch(it -> it == Activity.ActivityType.STREAMING);
        var isActiveStreamer = LivestreamHandler.activeStreamerIds.contains(user.getIdLong());

//        Console.log("ActivityStart: \"" + user.getName() + "\" is now streaming: " + isStreaming);
//        Console.log("ActivityStart: \"" + user.getName() + "\" is already an active streamer: " + isActiveStreamer);

        if (isStreaming && !isActiveStreamer) {
            if (isStreamer) {
                Console.log("ActivityStart: Sending notification for \"" + user.getName() + "\" in \"" + guild.getName() + "\"!");
                LivestreamHandler.sendNotification(user, newActivity, guild, new MyGuild(guild), lang);
            }
            Console.log("ActivityStart: Adding role for \"" + user.getName() + "\" in \"" + guild.getName() + "\"!");
            LivestreamHandler.addRole(guild, event.getMember(), guild.getRoleById(new MyGuild(guild).getStreamRoleId()));

            // Tracker
            Console.log("ActivityStart: Adding active streamer for \"" + user.getName() + "\"!");
            LivestreamHandler.activeStreamerIds.add(user.getIdLong());
//            Console.log("Current active streamers: " + LivestreamHandler.activeStreamerIds);
        }
    }
}
