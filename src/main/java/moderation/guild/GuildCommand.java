// Author: Tancred423 (https://github.com/Tancred423)
package moderation.guild;

import files.language.LanguageHandler;
import moderation.user.Master;
import net.dv8tion.jda.api.Permission;
import servant.Servant;
import utilities.*;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.util.HashMap;
import java.util.Map;

public class GuildCommand extends Command {
    public GuildCommand() {
        this.name = "server";
        this.aliases = new String[] { "guild" };
        this.help = "Bot personalization. (server specific)";
        this.category = new Category("Moderation");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.MOD_COOLDOWN;
        this.cooldownScope = CooldownScope.GUILD;
        this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var lang = LanguageHandler.getLanguage(event);
        var p = GuildHandler.getPrefix(event);

        var guild = event.getGuild();
        var server = new Server(guild);

        if (event.getArgs().isEmpty()) {
            var description = LanguageHandler.get(lang, "server_description");
            var usage = String.format(LanguageHandler.get(lang, "server_usage"),
                    p, name, p, name, p, name, p, name, p, name, p, name, p, name, p, name, p, name, p, name);
            var hint = String.format(LanguageHandler.get(lang, "server_hint"),
                    Servant.config.getDefaultOffset(), Servant.config.getDefaultPrefix());
            event.reply(MessageUtil.createUsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint));
            return;
        }

        var args = event.getArgs().split(" ");
        var type = args[0].toLowerCase();
        String setting;
        var author = event.getAuthor();
        Master internalUser;

        switch (type) {
            case "set":
            case "s":
                if (args.length < 3) {
                    event.reply(LanguageHandler.get(lang, "server_args_set"));
                    return;
                }

                setting = args[1].toLowerCase();
                var value = args[2];

                switch (setting) {
                    case "offset":
                    case "timezone":
                        if (!Parser.isValidOffset(value)) {
                            event.reply(LanguageHandler.get(lang, "server_offset"));
                            event.reactError();
                            return;
                        }

                        server.setOffset(value);
                        event.reactSuccess();
                        break;

                    case "prefix":
                        if (!Parser.isValidPrefix(value)) {
                            event.reply(LanguageHandler.get(lang, "server_prefix"));
                            event.reactError();
                            return;
                        }

                        server.setPrefix(value);
                        event.reactSuccess();
                        break;

                    case "language":
                        if (!Parser.isValidLanguage(value)) {
                            event.reply(LanguageHandler.get(lang, "server_language"));
                            event.reactError();
                            return;
                        }

                        server.setLanguage(value);
                        event.reactSuccess();
                        break;

                    default:
                        event.reply(LanguageHandler.get(lang, "server_invalidsetting"));
                        break;
                }
                break;

            case "unset":
            case "u":
                if (args.length < 2) {
                    event.reply(LanguageHandler.get(lang, "server_args_unset"));
                    return;
                }

                setting = args[1].toLowerCase();

                switch (setting) {
                    case "offset":
                    case "timezone":
                        server.unsetOffset();
                        event.reactSuccess();
                        break;

                    case "prefix":
                        server.unsetPrefix();
                        event.reactSuccess();
                        break;

                    case "language":
                        server.unsetLanguage();
                        event.reactSuccess();
                        break;

                    default:
                        event.reply(LanguageHandler.get(lang, "server_invalidsetting"));
                        event.reactWarning();
                        break;
                }
                break;

            case "show":
            case "sh":
                internalUser = new Master(author);

                var showPrefix = server.getPrefix();
                var showLanguage = server.getLanguage();
                var showOffset = server.getOffset();
                showOffset = showOffset.equals("Z") ? "UTC" : showOffset;

                var fields = new HashMap<String, Map.Entry<String, Boolean>>();
                fields.put(LanguageHandler.get(lang, "server_offset_text"), new MyEntry<>(showOffset, true));
                fields.put(LanguageHandler.get(lang, "server_prefix_text"), new MyEntry<>(showPrefix, true));
                fields.put(LanguageHandler.get(lang, "server_language_text"), new MyEntry<>(showLanguage, true));

                new MessageUtil().sendEmbed(event.getChannel(),
                        internalUser.getColor(),
                        LanguageHandler.get(lang, "server_settings"),
                        null,
                        guild.getIconUrl(),
                        null,
                        null,
                        null,
                        fields,
                        null,
                        null,
                        null);
                break;

            default:
                event.reply(LanguageHandler.get(lang, "server_firstarg"));
        }
    }
}
