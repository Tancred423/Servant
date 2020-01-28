package fun.level;

import files.language.LanguageHandler;
import moderation.guild.Server;
import moderation.user.Master;
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

    public static int getLevel(User user, long guildId) {
        return Parser.getLevelFromExp(new Master(user).getExp(guildId));
    }

    public static List<String> checkForNewRole(int level, MessageReceivedEvent event, String lang) {
        var guild = event.getGuild();
        var server = new Server(guild);
        var roleIds = server.getLevelRole(level);
        var roles = new ArrayList<String>();
        var member = event.getMember();
        var botMember = event.getGuild().getMemberById(event.getJDA().getSelfUser().getIdLong());
        if (member != null && botMember != null)
            for (var roleId : roleIds)
                if (guild.getRoleById(roleId) != null
                        && botMember.hasPermission(Permission.MANAGE_ROLES)) {
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
        var user = event.getAuthor();
        var master = new Master(user);
        var message = event.getMessage();

        if (level >= 10) {
            if (!master.hasAchievement("level10") && !hasHigherLevelAchievement(master, 10)) {
                master.setAchievement("level10", 10);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 20) {
            if (!master.hasAchievement("level20") && !hasHigherLevelAchievement(master, 20)) {
                master.setAchievement("level20", 20);
                deleteLowerAchievements(master, 20);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 30) {
            if (!master.hasAchievement("level30") && !hasHigherLevelAchievement(master, 30)) {
                master.setAchievement("level30", 30);
                deleteLowerAchievements(master, 30);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 40) {
            if (!master.hasAchievement("level40") && !hasHigherLevelAchievement(master, 40)) {
                master.setAchievement("level40", 40);
                deleteLowerAchievements(master, 40);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 50) {
            if (!master.hasAchievement("level50") && !hasHigherLevelAchievement(master, 50)) {
                master.setAchievement("level50", 50);
                deleteLowerAchievements(master, 50);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 60) {
            if (!master.hasAchievement("level60") && !hasHigherLevelAchievement(master, 60)) {
                master.setAchievement("level60", 60);
                deleteLowerAchievements(master, 60);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 69) {
            if (!master.hasAchievement("nicelevel") && !hasHigherLevelAchievement(master, 69)) {
                master.setAchievement("nicelevel", 69);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 70) {
            if (!master.hasAchievement("level70") && !hasHigherLevelAchievement(master, 70)) {
                master.setAchievement("level70", 70);
                deleteLowerAchievements(master, 70);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 80) {
            if (!master.hasAchievement("level80") && !hasHigherLevelAchievement(master, 80)) {
                master.setAchievement("level80", 80);
                deleteLowerAchievements(master, 80);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 90) {
            if (!master.hasAchievement("level90") && !hasHigherLevelAchievement(master, 90)) {
                master.setAchievement("level90", 90);
                deleteLowerAchievements(master, 90);
                new MessageHandler().reactAchievement(message);
            }
        }

        if (level >= 100) {
            if (!master.hasAchievement("level100") && !hasHigherLevelAchievement(master, 100)) {
                master.setAchievement("level100", 100);
                deleteLowerAchievements(master, 100);
                new MessageHandler().reactAchievement(message);
            }
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean hasHigherLevelAchievement(Master internalUser, int level) {
        var achievements = internalUser.getLevelAchievements();
        String highestLevelAchievement = (achievements.isEmpty() ? null : achievements.get(0));

        if (highestLevelAchievement != null) {
            int highestLevel = Integer.parseInt(highestLevelAchievement.substring(5));
            return highestLevel > level;
        } else return false;
    }

    public static void deleteLowerAchievements(Master internalUser, int level) {
        if (level > 90) internalUser.unsetAchievement("level90");
        if (level > 80) internalUser.unsetAchievement("level80");
        if (level > 70) internalUser.unsetAchievement("level70");
        if (level > 60) internalUser.unsetAchievement("level60");
        if (level > 50) internalUser.unsetAchievement("level50");
        if (level > 40) internalUser.unsetAchievement("level40");
        if (level > 30) internalUser.unsetAchievement("level30");
        if (level > 20) internalUser.unsetAchievement("level20");
        if (level > 10) internalUser.unsetAchievement("level10");
    }
}
