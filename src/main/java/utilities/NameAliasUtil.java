package utilities;

import java.util.ArrayList;
import java.util.List;

public class NameAliasUtil {
    public static String getToggleAlias(String arg) {
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
            case "deletecommands":
            case "deletecommand":
                return "deletecommands";
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

    public static List<String> getValidToggles() {
        return new ArrayList<>() {{
            // moderation
            add("autorole");
            add("bestofimage");
            add("bestofquote");
            add("birthday");
            add("clear");
            add("deletecommands");
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

    public static List<String> getValidFeatures() {
        return new ArrayList<>() {{
            // moderation
            add("autorole");
            add("bestofimage");
            add("bestofquote");
            add("birthday");
            add("clear");
            add("join");
            add("joinmessage");
            add("leave");
            add("leavemessage");
            add("levelrole");
            add("livestream");
            add("mediaonlychannel");
            add("reactionrole");
            add("role");
            add("server");
            add("serversetup");
            add("toggle");
            add("user");
            add("voicelobby");

            // information
            add("botinfo");
            add("patreon");
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
            add("achievements");
            add("avatar");
            add("baguette");
            add("bio");
            add("coinflip");
            add("createembed");
            add("editembed");
            add("flip");
            add("love");
            add("mostusedcommands");
            add("profile");
            add("unflip");

            // interaction
            add("beg");
            add("cookie");
            add("cop");
            add("dab");
            add("flex");
            add("happybirthday");
            add("highfive");
            add("hug");
            add("kiss");
            add("lick");
            add("pat");
            add("poke");
            add("slap");
            add("wave");
            add("wink");

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
}
