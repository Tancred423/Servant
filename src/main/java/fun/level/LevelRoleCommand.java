// Author: Tancred423 (https://github.com/Tancred423)
package fun.level;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import owner.blacklist.Blacklist;
import servant.Servant;
import utilities.Constants;
import utilities.Parser;
import utilities.StringFormat;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
        CompletableFuture.runAsync(() -> {
            try {
                if (!Toggle.isEnabled(event, name)) return;
                if (Blacklist.isBlacklisted(event.getAuthor(), event.getGuild())) return;

                var lang = LanguageHandler.getLanguage(event);
                var p = GuildHandler.getPrefix(event);

                if (event.getArgs().isEmpty()) {
                    var description = LanguageHandler.get(lang, "levelrole_description");
                    var usage = String.format(LanguageHandler.get(lang, "levelrole_usage"),
                            p, name, p, name, p, name, p, name, p, name, p, name);
                    var hint = LanguageHandler.get(lang, "levelrole_hint");
                    event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
                    return;
                }

                var author = event.getAuthor();
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
                        var wasSet = internalGuild.setLevelRole(level, role.getIdLong(), guild, author);
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
                        internalGuild.unsetLevelRole(level, role.getIdLong(), guild, author);
                        event.reactSuccess();
                        break;

                    case "show":
                    case "sh":
                        var sb = new StringBuilder().append("```c\n").append(LanguageHandler.get(lang, "levelrole_levelrole")).append("\n").append("----- ----------\n");
                        Map<Integer, Long> levelRoles = internalGuild.getLevelRoles(guild, author);
                        var hasEntry = false;
                        for (Map.Entry<Integer, Long> levelRoleEntry : levelRoles.entrySet()) {
                            if (guild.getRoleById(levelRoleEntry.getValue()) == null)
                                internalGuild.unsetLevelRole(levelRoleEntry.getKey(), levelRoleEntry.getValue(), guild, author);
                            else {
                                var levelRole = guild.getRoleById(levelRoleEntry.getValue());
                                if (levelRole != null)
                                    sb.append(StringFormat.pushWithWhitespace(String.valueOf(levelRoleEntry.getKey()), 5))
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
                        eb.setColor(new User(event.getAuthor().getIdLong()).getColor(guild, author));
                        eb.setAuthor(LanguageHandler.get(lang, "levelrole_current"), null, guild.getIconUrl());
                        eb.setDescription(sb.toString());
                        event.reply(eb.build());
                        break;

                    case "refresh":
                        List<Member> members = event.getGuild().getMembers();
                        for (Member member : members) {
                            var internalUser = new User(member.getUser().getIdLong());
                            var currentExp = internalUser.getExp(guild.getIdLong(), guild, author);
                            var currentLevel = Parser.getLevelFromExp(currentExp);
                            List<Long> roleIds = internalGuild.getLevelRolesForLevel(currentLevel, guild, author);

                            for (Long roleId : roleIds)
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

                // Statistics.
                new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
                new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Servant.cpuPool);
    }
}
