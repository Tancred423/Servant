// Author: Tancred423 (https://github.com/Tancred423)
package moderation.autorole;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Role;
import owner.blacklist.Blacklist;
import utilities.Constants;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.util.concurrent.CompletableFuture;

public class AutoroleCommand extends Command {
    public AutoroleCommand() {
        this.name = "autorole";
        this.aliases = new String[0];
        this.help = "Role for new members.";
        this.category = new Category("Moderation");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.MOD_COOLDOWN;
        this.cooldownScope = CooldownScope.GUILD;
        this.userPermissions = new Permission[]{Permission.MANAGE_ROLES};
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.MANAGE_ROLES};
    }

    @Override
    protected void execute(CommandEvent event) {
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, name)) return;
            if (Blacklist.isBlacklisted(event.getAuthor(), event.getGuild())) return;

            var author = event.getAuthor();
            var guild = event.getGuild();
            var internalGuild = new moderation.guild.Guild(guild.getIdLong());
            var lang = LanguageHandler.getLanguage(event);
            var p = GuildHandler.getPrefix(event);

            if (event.getArgs().isEmpty()) {
                var description = LanguageHandler.get(lang, "autorole_description");
                var usage = String.format(LanguageHandler.get(lang, "autorole_usage"), p, name, p, name, p, name, p, name, p, name);
                var hint = LanguageHandler.get(lang, "autorole_hint");
                event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
                return;
            }

            var args = event.getArgs().split(" ");
            Role role = null;
            int delay = 0;

            switch (args[0].toLowerCase()) {
                case "set":
                case "s":
                    if (event.getMessage().getMentionedRoles().isEmpty()) {
                        event.reply(LanguageHandler.get(lang, "autorole_no_role"));
                        return;
                    }

                    role = event.getMessage().getMentionedRoles().get(0);

                    try {
                        delay = Integer.parseInt(args[args.length - 1]);
                    } catch (NumberFormatException ignored) { }

                    internalGuild.setAutorole(role.getIdLong(), delay, guild, author);
                    event.reactSuccess();
                    break;

                case "unset":
                case "u":
                    if (internalGuild.unsetAutorole(guild, author)) event.reactSuccess();
                    else event.reply(LanguageHandler.get(lang, "autorole_missing"));
                    break;

                case "show":
                case "sh":
                    var roleAndDelay = internalGuild.getAutorole(guild, author);
                    if (!roleAndDelay.isEmpty()) {
                        role = roleAndDelay.entrySet().iterator().next().getKey();
                        delay = roleAndDelay.get(role);
                    }
                    if (role == null) event.reply(LanguageHandler.get(lang, "autorole_no_current"));
                    else event.reply(String.format(LanguageHandler.get(lang, "autorole_current"), role.getName(), role.getIdLong(), delay));
                    break;

                default:
                    event.reply(LanguageHandler.get(lang, "autorole_first_arg"));
            }

            // Statistics.
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
            new moderation.guild.Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
        });
    }
}
