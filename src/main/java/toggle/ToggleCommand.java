package toggle;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;
import servant.Guild;
import servant.Log;
import servant.Servant;
import utilities.UsageEmbed;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ToggleCommand extends Command {
    public ToggleCommand() {
        this.name = "toggle";
        this.help = "toggles feature on or off | **ADMINISTRATOR**";
        this.category = new Command.Category("Moderation");
        this.arguments = "[feature] [on|off|status]";
        this.guildOnly = true;
        this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
    }

    @Override
    protected void execute(CommandEvent event) {
        String prefix = Servant.config.getDefaultPrefix();
        // Usage
        if (event.getArgs().isEmpty()) {
            try {
                String usage = "**Manage one feautre**\n" +
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

                String hint = "Be careful with toggling all features on or off, as you may delete your perfect set-up.\n" +
                        "You may write `everything` instead of `all`.\n" +
                        "Status will only show the current status without changing any values.";

                event.reply(new UsageEmbed(name, event.getAuthor(), ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
            } catch (SQLException e) {
                new Log(e, event, name).sendLogSqlCommandEvent(true);
            }
            return;
        }

        List<String> validFeatures = new ArrayList<>(){{
            add("all");
            add("autorole");
            add("clear");
            add("guild");
            add("join");
            add("mediaonlychannel");
            add("coinflip");
            add("level");
            add("avatar");
            add("user");
            add("interaction");
        }};

        String[] args = event.getArgs().split(" ");
        if (args.length < 2) {
            event.reply("Too few arguments.\n" +
                    "toggle [feature] <on|off|status>\n" +
                    "e.g.: toggle level off");
            return;
        }

        String feature = getAlias(args[0]);
        if (!validFeatures.contains(feature)) {
            event.reply("Invalid feature.\n" +
                    "Currently only 'level' is toggleable.");
            return;
        }

        String status = args[1].toLowerCase();
        if (!status.equals("on") && !status.equals("off") && !status.equals("status")) {
            event.reply("Status has to be `on`, `off` or `status`.");
            return;
        }

        if (status.equals("status")) {
            try {
                boolean statusB = new Guild(event.getGuild().getIdLong()).getToggleStatus(feature);
                event.reply(feature + "'s status: " + (statusB ? "on" : "off"));
            } catch (SQLException e) {
                new Log(e, event, name).sendLogSqlCommandEvent(true);
            }
            return;
        }

        boolean statusBool;
        statusBool = status.equals("on");

        try {
            new Guild(event.getGuild().getIdLong()).setToggleStatus(feature, statusBool);
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(true);
            return;
        }
        event.reactSuccess();
    }

    private String getAlias(String arg) {
        switch (arg.toLowerCase()) {
            case "all":
            case "everything":
                return "all";

            case "autorole":
            case "ar":
                return "autorole";

            case "clear":
            case "clean":
            case "delete":
                return "clear";

            case "guild":
            case "server":
                return "guild";

            case "join":
            case "leave":
                return "join";

            case "mediaonlychannel":
            case "mediaonly":
            case "mo":
            case "moc":
                return "mediaonlychannel";

            case "coinflip":
            case "cf":
                return "coinflip";

            case "level":
            case "lvl":
                return "level";

            case "avatar":
            case "ava":
            case "stealavatar":
            case "stealava":
                return "avatar";

            case "user":
            case "member":
                return "user";

            case "interaction":
                return "interaction";

            default:
                return null;
        }
    }
}
