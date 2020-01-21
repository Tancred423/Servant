package fun.level;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import utilities.MessageHandler;
import utilities.Parser;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Level {
    public static Map<Guild, Map<User, ZonedDateTime>> guildCds = new HashMap<>();

    public static int getLevel(long userId, long guildId, Guild guild, User user) {
        return Parser.getLevelFromExp(new moderation.user.User(userId).getExp(guildId, guild, user));
    }

    public static List<String> checkForNewRole(int level, MessageReceivedEvent event, String lang) {
        var guild = event.getGuild();
        var internalGuild = new moderation.guild.Guild(guild.getIdLong());
        var roleIds = internalGuild.getLevelRole(level, event.getGuild(), event.getAuthor());
        var roles = new ArrayList<String>();
        var member = event.getMember();
        var selfMember = event.getGuild().getMemberById(event.getJDA().getSelfUser().getIdLong());
        if (member != null && selfMember != null)
            for (var roleId : roleIds)
                if (guild.getRoleById(roleId) != null
                        && selfMember.hasPermission(Permission.MANAGE_ROLES)) {
                    try {
                        var rolesToAdd = new ArrayList<Role>();
                        rolesToAdd.add(guild.getRoleById(roleId));

                        guild.modifyMemberRoles(member, rolesToAdd, null).queue();
                        var role = guild.getRoleById(roleId);
                        if (role != null) roles.add(role.getName());
                    } catch (HierarchyException e) {
                        var role = guild.getRoleById(roleId);
                        if (role != null) roles.add(String.format(LanguageHandler.get(lang, "level_hierarchy"), role.getName()));
                    }
                }
        return roles;
    }

    public static void checkForAchievements(int level, MessageReceivedEvent event) {
        var guild = event.getGuild();
        var author = event.getAuthor();
        var internalAuthor = new moderation.user.User(author.getIdLong());
        var message = event.getMessage();

        if (level >= 10) {
            if (!internalAuthor.hasAchievement("level10", guild, author) && !hasHigherLevelAchievement(internalAuthor, 10, guild, author)) {
                internalAuthor.setAchievement("level10", 10, guild, author);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 20) {
            if (!internalAuthor.hasAchievement("level20", guild, author) && !hasHigherLevelAchievement(internalAuthor, 20, guild, author)) {
                internalAuthor.setAchievement("level20", 20, guild, author);
                deleteLowerAchievements(internalAuthor, 20, guild, author);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 30) {
            if (!internalAuthor.hasAchievement("level30", guild, author) && !hasHigherLevelAchievement(internalAuthor, 30, guild, author)) {
                internalAuthor.setAchievement("level30", 30, guild, author);
                deleteLowerAchievements(internalAuthor, 30, guild, author);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 40) {
            if (!internalAuthor.hasAchievement("level40", guild, author) && !hasHigherLevelAchievement(internalAuthor, 40, guild, author)) {
                internalAuthor.setAchievement("level40", 40, guild, author);
                deleteLowerAchievements(internalAuthor, 40, guild, author);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 50) {
            if (!internalAuthor.hasAchievement("level50", guild, author) && !hasHigherLevelAchievement(internalAuthor, 50, guild, author)) {
                internalAuthor.setAchievement("level50", 50, guild, author);
                deleteLowerAchievements(internalAuthor, 50, guild, author);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 60) {
            if (!internalAuthor.hasAchievement("level60", guild, author) && !hasHigherLevelAchievement(internalAuthor, 60, guild, author)) {
                internalAuthor.setAchievement("level60", 60, guild, author);
                deleteLowerAchievements(internalAuthor, 60, guild, author);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 69) {
            if (!internalAuthor.hasAchievement("nicelevel", guild, author) && !hasHigherLevelAchievement(internalAuthor, 69, guild, author)) {
                internalAuthor.setAchievement("nicelevel", 69, guild, author);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 70) {
            if (!internalAuthor.hasAchievement("level70", guild, author) && !hasHigherLevelAchievement(internalAuthor, 70, guild, author)) {
                internalAuthor.setAchievement("level70", 70, guild, author);
                deleteLowerAchievements(internalAuthor, 70, guild, author);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 80) {
            if (!internalAuthor.hasAchievement("level80", guild, author) && !hasHigherLevelAchievement(internalAuthor, 80, guild, author)) {
                internalAuthor.setAchievement("level80", 80, guild, author);
                deleteLowerAchievements(internalAuthor, 80, guild, author);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 90) {
            if (!internalAuthor.hasAchievement("level90", guild, author) && !hasHigherLevelAchievement(internalAuthor, 90, guild, author)) {
                internalAuthor.setAchievement("level90", 90, guild, author);
                deleteLowerAchievements(internalAuthor, 90, guild, author);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 100) {
            if (!internalAuthor.hasAchievement("level100", guild, author) && !hasHigherLevelAchievement(internalAuthor, 100, guild, author)) {
                internalAuthor.setAchievement("level100", 100, guild, author);
                deleteLowerAchievements(internalAuthor, 100, guild, author);
                new MessageHandler().reactAchievement(message);
            }
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean hasHigherLevelAchievement(moderation.user.User internalUser, int level, Guild guild, User user) {
        var achievements = internalUser.getLevelAchievements(guild, user);
        String highestLevelAchievement = (achievements.isEmpty() ? null : achievements.get(0));

        if (highestLevelAchievement != null) {
            int highestLevel = Integer.parseInt(highestLevelAchievement.substring(5));
            return highestLevel > level;
        } else return false;
    }

    public static void deleteLowerAchievements(moderation.user.User internalUser, int level, Guild guild, User user) {
        if (level > 90) internalUser.unsetAchievement("level90", guild, user);
        if (level > 80) internalUser.unsetAchievement("level80", guild, user);
        if (level > 70) internalUser.unsetAchievement("level70", guild, user);
        if (level > 60) internalUser.unsetAchievement("level60", guild, user);
        if (level > 50) internalUser.unsetAchievement("level50", guild, user);
        if (level > 40) internalUser.unsetAchievement("level40", guild, user);
        if (level > 30) internalUser.unsetAchievement("level30", guild, user);
        if (level > 20) internalUser.unsetAchievement("level20", guild, user);
        if (level > 10) internalUser.unsetAchievement("level10", guild, user);
    }
}
