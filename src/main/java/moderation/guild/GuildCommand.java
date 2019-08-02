// Author: Tancred423 (https://github.com/Tancred423)
package moderation.guild;

import net.dv8tion.jda.core.Permission;
import servant.Log;
import servant.Servant;
import servant.User;
import utilities.MessageHandler;
import utilities.MyEntry;
import utilities.Parser;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class GuildCommand extends Command {
    public GuildCommand() {
        this.name = "guild";
        this.aliases = new String[]{"server"};
        this.help = "Bot personalization. (guild specific)";
        this.category = new Category("Moderation");
        this.arguments = null;
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
        // Enabled?
        try {
            if (!new Guild(event.getGuild().getIdLong()).getToggleStatus("guild")) return;
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
        }

        var guild = event.getGuild();
        var internalGuild = new Guild(guild.getIdLong());

        var prefix = Servant.config.getDefaultPrefix();
        // Usage
        if (event.getArgs().isEmpty()) {
            try {
                var description = "With this command you can personalize the bot to your guild's desire.\n" +
                        "Currently you can set an offset and prefix.";

                var usage = "**Setting an offset**\n" +
                        "Command: `" + prefix + name + " set offset [offset]`\n" +
                        "Example: `" + prefix + name + " set offset +01:00`\n" +
                        "\n" +
                        "**Unsetting the offset**\n" +
                        "Command: `" + prefix + name + " unset offset`\n" +
                        "\n" +
                        "**Setting an server specific prefix**\n" +
                        "Command: `" + prefix + name + " set prefix [prefix]`\n" +
                        "Example: `" + prefix + name + " set prefix !`\n" +
                        "\n" +
                        "**Unsettings the prefix**\n" +
                        "Command: `" + prefix + name + " unset prefix`\n" +
                        "\n" +
                        "**Show your current settings**\n" +
                        "Command: `" + prefix + name + " show`";

                var hint = "Unsetting an offset will just remove your custom offset and you will use the default offset (" + Servant.config.getDefaultOffset() + ") again.\n" +
                        "Offset always adds on UTC.\n" +
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

                switch (setting) {
                    case "offset":
                    case "timezone":
                        if (!Parser.isValidOffset(value)) {
                            event.reply("Invalid offset.");
                            event.reactError();
                            return;
                        }

                        try {
                            internalGuild.setOffset(value);
                        } catch (SQLException e) {
                            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                            return;
                        }

                        event.reactSuccess();
                        break;

                    case "prefix":
                        if (!Parser.isValidPrefix(value)) {
                            event.reply("Invalid prefix.");
                            event.reactError();
                            return;
                        }

                        try {
                            internalGuild.setPrefix(value);
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
                boolean wasUnset;

                switch (setting) {
                    case "offset":
                    case "timezone":
                        try {
                            wasUnset = internalGuild.unsetOffset();
                        } catch (SQLException e) {
                            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                            return;
                        }

                        if (wasUnset) event.reactSuccess();
                        else event.reply("Nothing to unset.");
                        break;

                    case "prefix":
                        try {
                            wasUnset = internalGuild.unsetPrefix();
                        } catch (SQLException e) {
                            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                            return;
                        }

                        if (wasUnset) event.reactSuccess();
                        else event.reply("Nothing to unset.");
                        break;

                    default:
                        event.reply("This setting does not exist.");
                        event.reactWarning();
                        break;
                }
                break;

            case "show":
            case "sh":
                var author = event.getAuthor();
                internalUser = new User(author.getIdLong());

                String showOffset;
                String showPrefix;
                try {
                    showOffset = internalGuild.getOffset();
                    showPrefix = internalGuild.getPrefix();
                } catch (SQLException e) {
                    new Log(e, guild, author, name, event).sendLog(true);
                    return;
                }

                Map<String, Map.Entry<String, Boolean>> fields = new HashMap<>();
                fields.put("Offset", new MyEntry<>(showOffset, true));
                fields.put("Prefix", new MyEntry<>(showPrefix, true));

                try {
                    new MessageHandler().sendEmbed(event.getChannel(),
                            internalUser.getColor(),
                            "Guild Settings",
                            null,
                            guild.getIconUrl(),
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
