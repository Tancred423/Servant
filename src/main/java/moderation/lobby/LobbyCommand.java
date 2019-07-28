package moderation.lobby;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import servant.Log;
import utilities.MessageHandler;
import utilities.MyEntry;
import utilities.Parser;
import utilities.UsageEmbed;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LobbyCommand extends Command {
    public LobbyCommand() {
        this.name = "lobby";
        this.aliases = new String[]{"autochannel", "ac"};
        this.help = "set up lobbies for automated voice channels.";
        this.category = new Command.Category("Moderation");
        this.userPermissions = new Permission[]{Permission.MANAGE_CHANNEL};
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        // Enabled?
        try {
            if (!new moderation.guild.Guild(event.getGuild().getIdLong()).getToggleStatus("lobby")) return;
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }

        Guild guild = event.getGuild();
        moderation.guild.Guild internalGuild;

        try {
            internalGuild = new moderation.guild.Guild(guild.getIdLong());
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(true);
            return;
        }

        String[] args = event.getArgs().split(" ");
        if (args.length < 1) {
            String prefix = internalGuild.getPrefix();
            // Usage
            try {
                String usage = "**Set a voice channel lobby**\n" +
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

                event.reply(new UsageEmbed(name, event.getAuthor(), ownerCommand, userPermissions, aliases, usage, null).getEmbed());
            } catch (SQLException e) {
                new Log(e, event, name).sendLogSqlCommandEvent(true);
            }
            return;
        }

        User author = event.getAuthor();
        servant.User internalAuthor = new servant.User(author.getIdLong());

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
                    new Log(e, event, name).sendLogSqlCommandEvent(true);
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
                    new Log(e, event, name).sendLogSqlCommandEvent(true);
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
                    new Log(e, event, name).sendLogSqlCommandEvent(true);
                    return;
                }

                if (lobbies.isEmpty()) {
                    event.reply("No lobbies set!");
                    return;
                }

                StringBuilder builder = new StringBuilder();
                builder.append("```c\n");
                for (long lobby : lobbies)
                    builder.append(guild.getVoiceChannelById(lobby).getName()).append(" (").append(lobby).append(")\n");
                builder.append("```");

                Map<String, Map.Entry<String, Boolean>> fields = new HashMap<>();
                fields.put("Current Lobbies", new MyEntry<>(builder.toString(), false));
                try {
                    fields.put("Voice Text", new MyEntry<>(internalGuild.isVoiceText() ? "Enabled" : "Disabled", false));
                } catch (SQLException e) {
                    new Log(e, event, name).sendLogSqlCommandEvent(true);
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
                    new Log(e, event, name).sendLogSqlCommandEvent(true);
                    return;
                }
                break;

            case "toggletext":
            case "tt":
                try {
                    boolean wasEnabled = internalGuild.toggleVoiceText();
                    event.reactSuccess();
                    if (wasEnabled) event.reply("Voice text enabled.");
                    else event.reply("Voice text disabled.");
                } catch (SQLException e) {
                    new Log(e, event, name).sendLogSqlCommandEvent(true);
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
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }
    }
}
