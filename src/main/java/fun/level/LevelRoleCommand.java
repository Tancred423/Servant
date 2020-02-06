// Author: Tancred423 (https://github.com/Tancred423)
package fun.level;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.guild.Server;
import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import utilities.Constants;
import utilities.MessageUtil;
import utilities.Parser;
import utilities.StringUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.util.ArrayList;
import java.util.Map;

public class LevelRoleCommand extends Command {
    public LevelRoleCommand() {
        this.name = "levelrole";
        this.aliases = new String[] { "rankrole" };
        this.help = "Gain roles on certain levels.";
        this.category = new Category("Moderation");
        this.arguments = "[set|unset] [level] @role";
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.MOD_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[] { Permission.MANAGE_ROLES };
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS, Permission.MANAGE_ROLES
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var lang = LanguageHandler.getLanguage(event);
        var p = GuildHandler.getPrefix(event);

        if (event.getArgs().isEmpty()) {
            var description = LanguageHandler.get(lang, "levelrole_description");
            var usage = String.format(LanguageHandler.get(lang, "levelrole_usage"),
                    p, name, p, name, p, name, p, name, p, name, p, name);
            var hint = LanguageHandler.get(lang, "levelrole_hint");
            event.reply(MessageUtil.createUsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint));
            return;
        }

        var guild = event.getGuild();
        var server = new Server(guild);
        var user = event.getAuthor();
        var master = new Master(user);

        var args = event.getArgs().split(" ");
        var firstArg = args[0];
        var message = event.getMessage();
        Role role;
        int level;

        switch (firstArg.toLowerCase()) {
            case "set":
            case "s":
                if (message.getMentionedRoles().isEmpty() || args.length < 3) {
                    event.reply(LanguageHandler.get(lang, "levelrole_missing"));
                    return;
                }

                try {
                    level = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    event.reply(LanguageHandler.get(lang, "levelrole_invalidlevel"));
                    return;
                }

                role = message.getMentionedRoles().get(0);
                var wasSet = server.setLevelRole(level, role.getIdLong());
                if (wasSet) event.reactSuccess();
                else {
                    event.reply(LanguageHandler.get(lang, "levelrole_alreadyset"));
                    event.reactError();
                }
                break;

            case "unset":
            case "u":
                if (message.getMentionedRoles().isEmpty() || args.length < 3) {
                    event.reply(LanguageHandler.get(lang, "levelrole_missing"));
                    return;
                }

                try {
                    level = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    event.reply(LanguageHandler.get(lang, "levelrole_invalidlevel"));
                    return;
                }

                role = message.getMentionedRoles().get(0);
                server.unsetLevelRole(level, role.getIdLong());
                event.reactSuccess();
                break;

            case "show":
            case "sh":
                var sb = new StringBuilder().append("```c\n").append(LanguageHandler.get(lang, "levelrole_levelrole")).append("\n").append("----- ----------\n");
                Map<Integer, Long> levelRoles = server.getLevelRoles();
                var hasEntry = false;
                for (Map.Entry<Integer, Long> levelRoleEntry : levelRoles.entrySet()) {
                    if (guild.getRoleById(levelRoleEntry.getValue()) == null)
                        server.unsetLevelRole(levelRoleEntry.getKey(), levelRoleEntry.getValue());
                    else {
                        var levelRole = guild.getRoleById(levelRoleEntry.getValue());
                        if (levelRole != null)
                            sb.append(StringUtil.pushWithWhitespace(String.valueOf(levelRoleEntry.getKey()), 5))
                                    .append(" ")
                                    .append(levelRole.getName()).append(" (ID: ")
                                    .append(levelRoleEntry.getValue()).append(")\n");
                        hasEntry = true;
                    }
                }
                sb.append("```");

                if (!hasEntry) {
                    event.reply(LanguageHandler.get(lang, "levelrole_empty"));
                    return;
                }

                var eb = new EmbedBuilder();
                eb.setColor(master.getColor());
                eb.setAuthor(LanguageHandler.get(lang, "levelrole_current"), null, guild.getIconUrl());
                eb.setDescription(sb.toString());
                event.reply(eb.build());
                break;

            case "refresh":
                var members = event.getGuild().getMembers();
                for (Member member : members) {
                    var currentExp = master.getExp(guild.getIdLong());
                    var currentLevel = Parser.getLevelFromExp(currentExp);
                    var roleIds = server.getLevelRolesForLevel(currentLevel);

                    for (var roleId : roleIds)
                        if (guild.getRoleById(roleId) != null) {
                            try {
                                var rolesToAdd = new ArrayList<Role>();
                                rolesToAdd.add(guild.getRoleById(roleId));
                                guild.modifyMemberRoles(member, rolesToAdd, null).queue();
                            } catch (HierarchyException ignored) {
                            }
                        }
                }
                event.reactSuccess();
                break;
        }
    }
}
