// Author: Tancred423 (https://github.com/Tancred423)
package moderation.user;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import net.dv8tion.jda.api.Permission;
import patreon.PatreonHandler;
import utilities.*;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserCommand extends Command {
    public UserCommand() {
        this.name = "user";
        this.aliases = new String[0];
        this.help = "Bot personalization. (user specific)";
        this.category = new Category("Moderation");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var lang = LanguageHandler.getLanguage(event);
        var p = GuildHandler.getPrefix(event);

        if (event.getArgs().isEmpty()) {
            var description = LanguageHandler.get(lang, "user_description");
            var usage = String.format(LanguageHandler.get(lang, "user_usage"),
                    p, name, p, name, p, name, p, name, p, name, p, name, p, name, p, name, p, name, p, name, p, name, p, name, p, name, p, name);
            var hint = String.format(LanguageHandler.get(lang, "user_hint"), p);
            event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
            return;
        }

        var guild = event.getGuild();
        var user = event.getAuthor();
        var master = new Master(event.getAuthor());

        var args = event.getArgs().split(" ");
        var type = args[0].toLowerCase();
        String setting;
        var userId = event.getAuthor().getIdLong();

        // Stream Hide
        if (args.length < 3 && args[0].equalsIgnoreCase("streamhide")) {
            if (event.getGuild() == null && args.length == 1) {
                event.reply(String.format(LanguageHandler.get(lang, "user_streamhide_description"), p, name, p, name));
                return;
            }

            var givenGuild = event.getJDA().getGuildById(args[1]);
            if (givenGuild == null) return;
            var guildId = event.getGuild() == null ? givenGuild.getIdLong() : event.getGuild().getIdLong();

            if (master.toggleStreamHidden(guildId))
                event.reply(LanguageHandler.get(lang, "user_streamhide_hidden"));
            else event.reply(LanguageHandler.get(lang, "user_streamhide_visible"));
            event.reactSuccess();
            return;
        }

        switch (type) {
            case "set":
            case "s":
                if (args.length < 3) {
                    event.reply(LanguageHandler.get(lang, "user_args_set"));
                    return;
                }

                setting = args[1].toLowerCase();
                var value = args[2];

                switch (setting) {
                    case "color":
                    case "colour":
                        // Has to be $10 Patron.
                        if (!PatreonHandler.is$10Patron(event.getAuthor())) {
                            PatreonHandler.sendWarning(event.getChannel(), "$10", lang);
                            return;
                        }

                        value = utilities.Parser.parseColor(value);
                        if (value == null) {
                            event.reply(LanguageHandler.get(lang, "user_invalidcolor"));
                            return;
                        }
                        master.setColor(value);
                        event.reactSuccess();
                        break;

                    case "offset":
                    case "timezone":
                        if (!Parser.isValidOffset(value)) {
                            event.reply(LanguageHandler.get(lang, "server_offset"));
                            event.reactError();
                            return;
                        }

                        master.setOffset(value);
                        event.reactSuccess();
                        break;

                    case "prefix":
                        if (!Parser.isValidPrefix(value)) {
                            event.reply(LanguageHandler.get(lang, "server_prefix"));
                            event.reactError();
                            return;
                        }

                        master.setPrefix(value);
                        event.reactSuccess();
                        break;

                    case "language":
                        master.setLanguage(value);
                        event.reactSuccess();
                        break;

                    default:
                        event.reply(LanguageHandler.get(lang, "user_invalidsetting"));
                        break;
                }
                break;

            case "unset":
            case "u":
                if (args.length < 2) {
                    event.reply(LanguageHandler.get(lang, "user_args_unset"));
                    return;
                }

                setting = args[1].toLowerCase();
                master = new Master(user);

                switch (setting) {
                    case "color":
                    case "colour":
                        // Has to be $10 Patron.
                        if (!PatreonHandler.is$10Patron(event.getAuthor())) {
                            PatreonHandler.sendWarning(event.getChannel(), "$10", lang);
                            return;
                        }

                        if (master.unsetColor()) event.reactSuccess();
                        else event.reply(LanguageHandler.get(lang, "user_unset_fail"));
                        break;

                    case "offset":
                    case "timezone":
                        master.unsetOffset();
                        event.reactSuccess();
                        break;

                    case "prefix":
                        master.unsetPrefix();
                        event.reactSuccess();
                        break;

                    case "language":
                        master.unsetLanguage();
                        event.reactSuccess();
                        break;

                    default:
                        event.reply(LanguageHandler.get(lang, "user_invalidsetting"));
                        break;
                }
                break;

            case "show":
            case "sh":
                String colorCode;
                String prefix;
                String offset;
                String language;
                List<Long> streamHiddenGuilds;
                master = new Master(user);
                colorCode = master.getColorCode();
                prefix = master.getPrefix();
                offset = master.getOffset();
                if (offset.equals("Z")) offset = "00:00";
                language = master.getLanguage();
                streamHiddenGuilds = master.getStreamHiddenGuilds();

                Map<String, Map.Entry<String, Boolean>> fields = new HashMap<>();
                fields.put(LanguageHandler.get(lang, "user_color_text"), new MyEntry<>(colorCode, true));
                fields.put(LanguageHandler.get(lang, "user_prefix_text"), new MyEntry<>(prefix, true));
                fields.put(LanguageHandler.get(lang, "user_offset_text"), new MyEntry<>(offset, true));
                fields.put(LanguageHandler.get(lang, "user_language_text"), new MyEntry<>(language, true));

                var sb = new StringBuilder();
                if (streamHiddenGuilds.isEmpty()) sb.append(LanguageHandler.get(lang, "user_noservers"));
                else for (Long guildId : streamHiddenGuilds) {
                    var streamHiddenGuild = event.getJDA().getGuildById(guildId);
                    if (streamHiddenGuild != null) sb.append(streamHiddenGuild.getName()).append("\n");
                }
                fields.put(LanguageHandler.get(lang, "user_streamhideservers"), new MyEntry<>(sb.toString(), true));

                new MessageHandler().sendEmbed(event.getChannel(),
                        master.getColor(),
                        LanguageHandler.get(lang, "user_settings"),
                        null,
                        user.getAvatarUrl(),
                        null,
                        null,
                        null,
                        fields,
                        null,
                        null,
                        null);
                break;

            default:
                event.reply(LanguageHandler.get(lang, "user_firstarg"));
        }
    }
}
