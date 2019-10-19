// Author: Tancred423 (https://github.com/Tancred423)
package moderation.autorole;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.user.User;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Role;
import servant.Log;
import moderation.toggle.Toggle;
import utilities.Constants;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;

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
        if (!Toggle.isEnabled(event, name)) return;

        var internalGuild = new moderation.guild.Guild(event.getGuild().getIdLong());
        var lang = LanguageHandler.getLanguage(event, name);
        var p = GuildHandler.getPrefix(event, name);

        if (event.getArgs().isEmpty()) {
            try {
                var description = LanguageHandler.get(lang, "autorole_description");
                var usage = String.format(LanguageHandler.get(lang, "autorole_usage"), p, name, p, name, p, name, p, name, p, name);
                var hint = LanguageHandler.get(lang, "autorole_hint");
                event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            }
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

                try {
                    internalGuild.setAutorole(role.getIdLong(), delay);
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                    return;
                }
                event.reactSuccess();
                break;

            case "unset":
            case "u":
                boolean wasUnset;
                try {
                    wasUnset = internalGuild.unsetAutorole();
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                    return;
                }
                if (wasUnset) event.reactSuccess();
                else event.reply(LanguageHandler.get(lang, "autorole_missing"));
                break;

            case "show":
            case "sh":
                try {
                    var roleAndDelay = internalGuild.getAutorole();
                    if (!roleAndDelay.isEmpty()) {
                        role = roleAndDelay.entrySet().iterator().next().getKey();
                        delay = roleAndDelay.get(role);
                    }
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                    return;
                }
                if (role == null) event.reply(LanguageHandler.get(lang, "autorole_no_current"));
                else event.reply(String.format(LanguageHandler.get(lang, "autorole_current"), role.getName(), role.getIdLong(), delay));
                break;

            default:
                event.reply(LanguageHandler.get(lang, "autorole_first_arg"));
        }

        // Statistics.
        try {
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            new moderation.guild.Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }
    }
}
