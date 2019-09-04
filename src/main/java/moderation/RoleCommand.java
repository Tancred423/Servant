// Author: Tancred423 (https://github.com/Tancred423)
package moderation;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.core.Permission;
import servant.Log;
import utilities.Constants;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;

public class RoleCommand extends Command {
    public RoleCommand() {
        this.name = "role";
        this.aliases = new String[0];
        this.help = "Assign or remove roles.";
        this.category = new Category("Moderation");
        this.arguments = "@user <roleName>";
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

        var lang = LanguageHandler.getLanguage(event, name);
        var p = GuildHandler.getPrefix(event, name);
        var message = event.getMessage();

        try {
            if (event.getArgs().isEmpty() || (message.getMentionedMembers().isEmpty())) {
                var description = LanguageHandler.get(lang, "role_description");
                var usage = String.format(LanguageHandler.get(lang, "role_usage"), p, name, p, name);
                var hint = LanguageHandler.get(lang, "role_hint");
                event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
                return;
            }
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            return;
        }

        var mentioned = message.getMentionedMembers().get(0);
        var roleName = message.getContentRaw().split(">")[1].trim();

        if (roleName.isEmpty()) {
            event.reply(LanguageHandler.get(lang, "role_missing"));
            return;
        }

        var guild = event.getGuild();
        var roles = guild.getRolesByName(roleName, true);

        if (roles.isEmpty()) {
            event.reply(LanguageHandler.get(lang, "role_notfound"));
            return;
        }

        var role = roles.get(0);

        if (mentioned.getRoles().contains(role)) event.getGuild().getController().removeSingleRoleFromMember(mentioned, role).queue();
        else event.getGuild().getController().addSingleRoleToMember(mentioned, role).queue();

        event.reactSuccess();

        // Statistics.
        try {
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }
    }
}
