// Author: Tancred423 (https://github.com/Tancred423)
package moderation.toggle;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.user.User;
import net.dv8tion.jda.core.Permission;
import moderation.guild.Guild;
import servant.Log;
import utilities.Constants;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
        this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected void execute(CommandEvent event) {
        var lang = LanguageHandler.getLanguage(event, name);
        var p = GuildHandler.getPrefix(event, name);

        if (event.getArgs().isEmpty()) {
            try {
                var description = String.format(LanguageHandler.get(lang, "toggle_description"), p, name);
                var usage = String.format(LanguageHandler.get(lang, "toggle_usage"), p, name, p, name, p, name, p, name, p, name, p, name, p, name, p, name);
                var hint = LanguageHandler.get(lang, "toggle_hint");

                event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            }
            return;
        }

        List<String> validFeatures = new ArrayList<>(){{
            // moderation
            add("autorole");
            add("birthday");
            add("bestofimage");
            add("bestofquote");
            add("clear");
            add("join");
            add("leave");
            add("levelrole");
            add("livestream");
            add("mediaonlychannel");
            add("reactionrole");
            add("role");
            add("server");
            add("setupwizard");
            add("user");
            add("voicelobby");

            // information
            add("botinfo");
            add("serverinfo");

            // useful
            add("alarm");
            add("giveaway");
            add("reminder");
            add("signup");
            add("timezone");

            /// votes
            add("quickvote");
            add("vote");

            // fun
            add("avatar");
            add("baguette");
            add("bird");
            add("cat");
            add("coinflip");
            add("createembed");
            add("dog");
            add("editembed");
            add("flip");
            add("level");
            add("love");
            add("profile");

            /// interaction
            add("interaction");
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

        var internalGuild = new Guild(event.getGuild().getIdLong());

        if (arg1.equals("show") || arg1.equals("status")) {
            if (feature.equals("all")) {
                // Toggle Status All Features
                var stringBuilder = new StringBuilder();
                for (var validFeature : validFeatures) {
                    try {
                        var toggleStatus = internalGuild.getToggleStatus(validFeature);
                        stringBuilder.append(validFeature).append(": ").append(toggleStatus ? "on" : "off").append("\n");
                    } catch (SQLException e) {
                        new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
                    }
                }
                event.reply(stringBuilder.toString());
            } else {
                // Toggle Status Single Feature
                try {
                    var toggleStatus = internalGuild.getToggleStatus(feature);
                    event.reply(feature + "'s status: " + (toggleStatus ? "on" : "off"));
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                }
            }
            return;
        }

        var statusBool = arg1.equals("on");

        if (feature.equals("all")) {
            // Toggle all features.
            for (var validFeature : validFeatures) {
                try {
                    internalGuild.setToggleStatus(validFeature, statusBool);
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
                    return;
                }
            }
        } else {
            // Toggle single feature.
            try {
                internalGuild.setToggleStatus(feature, statusBool);
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                return;
            }
        }
        event.reactSuccess();

        // Statistics.
        try {
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            if (event.getGuild() != null) new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }
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
            case "birthday":
            case "bday":
                return "birthday";
            case "bestofimage":
            case "image":
                return "bestofimage";
            case "bestofquote":
            case "quote":
                return "bestofquote";
            case "clear":
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
            case "setupwizard":
            case "setup":
                return "setupwizard";
            case "user":
                return "user";
            case "voicelobby":
            case "lobby":
                return "voicelobby";

            // information
            case "botinfo":
            case "about":
                return "botinfo";
            case "serverinfo":
            case "guildinfo":
                return "serverinfo";

            // useful
            case "alarm":
                return "alarm";
            case "giveaway":
                return "giveaway";
            case "reminder":
                return "reminder";
            case "signup":
                return "signup";
            case "timezone":
                return "timezone";

            /// votes
            case "quickvote":
            case "qv":
                return "quickvote";
            case "vote":
            case "v":
                return "vote";

            // fun
            case "avatar":
            case "ava":
                return "avatar";
            case "baguette":
                return "baguette";
            case "bird":
            case "birb":
                return "bird";
            case "cat":
                return "cat";
            case "coinflip":
            case "cointoss":
                return "coinflip";
            case "createembed":
            case "embed":
                return "createembed";
            case "dog":
                return "dog";
            case "editembed":
                return "editembed";
            case "flip":
            case "unflip":
                return "flip";
            case "level":
            case "rank":
                return "level";
            case "love":
            case "ship":
                return "love";
            case "profile":
                return "profile";

            // interaction
            case "interaction":
                return "interaction";

            default:
                return null;
        }
    }
}
