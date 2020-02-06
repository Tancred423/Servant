// Author: Tancred423 (https://github.com/Tancred423)
package moderation.voicelobby;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.guild.Server;
import moderation.user.Master;
import net.dv8tion.jda.api.Permission;
import utilities.*;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.util.HashMap;
import java.util.Map;

public class VoiceLobbyCommand extends Command {
    public VoiceLobbyCommand() {
        this.name = "voicelobby";
        this.aliases = new String[] { "lobby" };
        this.help = "Voice channel lobbies.";
        this.category = new Category("Moderation");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.MOD_COOLDOWN;
        this.cooldownScope = CooldownScope.GUILD;
        this.userPermissions = new Permission[] { Permission.MANAGE_CHANNEL };
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS, Permission.MANAGE_CHANNEL
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var lang = LanguageHandler.getLanguage(event);
        var p = GuildHandler.getPrefix(event);

        var guild = event.getGuild();
        var server = new Server(guild);

        if (event.getArgs().isEmpty()) {
            var description = LanguageHandler.get(lang, "voicelobby_description");
            var usage = String.format(LanguageHandler.get(lang, "voicelobby_usage"),
                    p, name, p, name, p, name, p, name, p, name);
            var hint = LanguageHandler.get(lang, "voicelobby_hint");
            event.reply(MessageUtil.createUsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint));
            return;
        }

        var user = event.getAuthor();
        var master = new Master(user);
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

                var channelIdLong = Long.parseLong(args[1]);
                if (!server.isLobby(channelIdLong)) {
                    server.setLobby(channelIdLong);
                    event.reactSuccess();
                } else event.replyWarning(LanguageHandler.get(lang, "voicelobby_already_set"));
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

                if (server.unsetLobby(Long.parseLong(args[1]))) event.reactSuccess();
                else {
                    event.reactError();
                    event.reply(LanguageHandler.get(lang, "voicelobby_unset_fail"));
                }
                break;

            case "show":
            case "sh":
                var lobbies = server.getLobbies();

                if (lobbies.isEmpty()) {
                    event.reply(LanguageHandler.get(lang, "voicelobby_noneset"));
                    return;
                }

                var builder = new StringBuilder();
                builder.append("```c\n");
                for (long lobby : lobbies) {
                    var vc = guild.getVoiceChannelById(lobby);
                    if (vc != null) builder.append(vc.getName()).append(" (").append(lobby).append(")\n");
                }
                builder.append("```");

                var fields = new HashMap<String, Map.Entry<String, Boolean>>();
                fields.put(LanguageHandler.get(lang, "voicelobby_current"), new MyEntry<>(builder.toString(), false));

                new MessageUtil().sendEmbed(event.getChannel(),
                        master.getColor(),
                        guild.getName(),
                        null,
                        guild.getIconUrl(),
                        null,
                        guild.getSplashUrl(),
                        null,
                        fields,
                        null,
                        String.format(LanguageHandler.get(lang, "voicelobby_footer"), server.getPrefix()),
                        null);
                break;

            default:
                event.reactError();
                event.reply(LanguageHandler.get(lang, "voicelobby_firstarg"));
                break;
        }
    }
}
