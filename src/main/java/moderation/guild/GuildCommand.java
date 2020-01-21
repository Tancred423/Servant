// Author: Tancred423 (https://github.com/Tancred423)
package moderation.guild;

import files.language.LanguageHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.api.Permission;
import owner.blacklist.Blacklist;
import servant.Servant;
import utilities.*;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
        CompletableFuture.runAsync(() -> {
            try {
                if (!Toggle.isEnabled(event, name)) return;
                if (Blacklist.isBlacklisted(event.getAuthor(), event.getGuild())) return;

                var lang = LanguageHandler.getLanguage(event);
                var p = GuildHandler.getPrefix(event);

                var guild = event.getGuild();
                var internalGuild = new Guild(guild.getIdLong());

                if (event.getArgs().isEmpty()) {
                    var description = LanguageHandler.get(lang, "server_description");
                    var usage = String.format(LanguageHandler.get(lang, "server_usage"),
                            p, name, p, name, p, name, p, name, p, name, p, name, p, name, p, name, p, name, p, name);
                    var hint = String.format(LanguageHandler.get(lang, "server_hint"),
                            Servant.config.getDefaultOffset(), Servant.config.getDefaultPrefix());
                    event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
                    return;
                }

                var args = event.getArgs().split(" ");
                var type = args[0].toLowerCase();
                String setting;
                var author = event.getAuthor();
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

                                internalGuild.setOffset(value, guild, author);
                                event.reactSuccess();
                                break;

                            case "prefix":
                                if (!Parser.isValidPrefix(value)) {
                                    event.reply(LanguageHandler.get(lang, "server_prefix"));
                                    event.reactError();
                                    return;
                                }

                                internalGuild.setPrefix(value, guild, author);
                                event.reactSuccess();
                                break;

                            case "language":
                                internalGuild.setLanguage(value, guild, author);
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
                                internalGuild.unsetOffset(guild, author);
                                event.reactSuccess();
                                break;

                            case "prefix":
                                internalGuild.unsetPrefix(guild, author);
                                event.reactSuccess();
                                break;

                            case "language":
                                internalGuild.unsetLanguage(guild, author);
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
                        internalUser = new User(author.getIdLong());

                        var showPrefix = internalGuild.getPrefix(guild, author);
                        var showLanguage = internalGuild.getLanguage(guild, author);
                        var showOffset = internalGuild.getOffset(guild, author);
                        showOffset = showOffset.equals("Z") ? "UTC" : showOffset;

                        var fields = new HashMap<String, Map.Entry<String, Boolean>>();
                        fields.put(LanguageHandler.get(lang, "server_offset_text"), new MyEntry<>(showOffset, true));
                        fields.put(LanguageHandler.get(lang, "server_prefix_text"), new MyEntry<>(showPrefix, true));
                        fields.put(LanguageHandler.get(lang, "server_language_text"), new MyEntry<>(showLanguage, true));

                        new MessageHandler().sendEmbed(event.getChannel(),
                                internalUser.getColor(guild, author),
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

                // Statistics.
                new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
                if (event.getGuild() != null)
                    new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Servant.threadPool);
    }
}
