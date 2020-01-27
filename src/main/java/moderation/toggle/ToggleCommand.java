// Author: Tancred423 (https://github.com/Tancred423)
package moderation.toggle;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.guild.GuildHandler;
import moderation.user.User;
import net.dv8tion.jda.api.Permission;
import owner.blacklist.Blacklist;
import servant.Servant;
import utilities.Constants;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ToggleCommand extends Command {
    public ToggleCommand() {
        this.name = "toggle";
        this.aliases = new String[0];
        this.help = "Toggles bot's features.";
        this.category = new Command.Category("Moderation");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.MOD_COOLDOWN;
        this.cooldownScope = CooldownScope.GUILD;
        this.userPermissions = new Permission[] { Permission.MANAGE_SERVER };
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                if (Blacklist.isBlacklisted(event.getAuthor(), event.getGuild())) return;

                var lang = LanguageHandler.getLanguage(event);
                var p = GuildHandler.getPrefix(event);

                if (event.getArgs().isEmpty()) {
                    var description = String.format(LanguageHandler.get(lang, "toggle_description"), p, name);
                    var usage = String.format(LanguageHandler.get(lang, "toggle_usage"), p, name, p, name, p, name, p, name, p, name, p, name, p, name, p, name);
                    var hint = LanguageHandler.get(lang, "toggle_hint");

                    event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
                    return;
                }

                List<String> validFeatures = new ArrayList<>() {{
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
                }};

                var args = event.getArgs().split(" ");
                if (args.length < 2) {
                    event.reply(LanguageHandler.get(lang, "toggle_args"));
                    return;
                }

                var feature = getAlias(args[0]);
                // Has to be improved. Redundant text.
                if (feature == null) {
                    event.reply(LanguageHandler.get(lang, "toggle_invalid_feature"));
                    return;
                } else if (!validFeatures.contains(feature) && !feature.equals("all")) {
                    event.reply(LanguageHandler.get(lang, "toggle_invalid_feature"));
                    return;
                }

                var arg1 = args[1].toLowerCase();
                if (!arg1.equals("on") && !arg1.equals("off") && !arg1.equals("status") && !arg1.equals("show")) {
                    event.reply(LanguageHandler.get(lang, "toggle_invalid_argument"));
                    return;
                }

                var author = event.getAuthor();
                var guild = event.getGuild();
                var internalGuild = new Guild(guild.getIdLong());

                if (arg1.equals("show") || arg1.equals("status")) {
                    if (feature.equals("all")) {
                        // Toggle Status All Features
                        var stringBuilder = new StringBuilder();
                        for (var validFeature : validFeatures) {
                            var toggleStatus = internalGuild.getToggleStatus(validFeature, guild, author);
                            stringBuilder.append(validFeature).append(": ").append(toggleStatus ? "on" : "off").append("\n");
                        }
                        event.reply(stringBuilder.toString());
                    } else {
                        // Toggle Status Single Feature
                        var toggleStatus = internalGuild.getToggleStatus(feature, guild, author);
                        event.reply(feature + "'s status: " + (toggleStatus ? "on" : "off"));
                    }
                    return;
                }

                var statusBool = arg1.equals("on");

                if (feature.equals("all"))
                    for (var validFeature : validFeatures)
                        internalGuild.setToggleStatus(validFeature, statusBool, guild, author);
                else internalGuild.setToggleStatus(feature, statusBool, guild, author);
                event.reactSuccess();

                // Statistics.
                new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
                if (event.getGuild() != null)
                    new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Servant.threadPool);
    }

    private String getAlias(String arg) {
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
            case "achievement":
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
                return "random";
            case "bird":
            case "birb":
                return "bird";
            case "cat":
                return "cat";
            case "dog":
                return "dog";
            case "fox":
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

            default:
                return null;
        }
    }
}
