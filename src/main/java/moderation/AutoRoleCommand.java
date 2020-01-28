// Author: Tancred423 (https://github.com/Tancred423)
package moderation;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.guild.Server;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import utilities.Constants;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

public class AutoRoleCommand extends Command {
    public AutoRoleCommand() {
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
        this.userPermissions = new Permission[] { Permission.MANAGE_ROLES };
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS, Permission.MANAGE_ROLES
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var guild = event.getGuild();
        var server = new Server(guild);
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
                } catch (NumberFormatException ignored) {
                }

                server.setAutorole(role.getIdLong(), delay);
                event.reactSuccess();
                break;

            case "unset":
            case "u":
                if (server.unsetAutorole()) event.reactSuccess();
                else event.reply(LanguageHandler.get(lang, "autorole_missing"));
                break;

            case "show":
            case "sh":
                var roleAndDelay = server.getAutorole();
                if (roleAndDelay != null) {
                    role = roleAndDelay.getKey();
                    delay = roleAndDelay.getValue();
                }
                if (role == null) event.reply(LanguageHandler.get(lang, "autorole_no_current"));
                else
                    event.reply(String.format(LanguageHandler.get(lang, "autorole_current"), role.getName(), role.getIdLong(), delay));
                break;

            default:
                event.reply(LanguageHandler.get(lang, "autorole_first_arg"));
        }
    }
}
