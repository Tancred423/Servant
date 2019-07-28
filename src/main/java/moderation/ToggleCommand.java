package moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;
import moderation.guild.Guild;
import servant.Log;
import servant.Servant;
import utilities.UsageEmbed;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ToggleCommand extends Command {
    public ToggleCommand() {
        this.name = "toggle";
        this.aliases = new String[0];
        this.help = "toggles feature on or off | Administrator";
        this.category = new Command.Category("Moderation");
        this.arguments = "<feature> [on|off|status]";
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = 5;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
        this.botPermissions = new Permission[0];
    }

    @Override
    protected void execute(CommandEvent event) {
        var prefix = Servant.config.getDefaultPrefix();
        // Usage
        if (event.getArgs().isEmpty()) {
            try {
                var usage = "**Manage one feautre**\n" +
                        "Command: `" + prefix + name + " [feature] [on|off|status]`\n" +
                        "Example 1: `" + prefix + name + " level on`\n" +
                        "Example 2: `" + prefix + name + " level off`\n" +
                        "Example 3: `" + prefix + name + " level status`\n" +
                        "\n" +
                        "**Manage all features**\n" +
                        "Command: `" + prefix + name + " all [on|off|status]`\n" +
                        "Example 1: `" + prefix + name + " all on`\n" +
                        "Example 2: `" + prefix + name + " all off`\n" +
                        "Examepl 3: `" + prefix + name + " all status`\n";

                var hint = "Be careful with toggling all features on or off, as you may delete your perfect set-up.\n" +
                        "You may write `everything` instead of `all`.\n" +
                        "Status will only show the current status without changing any values.";

                event.reply(new UsageEmbed(name, event.getAuthor(), ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
            } catch (SQLException e) {
                new Log(e, event, name).sendLogSqlCommandEvent(true);
            }
            return;
        }

        List<String> validFeatures = new ArrayList<>(){{
            add("autorole");
            add("avatar");
            add("baguette");
            add("chatbot");
            add("clear");
            add("coinflip");
            add("embed");
            add("guild");
            add("interaction");
            add("join");
            add("level");
            add("lobby");
            add("love");
            add("mediaonlychannel");
            add("profile");
            add("quickvote");
            add("reactionrole");
            add("user");
        }};

        var args = event.getArgs().split(" ");
        if (args.length < 2) {
            event.reply("Too few arguments.\n" +
                    "toggle [feature] <on|off|status>\n" +
                    "e.g.: toggle level off");
            return;
        }

        var feature = getAlias(args[0]);
        // Has to be improved. Redundant text.
        if (feature == null) {
            event.reply("Invalid feature.");
            return;
        } else if (!validFeatures.contains(feature) && !feature.equals("all")) {
            event.reply("Invalid feature.");
            return;
        }

        var arg1 = args[1].toLowerCase();
        if (!arg1.equals("on") && !arg1.equals("off") && !arg1.equals("status")) {
            event.reply("Status has to be `on`, `off` or `status`.");
            return;
        }

        Guild internalGuild;
        try {
            internalGuild = new Guild(event.getGuild().getIdLong());
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(true);
            return;
        }

        if (arg1.equals("status")) {
            if (feature.equals("all")) {
                // Toggle Status All Features
                var stringBuilder = new StringBuilder();
                for (var validFeature : validFeatures) {
                    try {
                        var toggleStatus = internalGuild.getToggleStatus(validFeature);
                        stringBuilder.append(validFeature).append(": ").append(toggleStatus ? "on" : "off").append("\n");
                    } catch (SQLException e) {
                        new Log(e, event, name).sendLogSqlCommandEvent(false);
                    }
                }
                event.reply(stringBuilder.toString());
            } else {
                // Toggle Status Single Feature
                try {
                    var toggleStatus = internalGuild.getToggleStatus(feature);
                    event.reply(feature + "'s status: " + (toggleStatus ? "on" : "off"));
                } catch (SQLException e) {
                    new Log(e, event, name).sendLogSqlCommandEvent(true);
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
                    new Log(e, event, name).sendLogSqlCommandEvent(false);
                    return;
                }
            }
        } else {
            // Toggle single feature.
            try {
                internalGuild.setToggleStatus(feature, statusBool);
            } catch (SQLException e) {
                new Log(e, event, name).sendLogSqlCommandEvent(true);
                return;
            }
        }
        event.reactSuccess();

        // Statistics.
        try {
            new servant.User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            if (event.getGuild() != null) new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }
    }

    private String getAlias(String arg) {
        switch (arg.toLowerCase()) {
            case "all":
            case "everything":
            case "anything":
                return "all";

            case "autorole":
            case "ar":
                return "autorole";

            case "avatar":
            case "ava":
            case "stealavatar":
            case "stealava":
                return "avatar";

            case "baguette":
                return "baguette";

            case "chatbot":
            case "cleverbot":
                return "chatbot";

            case "clear":
            case "clean":
            case "delete":
            case "purge":
                return "clear";

            case "coinflip":
            case "cf":
            case "cointoss":
            case "ct":
                return "coinflip";

            case "embed":
                return "embed";

            case "guild":
            case "server":
                return "guild";

            case "interaction":
                return "interaction";

            case "join":
            case "leave":
                return "join";

            case "level":
            case "lvl":
            case "experience":
            case "exp":
                return "level";

            case "lobby":
            case "autochannel":
            case "ac":
                return "lobby";

            case "love":
            case "ship":
            case "uwu":
                return "love";

            case "mediaonlychannel":
            case "mediaonly":
            case "moc":
            case "mo":
                return "mediaonlychannel";

            case "profile":
            case "profil":
                return "profile";

            case "quickvote":
            case "qr":
                return "quickvote";

            case "reactionrole":
            case "reactrole":
            case "rr":
                return "reactionrole";

            case "user":
            case "member":
                return "user";

            default:
                return null;
        }
    }
}
