// Author: Tancred423 (https://github.com/Tancred423)
package fun.level;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import servant.Log;
import servant.Servant;
import utilities.Constants;
import utilities.Parser;
import utilities.StringFormat;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class LevelRoleCommand extends Command {
    public LevelRoleCommand() {
        this.name = "levelrole";
        this.aliases = new String[]{"rankrole"};
        this.help = "Gain roles on certain levels";
        this.category = new Category("Moderation");
        this.arguments = "[set|unset] [level] @role";
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.MOD_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[]{Permission.MANAGE_ROLES};
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.MANAGE_ROLES};
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!Toggle.isEnabled(event, name)) return;

        var lang = LanguageHandler.getLanguage(event, name);
        var p = GuildHandler.getPrefix(event, name);

        if (event.getArgs().isEmpty()) {
            try {
                var description = LanguageHandler.get(lang, "levelrole_description");
                var usage = String.format(LanguageHandler.get(lang, "levelrole_usage"),
                        p, name, p, name, p, name, p, name, p, name, p, name);
                var hint = LanguageHandler.get(lang, "levelrole_hint");
                event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            }
            return;
        }

        var guild = event.getGuild();
        var internalGuild = new Guild(guild.getIdLong());
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
                try {
                    var wasSet = internalGuild.setLevelRole(level, role.getIdLong());
                    if (wasSet) event.reactSuccess();
                    else {
                        event.reply(LanguageHandler.get(lang, "levelrole_alreadyset"));
                        event.reactError();
                    }
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
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
                try {
                    internalGuild.unsetLevelRole(level, role.getIdLong());
                    event.reactSuccess();
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                }
                break;

            case "show":
            case "sh":
                try {
                    var sb = new StringBuilder().append("```c\n").append(LanguageHandler.get(lang, "levelrole_levelrole")).append("\n").append("----- ----------\n");
                    Map<Integer, Long> levelRoles = internalGuild.getLevelRoles();
                    var hasEntry = false;
                    for (Map.Entry<Integer, Long> levelRole : levelRoles.entrySet()) {
                        if (guild.getRoleById(levelRole.getValue()) == null)
                            internalGuild.unsetLevelRole(levelRole.getKey(), levelRole.getValue());
                        else {
                            sb.append(StringFormat.pushWithWhitespace(String.valueOf(levelRole.getKey()), 5))
                                    .append(" ")
                                    .append(guild.getRoleById(levelRole.getValue()).getName()).append(" (ID: ")
                                    .append(levelRole.getValue()).append(")\n");
                            hasEntry = true;
                        }
                    }
                    sb.append("```");

                    if (!hasEntry) {
                        event.reply(LanguageHandler.get(lang, "levelrole_empty"));
                        return;
                    }

                    var eb = new EmbedBuilder();
                    try {
                        eb.setColor(new User(event.getAuthor().getIdLong()).getColor());
                    } catch (SQLException e) {
                        eb.setColor(Color.decode(Servant.config.getDefaultColorCode()));
                    }
                    eb.setAuthor(LanguageHandler.get(lang, "levelrole_current"), null, guild.getIconUrl());
                    eb.setDescription(sb.toString());
                    event.reply(eb.build());
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                }
                break;

            case "refresh":
                try {
                    List<Member> members = event.getGuild().getMembers();
                    for (Member member : members) {
                        var internalUser = new User(member.getUser().getIdLong());
                        var currentExp = internalUser.getExp(guild.getIdLong());
                        var currentLevel = Parser.getLevelFromExp(currentExp);
                        List<Long> roleIds = internalGuild.getLevelRolesForLevel(currentLevel);

                        for (Long roleId : roleIds)
                            if (guild.getRoleById(roleId) != null)
                                guild.getController().addSingleRoleToMember(member, guild.getRoleById(roleId)).queue();
                    }
                    event.reactSuccess();
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                }
                break;
        }

        // Statistics.
        try {
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }
    }
}
