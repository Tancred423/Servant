// Author: Tancred423 (https://github.com/Tancred423)
package moderation.toggle;

import moderation.guild.Server;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.user.UserActivityEndEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.util.ArrayList;
import java.util.List;

public class Toggle {
    public static boolean isEnabled(CommandEvent event, String name) {
        if (event.getGuild() == null) return true;
        return new Server(event.getGuild()).getToggleStatus(name);
    }

    public static boolean isEnabled(GuildMessageReactionAddEvent event, String name) {
        return new Server(event.getGuild()).getToggleStatus(name);
    }

    public static boolean isEnabled(GuildMemberJoinEvent event, String name) {
        return new Server(event.getGuild()).getToggleStatus(name);
    }

    public static boolean isEnabled(GuildMemberLeaveEvent event, String name) {
        return new Server(event.getGuild()).getToggleStatus(name);
    }

    public static boolean isEnabled(GuildVoiceJoinEvent event, String name) {
        return new Server(event.getGuild()).getToggleStatus(name);
    }

    public static boolean isEnabled(GuildVoiceMoveEvent event, String name) {
        return new Server(event.getGuild()).getToggleStatus(name);
    }

    public static boolean isEnabled(GuildVoiceLeaveEvent event, String name) {
        return new Server(event.getGuild()).getToggleStatus(name);
    }

    public static boolean isEnabled(GuildMessageReactionRemoveEvent event, String name) {
        return new Server(event.getGuild()).getToggleStatus(name);
    }

    public static boolean isEnabled(GuildMessageReceivedEvent event, String name) {
        return new Server(event.getGuild()).getToggleStatus(name);
    }

    public static boolean isEnabled(MessageReceivedEvent event, String name) {
        return new Server(event.getGuild()).getToggleStatus(name);
    }

    public static boolean isEnabled(UserActivityStartEvent event, String name) {
        return new Server(event.getGuild()).getToggleStatus(name);
    }

    public static boolean isEnabled(UserActivityEndEvent event, String name) {
        return new Server(event.getGuild()).getToggleStatus(name);
    }

    public static String getAlias(String arg) {
        switch (arg.toLowerCase()) {
            case "all":
            case "everything":
            case "anything":
                return "all";

            // moderation
            case "autorole":
                return "autorole";
            case "bestofimage":
            case "image":
                return "bestofimage";
            case "bestofquote":
            case "quote":
                return "bestofquote";
            case "birthday":
            case "bday":
                return "birthday";
            case "clear":
            case "clean":
            case "remove":
            case "delete":
            case "purge":
                return "clear";
            case "join":
                return "join";
            case "leave":
                return "leave";
            case "levelrole":
            case "rankrole":
                return "levelrole";
            case "livestream":
            case "stream":
                return "livestream";
            case "mediaonlychannel":
            case "mediaonly":
                return "mediaonlychannel";
            case "reactionrole":
                return "reactionrole";
            case "role":
                return "role";
            case "server":
            case "guild":
                return "server";
            case "serversetup":
            case "setup":
                return "serversetup";
            case "user":
                return "user";
            case "voicelobby":
            case "lobby":
                return "voicelobby";

            // information
            case "botinfo":
            case "about":
                return "botinfo";
            case "ping":
            case "pong":
                return "ping";
            case "serverinfo":
            case "guildinfo":
                return "serverinfo";

            // useful
            case "alarm":
                return "alarm";
            case "giveaway":
                return "giveaway";
            case "poll":
            case "vote":
                return "poll";
            case "quickpoll":
            case "quickvote":
                return "quickpoll";
            case "reminder":
            case "remindme":
                return "reminder";
            case "signup":
            case "event":
                return "signup";
            case "timezone":
                return "timezone";

            // fun
            case "easteregg":
            case "eastereggs":
                return "easteregg";
            case "avatar":
            case "ava":
                return "avatar";
            case "baguette":
                return "baguette";
            case "coinflip":
            case "cointoss":
                return "coinflip";
            case "createembed":
            case "embed":
                return "createembed";
            case "editembed":
                return "editembed";
            case "flip":
            case "unflip":
                return "flip";
            case "leaderboard":
            case "levelranking":
                return "leaderboard";
            case "level":
                return "level";
            case "levelupmessage":
            case "levelupmessages":
            case "levelmessage":
            case "levelmessages":
                return "levelupmessage";
            case "love":
            case "ship":
                return "love";
            case "profile":
                return "profile";

            // interaction
            case "interaction":
                return "interaction";

            // random
            case "random":
            case "imgur":
                return "random";
            case "bird":
            case "birb":
                return "bird";
            case "cat":
            case "catto":
                return "cat";
            case "dog":
            case "doggo":
                return "dog";
            case "fox":
            case "fennec":
                return "fox";
            case "koala":
                return "koala";
            case "meme":
                return "meme";
            case "panda":
                return "panda";
            case "pikachu":
            case "pika":
                return "pikachu";
            case "redpanda":
                return "redpanda";
            case "sloth":
            case "hiriko":
            case "hirik0":
                return "sloth";

            default:
                return null;
        }
    }

    public static List<String> getValidFeatures() {
        return new ArrayList<>() {{
            // moderation
            add("autorole");
            add("bestofimage");
            add("bestofquote");
            add("birthday");
            add("clear");
            add("join");
            add("leave");
            add("levelrole");
            add("livestream");
            add("mediaonlychannel");
            add("reactionrole");
            add("role");
            add("server");
            add("serversetup");
            add("user");
            add("voicelobby");

            // information
            add("botinfo");
            add("ping");
            add("serverinfo");

            // useful
            add("alarm");
            add("giveaway");
            add("poll");
            add("quickpoll");
            add("reminder");
            add("signup");
            add("timezone");

            // fun
            add("easteregg");
            add("avatar");
            add("baguette");
            add("coinflip");
            add("createembed");
            add("editembed");
            add("flip");
            add("leaderboard");
            add("level");
            add("levelupmessage");
            add("love");
            add("profile");

            // interaction
            add("interaction");

            // random
            add("random");
            add("bird");
            add("cat");
            add("dog");
            add("fox");
            add("koala");
            add("meme");
            add("panda");
            add("pikachu");
            add("redpanda");
            add("sloth");
        }};
    }

    public static String getToggleName(String name) {
        switch (name) {
            // Flip also toggles Unflip
            case "unflip":
                return "flip";

            // Profile also toggles Bio
            case "bio":
            case "achievements":
            case "mostusedcommands":
                return "profile";

            // All interactions will be toggled at once
            case "beg":
            case "cookie":
            case "cop":
            case "dab":
            case "flex":
            case "highfive":
            case "hug":
            case "kiss":
            case "lick":
            case "pat":
            case "slap":
            case "wave":
            case "wink":
                return "interaction";

            default:
                return name;
        }
    }
}
