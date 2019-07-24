package moderation.lobby;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import servant.Log;
import utilities.MessageHandler;
import utilities.Parser;

import java.sql.SQLException;
import java.util.List;

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
            if (!new servant.Guild(event.getGuild().getIdLong()).getToggleStatus("lobby")) return;
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }

        String[] args = event.getArgs().split(" ");
        if (args.length < 1) {
            // Usage
            return;
        }

        Guild guild = event.getGuild();
        servant.Guild internalGuild;
        try {
            internalGuild = new servant.Guild(guild.getIdLong());
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(true);
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

                try {
                    internalGuild.unsetLobby(Long.parseLong(args[1]));
                } catch (SQLException e) {
                    new Log(e, event, name).sendLogSqlCommandEvent(true);
                    return;
                }

                event.reactSuccess();
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

                try {
                    new MessageHandler().sendEmbed(event.getChannel(),
                            internalAuthor.getColor(),
                            guild.getName(),
                            null,
                            guild.getIconUrl(),
                            "Current Lobbies",
                            guild.getSplashUrl(),
                            builder.toString(),
                            null,
                            null,
                            "Type `" + internalGuild.getPrefix() + "lobby` to get help.",
                            null);
                } catch (SQLException e) {
                    new Log(e, event, name).sendLogSqlCommandEvent(true);
                    return;
                }
                break;

            default:
                event.reactError();
                event.reply("Invalid argument. Either `set`, `unset` or `show`.");
                break;
        }
    }
}
