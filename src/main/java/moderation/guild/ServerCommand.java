// Author: Tancred423 (https://github.com/Tancred423)
package moderation.guild;

import files.language.LanguageHandler;
import moderation.toggle.Toggle;
import net.dv8tion.jda.core.Permission;
import owner.blacklist.Blacklist;
import servant.Log;
import servant.Servant;
import moderation.user.User;
import utilities.*;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ServerCommand extends Command {
    public ServerCommand() {
        this.name = "server";
        this.aliases = new String[]{"guild"};
        this.help = "Bot personalization. (server specific)";
        this.category = new Category("Moderation");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.MOD_COOLDOWN;
        this.cooldownScope = CooldownScope.GUILD;
        this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected void execute(CommandEvent event) {
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, name)) return;
            if (Blacklist.isBlacklisted(event.getAuthor(), event.getGuild())) return;

            var lang = LanguageHandler.getLanguage(event, name);
            var p = GuildHandler.getPrefix(event, name);

            var guild = event.getGuild();
            var internalGuild = new Guild(guild.getIdLong());

            if (event.getArgs().isEmpty()) {
                try {
                    var description = LanguageHandler.get(lang, "server_description");
                    var usage = String.format(LanguageHandler.get(lang, "server_usage"),
                            p, name, p, name, p, name, p, name, p, name, p, name, p, name, p, name, p, name, p, name);
                    var hint = String.format(LanguageHandler.get(lang, "server_hint"),
                            Servant.config.getDefaultOffset(), Servant.config.getDefaultPrefix());
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
                                event.reply(LanguageHandler.get(lang, "server_prefix"));
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

                        case "language":
                            try {
                                internalGuild.setLanguage(value);
                            } catch (SQLException e) {
                                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                                return;
                            }

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
                            try {
                                internalGuild.unsetOffset();
                            } catch (SQLException e) {
                                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                                return;
                            }

                            event.reactSuccess();
                            break;

                        case "prefix":
                            try {
                                internalGuild.unsetPrefix();
                            } catch (SQLException e) {
                                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                                return;
                            }

                            event.reactSuccess();
                            break;

                        case "language":
                            try {
                                internalGuild.unsetLanguage();
                            } catch (SQLException e) {
                                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                                return;
                            }

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
                    var author = event.getAuthor();
                    internalUser = new User(author.getIdLong());

                    String showOffset;
                    String showPrefix;
                    String showLanguage;
                    try {
                        showOffset = internalGuild.getOffset();
                        showOffset = showOffset.equalsIgnoreCase("z") ? "UTC" : showOffset;
                        showPrefix = internalGuild.getPrefix();
                        showLanguage = internalGuild.getLanguage();
                    } catch (SQLException e) {
                        new Log(e, guild, author, name, event).sendLog(true);
                        return;
                    }

                    Map<String, Map.Entry<String, Boolean>> fields = new HashMap<>();
                    fields.put(LanguageHandler.get(lang, "server_offset_text"), new MyEntry<>(showOffset, true));
                    fields.put(LanguageHandler.get(lang, "server_prefix_text"), new MyEntry<>(showPrefix, true));
                    fields.put(LanguageHandler.get(lang, "server_language_text"), new MyEntry<>(showLanguage, true));

                    try {
                        new MessageHandler().sendEmbed(event.getChannel(),
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
                    } catch (SQLException e) {
                        new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                    }
                    break;

                default:
                    event.reply(LanguageHandler.get(lang, "server_firstarg"));
            }

            // Statistics.
            try {
                new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
                if (event.getGuild() != null) new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
            }
        });
    }
}
