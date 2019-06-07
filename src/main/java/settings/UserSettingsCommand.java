package settings;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;
import servant.Guild;
import servant.Log;
import servant.User;

import java.sql.SQLException;

public class UserSettingsCommand extends Command {
    public UserSettingsCommand() {
        this.name = "user";
        this.aliases = new String[]{"member"};
        this.help = "personalize the bot to your desire.";
        this.category = new Category("Settings");
        this.arguments = "<set|unset> [setting] [value]";
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getArgs().isEmpty()) {
            event.reply("You have to put arguments.");
            return;
        }

        String[] args = event.getArgs().split(" ");
        String type = args[0].toLowerCase();
        String setting;
        long userId = event.getAuthor().getIdLong();
        User internalUser;

        switch (type) {
            case "set":
            case "s":
                if (args.length < 3) {
                    event.reply("To set a setting, there have to be 3 arguments.\n" +
                            "... set [setting] [value]");
                    return;
                }

                setting = args[1].toLowerCase();
                String value = args[2];

                try {
                    internalUser = new User(userId);
                } catch (SQLException e) {
                    new Log(e, event, name).sendLogSqlCommandEvent(true);
                    return;
                }

                switch (setting) {
                    case "color":
                        value = utilities.Parser.parseColor(value);
                        if (value == null) {
                            event.reply("The given color code is invalid.");
                            return;
                        }
                        try {
                            internalUser.setColor(value);
                        } catch (SQLException e) {
                            new Log(e, event, name).sendLogSqlCommandEvent(true);
                            return;
                        }

                        event.reactSuccess();
                        break;

                    default:
                        event.reply("This setting does not exist.");
                        break;
                }
                break;

            case "unset":
            case "u":
                if (args.length < 2) {
                    event.reply("To unset a setting, there have to be 2 arguments.\n" +
                            "... unset [setting]");
                    return;
                }

                setting = args[1].toLowerCase();

                try {
                    internalUser = new User(userId);
                } catch (SQLException e) {
                    new Log(e, event, name).sendLogSqlCommandEvent(true);
                    return;
                }

                switch (setting) {
                    case "color":
                        boolean wasUnset;
                        try {
                            wasUnset = internalUser.unsetColor();
                        } catch (SQLException e) {
                            new Log(e, event, name).sendLogSqlCommandEvent(true);
                            return;
                        }

                        if (wasUnset) event.reactSuccess();
                        else event.reply("Nothing to unset.");
                        break;

                    default:
                        event.reply("This setting does not exist.");
                        break;
                }
                break;

            default:
                event.reply("The first argument has to be either `set` or `unset`.");
        }

        // Statistics.
        try {
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            if (event.getGuild() != null) new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }
    }
}
