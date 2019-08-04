// Author: Tancred423 (https://github.com/Tancred423)
package moderation.lobby;

import net.dv8tion.jda.core.Permission;
import servant.Log;
import utilities.MessageHandler;
import utilities.MyEntry;
import utilities.Parser;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LobbyCommand extends Command {
    public LobbyCommand() {
        this.name = "lobby";
        this.aliases = new String[]{"autochannel", "ac"};
        this.help = "Voice channel lobbies.";
        this.category = new Category("Moderation");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = 5;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[]{Permission.MANAGE_CHANNEL};
        this.botPermissions = new Permission[]{Permission.MANAGE_CHANNEL};
    }

    @Override
    protected void execute(CommandEvent event) {
        // Enabled?
        try {
            if (!new moderation.guild.Guild(event.getGuild().getIdLong()).getToggleStatus("lobby")) return;
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }

        var guild = event.getGuild();
        var internalGuild = new moderation.guild.Guild(guild.getIdLong());

        var args = event.getArgs().split(" ");
        if (args.length < 1) {
            String prefix;
            try {
                prefix = internalGuild.getPrefix();
            } catch (SQLException e) {
                new Log(e, guild, event.getAuthor(), name, event).sendLog(true);
                return;
            }
            // Usage
            try {
                var description = "If a member joins an voice channel that is marked as lobby, a copy of this voice channel will be made.\n" +
                        "Then the member will be moved into this new voice channel.\n" +
                        "Once everyone left the new channel, it will be deleted automatically.\n" +
                        "This will save you a lot of space from unused voice channels.";

                var usage = "**Set a voice channel lobby**\n" +
                        "Command: `" + prefix + name + " set <Voice Channel ID>`\n" +
                        "Example: `" + prefix + name + " set 999999999999999999`\n" +
                        "\n" +
                        "**Unset a voice channel lobby**\n" +
                        "Command: `" + prefix + name + " unset <Voice Channel ID>`\n" +
                        "Example: `" + prefix + name + " unset 999999999999999999`\n" +
                        "\n" +
                        "**Show current voice channel lobbies**\n" +
                        "Command: `" + prefix + name + " show`\n" +
                        "\n" +
                        "**Toggle voice-text-channel mode**\n" +
                        "Command: `" + prefix + name + " toggletext`";

                var hint = "**How to get ID's:**\n" +
                        "1. Activate Discord Developer Mode: User Settings → Appearance → ADVANCED → Developer Mode\n" +
                        "2. Rightclick voice channel → Copy ID";

                event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            }
            return;
        }

        var author = event.getAuthor();
        var internalAuthor = new servant.User(author.getIdLong());

        switch (args[0].toLowerCase()) {
            case "set":
            case "s":
                if (args.length < 2) {
                    event.reactError();
                    event.reply("Too few arguments. Please provide a voice channel ID.");
                    return;
                }

                if (!Parser.isValidVoiceChannelId(guild, args[1])) {
                    event.reactError();
                    event.reply("Invalid voice channel ID.");
                    return;
                }

                try {
                    internalGuild.setLobby(Long.parseLong(args[1]));
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                    return;
                }

                event.reactSuccess();
                break;

            case "unset":
            case "u":
                if (args.length < 2) {
                    event.reactError();
                    event.reply("Too few arguments. Please provide a voice channel ID.");
                    return;
                }

                if (!Parser.isValidVoiceChannelId(guild, args[1])) {
                    event.reactError();
                    event.reply("Invalid voice channel ID.");
                    return;
                }

                boolean wasUnset;

                try {
                    wasUnset = internalGuild.unsetLobby(Long.parseLong(args[1]));
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                    return;
                }

                if (wasUnset) event.reactSuccess();
                else {
                    event.reactError();
                    event.reply("Nothing to unset.");
                }
                break;

            case "show":
            case "sh":
                List<Long> lobbies;
                try {
                    lobbies = internalGuild.getLobbies();
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                    return;
                }

                if (lobbies.isEmpty()) {
                    event.reply("No lobbies set!");
                    return;
                }

                var builder = new StringBuilder();
                builder.append("```c\n");
                for (long lobby : lobbies)
                    builder.append(guild.getVoiceChannelById(lobby).getName()).append(" (").append(lobby).append(")\n");
                builder.append("```");

                Map<String, Map.Entry<String, Boolean>> fields = new HashMap<>();
                fields.put("Current Lobbies", new MyEntry<>(builder.toString(), false));
                try {
                    fields.put("Voice Text", new MyEntry<>(internalGuild.isVoiceText() ? "Enabled" : "Disabled", false));
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                    return;
                }

                try {
                    new MessageHandler().sendEmbed(event.getChannel(),
                            internalAuthor.getColor(),
                            guild.getName(),
                            null,
                            guild.getIconUrl(),
                            null,
                            guild.getSplashUrl(),
                            null,
                            fields,
                            null,
                            "Type `" + internalGuild.getPrefix() + "lobby` to get help.",
                            null);
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                    return;
                }
                break;

            case "toggletext":
            case "tt":
                try {
                    var wasEnabled = internalGuild.toggleVoiceText();
                    event.reactSuccess();
                    if (wasEnabled) event.reply("Voice text enabled.");
                    else event.reply("Voice text disabled.");
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                    event.reactWarning();
                }
                break;

            default:
                event.reactError();
                event.reply("Invalid argument. Either `set`, `unset`, `show` or `toggletext`.");
                break;
        }

        // Statistics.
        try {
            new servant.User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            if (event.getGuild() != null) new moderation.guild.Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }
    }
}
