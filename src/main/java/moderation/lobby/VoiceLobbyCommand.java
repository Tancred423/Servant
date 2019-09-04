// Author: Tancred423 (https://github.com/Tancred423)
package moderation.lobby;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.core.Permission;
import servant.Log;
import utilities.*;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoiceLobbyCommand extends Command {
    public VoiceLobbyCommand() {
        this.name = "voicelobby";
        this.aliases = new String[]{"lobby"};
        this.help = "Voice channel lobbies.";
        this.category = new Category("Moderation");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.MOD_COOLDOWN;
        this.cooldownScope = CooldownScope.GUILD;
        this.userPermissions = new Permission[]{Permission.MANAGE_CHANNEL};
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.MANAGE_CHANNEL};
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!Toggle.isEnabled(event, name)) return;

        var lang = LanguageHandler.getLanguage(event, name);
        var p = GuildHandler.getPrefix(event, name);

        var guild = event.getGuild();
        var internalGuild = new moderation.guild.Guild(guild.getIdLong());

        if (event.getArgs().isEmpty()) {
            try {
                var description = LanguageHandler.get(lang, "voicelobby_description");
                var usage = String.format(LanguageHandler.get(lang, "voicelobby_usage"),
                        p, name, p, name, p, name, p, name, p, name);
                var hint = LanguageHandler.get(lang, "voicelobby_hint");
                event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            }
            return;
        }

        var author = event.getAuthor();
        var internalAuthor = new User(author.getIdLong());
        var args = event.getArgs().split(" ");

        switch (args[0].toLowerCase()) {
            case "set":
            case "s":
                if (args.length < 2) {
                    event.reactError();
                    event.reply(LanguageHandler.get(lang, "voicelobby_missingid"));
                    return;
                }

                if (!Parser.isValidVoiceChannelId(guild, args[1])) {
                    event.reactError();
                    event.reply(LanguageHandler.get(lang, "voicelobby_invalidid"));
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
                    event.reply(LanguageHandler.get(lang, "voicelobby_missingid"));
                    return;
                }

                if (!Parser.isValidVoiceChannelId(guild, args[1])) {
                    event.reactError();
                    event.reply(LanguageHandler.get(lang, "voicelobby_invalidid"));
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
                    event.reply(LanguageHandler.get(lang, "voicelobby_unset_fail"));
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
                    event.reply(LanguageHandler.get(lang, "voicelobby_noneset"));
                    return;
                }

                var builder = new StringBuilder();
                builder.append("```c\n");
                for (long lobby : lobbies)
                    builder.append(guild.getVoiceChannelById(lobby).getName()).append(" (").append(lobby).append(")\n");
                builder.append("```");

                Map<String, Map.Entry<String, Boolean>> fields = new HashMap<>();
                fields.put(LanguageHandler.get(lang, "voicelobby_current"), new MyEntry<>(builder.toString(), false));

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
                            String.format(LanguageHandler.get(lang, "voicelobby_footer"), internalGuild.getPrefix()),
                            null);
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                    return;
                }
                break;

            default:
                event.reactError();
                event.reply(LanguageHandler.get(lang, "voicelobby_firstarg"));
                break;
        }

        // Statistics.
        try {
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            if (event.getGuild() != null) new moderation.guild.Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }
    }
}
