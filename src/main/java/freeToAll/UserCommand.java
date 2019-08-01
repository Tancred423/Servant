// Author: Tancred423 (https://github.com/Tancred423)
package freeToAll;

import net.dv8tion.jda.core.Permission;
import patreon.PatreonHandler;
import moderation.guild.Guild;
import servant.Log;
import servant.Servant;
import servant.User;
import utilities.MessageHandler;
import utilities.MyEntry;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UserCommand extends Command {
    public UserCommand() {
        this.name = "user";
        this.aliases = new String[]{"member"};
        this.help = "Bot personalization. (user specific)";
        this.category = new Category("Free to all");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.cooldown = 5;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[0];
    }

    @Override
    protected void execute(CommandEvent event) {
        // Enabled?
        try {
            if (event.getGuild() != null) if (!new Guild(event.getGuild().getIdLong()).getToggleStatus("user")) return;
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }

        var prefix = Servant.config.getDefaultPrefix();
        // Usage
        if (event.getArgs().isEmpty()) {
            try {
                var description = "With this command you can personalize the bot to your desire.\n" +
                        "Currently you can set a color (req: $10 Patron).";

                var usage = "**Setting an embed color**\n" +
                        "Command: `" + prefix + name + " set color [color code]`\n" +
                        "Example 1: `" + prefix + name + " set color 0xFFFFFF`\n" +
                        "Example 2: `" + prefix + name + " set color #FFFFFF`\n" +
                        "Example 3: `" + prefix + name + " set color FFFFFF`\n" +
                        "\n" +
                        "**Unsetting the embed color**\n" +
                        "Command: `" + prefix + name + " unset color`\n" +
                        "\n" +
                        "**Show your current settings**\n" +
                        "Command: `" + prefix + name + " show`";

                var hint = "An embed color is the color you can see right know on the left of this text field thingy.\n" +
                        "More settings will be added in future updates.";

                event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            }
            return;
        }

        var args = event.getArgs().split(" ");
        var type = args[0].toLowerCase();
        String setting;
        var userId = event.getAuthor().getIdLong();
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
                var value = args[2];
                internalUser = new User(userId);

                switch (setting) {
                    case "color":
                    case "colour":
                        // Has to be $10 Patron.
                        if (!PatreonHandler.is$10Patron(event.getAuthor())) {
                            PatreonHandler.sendWarning(event.getChannel(), "$10");
                            return;
                        }

                        value = utilities.Parser.parseColor(value);
                        if (value == null) {
                            event.reply("The given color code is invalid.");
                            return;
                        }
                        try {
                            internalUser.setColor(value);
                        } catch (SQLException e) {
                            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
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
                internalUser = new User(userId);

                switch (setting) {
                    case "color":
                    case "colour":
                        // Has to be $10 Patron.
                        if (!PatreonHandler.is$10Patron(event.getAuthor())) {
                            PatreonHandler.sendWarning(event.getChannel(), "$10");
                            return;
                        }

                        boolean wasUnset;
                        try {
                            wasUnset = internalUser.unsetColor();
                        } catch (SQLException e) {
                            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
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

            case "show":
            case "sh":
                var author = event.getAuthor();
                String colorCode;
                try {
                    internalUser = new User(author.getIdLong());
                    colorCode = internalUser.getColorCode();
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                    return;
                }

                Map<String, Map.Entry<String, Boolean>> fields = new HashMap<>();
                fields.put("Color", new MyEntry<>(colorCode, true));

                try {
                    new MessageHandler().sendEmbed(event.getChannel(),
                            internalUser.getColor(),
                            "User Settings",
                            null,
                            author.getAvatarUrl(),
                            null,
                            null,
                            null,
                            fields,
                            null,
                            null,
                            null);
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                }
                break;

            default:
                event.reply("The first argument has to be either `set`, `unset` or `show`.");
        }

        // Statistics.
        try {
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            if (event.getGuild() != null) new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }
    }
}
