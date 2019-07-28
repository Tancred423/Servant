package moderation.autorole;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Role;
import servant.Log;
import servant.Servant;
import utilities.Parser;
import utilities.UsageEmbed;

import java.sql.SQLException;

public class AutoroleCommand extends Command {
    public AutoroleCommand() {
        this.name = "autorole";
        this.aliases = new String[]{"ar"};
        this.help = "set the role any new member will receive | Manage Roles";
        this.category = new Category("Moderation");
        this.arguments = "[set|unset|show] <on set: @role or role ID>";
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = 5;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[]{Permission.MANAGE_ROLES};
        this.botPermissions = new Permission[]{Permission.MANAGE_ROLES};
    }

    @Override
    protected void execute(CommandEvent event) {
        // Enabled?
        try {
            if (!new moderation.guild.Guild(event.getGuild().getIdLong()).getToggleStatus("autorole")) return;
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }

        var guild = event.getGuild();
        moderation.guild.Guild internalGuild;
        try {
            internalGuild = new moderation.guild.Guild(guild.getIdLong());
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(true);
            return;
        }
        var prefix = Servant.config.getDefaultPrefix();
        // Usage
        if (event.getArgs().isEmpty()) {
            try {
                var usage = "**Setting up an autorole**\n" +
                        "Command: `" + prefix + name + " set [@role or role ID]`\n" +
                        "Example 1: `" + prefix + name + " set @Member`\n" +
                        "Example 2: `" + prefix + name + " set 999999999999999999`\n" +
                        "\n" +
                        "**Unsetting the autorole**\n" +
                        "Command: `" + prefix + name + " unset`\n" +
                        "\n" +
                        "**Showing current autorole**\n" +
                        "Command: `" + prefix + name + " show`";

                var hint = "You can get the role ID by writing `\\@role`.\n" +
                        "This requires you to ping it. To avoid it, you can do it in a hidden channel.\n" +
                        "Using the role ID instead of just pinging it, is nice if you don't want to annoy a lot of people.";

                event.reply(new UsageEmbed(name, event.getAuthor(), ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
            } catch (SQLException e) {
                new Log(e, event, name).sendLogSqlCommandEvent(true);
            }
            return;
        }

        var args = event.getArgs().split(" ");

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
            new moderation.guild.Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }
    }
}
