package moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.doc.standard.Error;
import com.jagrosh.jdautilities.doc.standard.RequiredPermissions;
import com.jagrosh.jdautilities.examples.doc.Author;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import servant.Log;
import utilities.Parser;

import java.sql.SQLException;

@CommandInfo(
        name = {"Autorole"},
        description = "Set the role any new member will receive!",
        usage = "autorole [@role or role ID]"
)
@Error(
        value = "If arguments are provided, but they are not a mention or ID.",
        response = "[Argument] is not a valid mention or ID!"
)
@RequiredPermissions({Permission.MANAGE_ROLES})
@Author("Tancred")
public class AutoroleCommand extends Command {
    public AutoroleCommand() {
        this.name = "autorole";
        this.aliases = new String[]{"ar"};
        this.help = "set the role any new member will receive";
        this.category = new Category("Moderation");
        this.arguments = "[@role or role ID]";
        this.guildOnly = true;
        this.botPermissions = new Permission[]{Permission.MANAGE_ROLES};
        this.userPermissions = new Permission[]{Permission.MANAGE_ROLES};
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getArgs().isEmpty()) {
            event.reply("You provided no arguments.\n" +
                    "You have to `set`, `unset` or `show`.");
            return;
        }

        String[] args = event.getArgs().split(" ");

        Guild guild = event.getGuild();
        servant.Guild internalGuild = new servant.Guild(guild.getIdLong());
        Role role;

        switch (args[0].toLowerCase()) {
            case "set":
            case "s":
                if (args.length < 2) {
                    event.reply("You did not provide a role mention or role ID.");
                    return;
                }

                role = Parser.getRoleFromMessage(event);

                if (role == null) {
                    event.reply("The given role is invalid.");
                    return;
                }

                try {
                    internalGuild.setAutorole(role);
                } catch (SQLException e) {
                    new Log(e, event, name).sendLogSqlCommandEvent(true);
                    return;
                }
                event.reactSuccess();
                break;

            case "unset":
            case "u":
                boolean wasUnset;
                try {
                    wasUnset = internalGuild.unsetAutorole();
                } catch (SQLException e) {
                    new Log(e, event, name).sendLogSqlCommandEvent(true);
                    return;
                }
                if (wasUnset) event.reactSuccess();
                else event.reply("No autorole was set.");
                break;

            case "show":
            case "sh":
                try {
                    role = internalGuild.getAutorole();
                } catch (SQLException e) {
                    new Log(e, event, name).sendLogSqlCommandEvent(true);
                    return;
                }
                if (role == null) event.reply("There is no current autorole.");
                else event.reply("The current autorole is: " + role.getName() + " (" + role.getIdLong() + ").");
                break;

            default:
                event.reply("Invalid first argument.\n" +
                        "Either `set`, `unset` or `show`");
        }

        // Statistics.
        try {
            new servant.User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            new servant.Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }
    }
}
