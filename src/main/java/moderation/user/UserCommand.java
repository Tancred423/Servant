// Author: Tancred423 (https://github.com/Tancred423)
package moderation.user;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import net.dv8tion.jda.core.Permission;
import patreon.PatreonHandler;
import moderation.guild.Guild;
import servant.Log;
import utilities.*;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;
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
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!Toggle.isEnabled(event, name)) return;

        var lang = LanguageHandler.getLanguage(event, name);
        var p = GuildHandler.getPrefix(event, name);

        if (event.getArgs().isEmpty()) {
            try {
                var description = LanguageHandler.get(lang, "user_description");
                var usage = String.format(LanguageHandler.get(lang, "user_usage"),
                        p, name, p, name, p, name, p, name, p, name, p, name, p, name, p, name, p, name, p, name, p, name, p, name, p, name);
                var hint = String.format(LanguageHandler.get(lang, "user_hint"), p);
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
        User internalUser = new User(userId);

        // Stream Hide
        if (args.length < 3 && args[0].equalsIgnoreCase("streamhide")) {
            if (event.getGuild() == null && args.length == 1) {
                event.reply(String.format(LanguageHandler.get(lang, "user_streamhide_description"), p, name, p, name));
                return;
            }

            var guildId = event.getGuild() == null ? event.getJDA().getGuildById(args[1]).getIdLong() : event.getGuild().getIdLong();

            try {
                if (internalUser.toggleStreamHidden(guildId)) event.reply(LanguageHandler.get(lang, "user_streamhide_hidden"));
                else event.reply(LanguageHandler.get(lang, "user_streamhide_visible"));
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                return;
            }
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
                        try {
                            internalUser.setColor(value);
                        } catch (SQLException e) {
                            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                            return;
                        }

                        event.reactSuccess();
                        break;

                    case "offset":
                    case "timezone":
                        if (!Parser.isValidOffset(value)) {
                            event.reply(LanguageHandler.get(lang, "server_offset"));
                            event.reactError();
                            return;
                        }

                        try {
                            internalUser.setOffset(value);
                        } catch (SQLException e) {
                            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                            return;
                        }

                        event.reactSuccess();
                        break;

                    case "prefix":
                        if (!Parser.isValidPrefix(value)) {
                            event.reply(LanguageHandler.get(lang, "server_prefix"));
                            event.reactError();
                            return;
                        }

                        try {
                            internalUser.setPrefix(value);
                        } catch (SQLException e) {
                            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                            return;
                        }

                        event.reactSuccess();
                        break;

                    case "language":
                        try {
                            internalUser.setLanguage(value);
                        } catch (SQLException e) {
                            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                            return;
                        }

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
                internalUser = new User(userId);

                switch (setting) {
                    case "color":
                    case "colour":
                        // Has to be $10 Patron.
                        if (!PatreonHandler.is$10Patron(event.getAuthor())) {
                            PatreonHandler.sendWarning(event.getChannel(), "$10", lang);
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
                        else event.reply(LanguageHandler.get(lang, "user_unset_fail"));
                        break;

                    case "offset":
                    case "timezone":
                        try {
                            internalUser.unsetOffset();
                        } catch (SQLException e) {
                            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                            return;
                        }

                        event.reactSuccess();
                        break;

                    case "prefix":
                        try {
                            internalUser.unsetPrefix();
                        } catch (SQLException e) {
                            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                            return;
                        }

                        event.reactSuccess();
                        break;

                    case "language":
                        try {
                            internalUser.unsetLanguage();
                        } catch (SQLException e) {
                            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                            return;
                        }

                        event.reactSuccess();
                        break;

                    default:
                        event.reply(LanguageHandler.get(lang, "user_invalidsetting"));
                        break;
                }
                break;

            case "show":
            case "sh":
                var author = event.getAuthor();
                String colorCode;
                String prefix;
                String offset;
                String language;
                List<Long> streamHiddenGuilds;
                try {
                    internalUser = new User(author.getIdLong());
                    colorCode = internalUser.getColorCode();
                    prefix = internalUser.getPrefix();
                    offset = internalUser.getOffset();
                    if (offset.equals("Z")) offset = "00:00";
                    language = internalUser.getLanguage();
                    streamHiddenGuilds = internalUser.getStreamHiddenGuilds();
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                    return;
                }

                Map<String, Map.Entry<String, Boolean>> fields = new HashMap<>();
                fields.put(LanguageHandler.get(lang, "user_color_text"), new MyEntry<>(colorCode, true));
                fields.put(LanguageHandler.get(lang, "user_prefix_text"), new MyEntry<>(prefix, true));
                fields.put(LanguageHandler.get(lang, "user_offset_text"), new MyEntry<>(offset, true));
                fields.put(LanguageHandler.get(lang, "user_language_text"), new MyEntry<>(language, true));

                var sb = new StringBuilder();
                if (streamHiddenGuilds.isEmpty()) sb.append(LanguageHandler.get(lang, "user_noservers"));
                else for (Long guildId : streamHiddenGuilds) sb.append(event.getJDA().getGuildById(guildId).getName()).append("\n");
                fields.put(LanguageHandler.get(lang, "user_streamhideservers"), new MyEntry<>(sb.toString(), true));

                try {
                    new MessageHandler().sendEmbed(event.getChannel(),
                            internalUser.getColor(),
                            LanguageHandler.get(lang, "user_settings"),
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
                event.reply(LanguageHandler.get(lang, "user_firstarg"));
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
