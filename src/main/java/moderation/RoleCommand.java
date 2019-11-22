// Author: Tancred423 (https://github.com/Tancred423)
package moderation;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import utilities.Constants;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.util.ArrayList;

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
        this.userPermissions = new Permission[] { Permission.MANAGE_ROLES };
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS, Permission.MANAGE_ROLES
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            if (!Toggle.isEnabled(event, name)) return;

            var lang = LanguageHandler.getLanguage(event);
            var p = GuildHandler.getPrefix(event);
            var message = event.getMessage();

            if (event.getArgs().isEmpty() || (message.getMentionedMembers().isEmpty())) {
                var description = LanguageHandler.get(lang, "role_description");
                var usage = String.format(LanguageHandler.get(lang, "role_usage"), p, name, p, name);
                var hint = LanguageHandler.get(lang, "role_hint");
                event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
                return;
            }

            var mentioned = message.getMentionedMembers().get(0);
            String roleName;
            try {
                roleName = message.getContentRaw().split(">")[1].trim();
            } catch (ArrayIndexOutOfBoundsException e) {
                event.replyError(LanguageHandler.get(lang, "role_missingrolename"));
                return;
            }

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

            try {
                var member = guild.getMemberById(event.getSelfUser().getIdLong());
                if (member != null && member.canInteract(mentioned)) {
                    var rolesToAddOrRemove = new ArrayList<Role>();
                    rolesToAddOrRemove.add(role);
                    if (mentioned.getRoles().contains(role))
                        event.getGuild().modifyMemberRoles(mentioned, null, rolesToAddOrRemove).queue();
                    else event.getGuild().modifyMemberRoles(mentioned, rolesToAddOrRemove, null).queue();
                    event.reactSuccess();
                } else event.replyWarning(LanguageHandler.get(lang, "role_cantinteract"));
            } catch (HierarchyException e) {
                event.reactWarning();
            }

            var author = event.getAuthor();
            // Statistics.
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
            new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
