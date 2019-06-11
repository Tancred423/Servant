package toggle;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;
import net.dv8tion.jda.core.Permission;
import servant.Guild;
import servant.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@CommandInfo(
        name = {"Toggle"},
        description = "Toggle features on/off!",
        usage = "toggle [feature] <on|off|status>"
)
@Author("Tancred")
public class ToggleCommand extends Command {
    public ToggleCommand() {
        this.name = "toggle";
        this.help = "toggles feature on or off";
        this.category = new Command.Category("Toggle");
        this.arguments = "[feature] <on|off|status>";
        this.guildOnly = true;
        this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
    }

    @Override
    protected void execute(CommandEvent event) {
        List<String> validFeatures = new ArrayList<>(){{
            add("level");
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
            case "level":
            case "lvl":
                return "level";
            default:
                return null;
        }
    }
}
