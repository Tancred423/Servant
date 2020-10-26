// Author: Tancred423 (https://github.com/Tancred423)
package commands.fun.profile;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import servant.MyGuild;
import servant.MyUser;
import utilities.MessageUtil;
import utilities.Parser;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Level {
    public static Map<Guild, Map<User, Instant>> guildCds = new HashMap<>();

    public static int getLevel(User user, long guildId) {
        return Parser.getLevelFromExp(new MyUser(user).getLevelTotalExp(guildId));
    }

    public static List<String> checkForNewRole(int level, MessageReceivedEvent event, String lang) throws HierarchyException {
        var guild = event.getGuild();
        var myGuild = new MyGuild(guild);
        var roleIds = myGuild.getLevelRole(level);
        var member = event.getMember();
        var botMember = event.getGuild().getMemberById(event.getJDA().getSelfUser().getIdLong());
        var roles = new ArrayList<String>();
        if (member != null && botMember != null) {
            var rolesToAdd = new ArrayList<Role>();

            for (var roleId : roleIds) {
                var role = guild.getRoleById(roleId);
                if (role != null) {
                    rolesToAdd.add(role);
                    roles.add(role.getName());
                }
            }

            if (botMember.hasPermission(Permission.MANAGE_ROLES)) {
                try {
                    guild.modifyMemberRoles(member, rolesToAdd, null).queue();
                } catch (HierarchyException e) {
                    throw new HierarchyException(LanguageHandler.get(lang, "level_hierarchy"));
                }
            }
        }

        return roles;
    }

    public static void checkForAchievements(int level, MessageReceivedEvent event) {
        var user = event.getAuthor();
        var myUser = new MyUser(user);
        var message = event.getMessage();

        if (level >= 10) {
            if (!myUser.hasAchievement("level10") && !hasHigherLevelAchievement(myUser, 10)
                    && new MyGuild(event.getGuild()).featureIsEnabled("achievements")) {
                myUser.setAchievement("level10");
                new MessageUtil().reactAchievement(message);
            }
        }

        if (level >= 20) {
            if (!myUser.hasAchievement("level20") && !hasHigherLevelAchievement(myUser, 20)
                    && new MyGuild(event.getGuild()).featureIsEnabled("achievements")) {
                myUser.setAchievement("level20");
                deleteLowerAchievements(myUser, 20);
                new MessageUtil().reactAchievement(message);
            }
        }

        if (level >= 30) {
            if (!myUser.hasAchievement("level30") && !hasHigherLevelAchievement(myUser, 30)
                    && new MyGuild(event.getGuild()).featureIsEnabled("achievements")) {
                myUser.setAchievement("level30");
                deleteLowerAchievements(myUser, 30);
                new MessageUtil().reactAchievement(message);
            }
        }

        if (level >= 40) {
            if (!myUser.hasAchievement("level40") && !hasHigherLevelAchievement(myUser, 40)
                    && new MyGuild(event.getGuild()).featureIsEnabled("achievements")) {
                myUser.setAchievement("level40");
                deleteLowerAchievements(myUser, 40);
                new MessageUtil().reactAchievement(message);
            }
        }

        if (level >= 50) {
            if (!myUser.hasAchievement("level50") && !hasHigherLevelAchievement(myUser, 50)
                    && new MyGuild(event.getGuild()).featureIsEnabled("achievements")) {
                myUser.setAchievement("level50");
                deleteLowerAchievements(myUser, 50);
                new MessageUtil().reactAchievement(message);
            }
        }

        if (level >= 60) {
            if (!myUser.hasAchievement("level60") && !hasHigherLevelAchievement(myUser, 60)
                    && new MyGuild(event.getGuild()).featureIsEnabled("achievements")) {
                myUser.setAchievement("level60");
                deleteLowerAchievements(myUser, 60);
                new MessageUtil().reactAchievement(message);
            }
        }

        if (level >= 69) {
            if (!myUser.hasAchievement("nicelevel") && !hasHigherLevelAchievement(myUser, 69)
                    && new MyGuild(event.getGuild()).featureIsEnabled("achievements")) {
                myUser.setAchievement("nicelevel");
                new MessageUtil().reactAchievement(message);
            }
        }

        if (level >= 70) {
            if (!myUser.hasAchievement("level70") && !hasHigherLevelAchievement(myUser, 70)
                    && new MyGuild(event.getGuild()).featureIsEnabled("achievements")) {
                myUser.setAchievement("level70");
                deleteLowerAchievements(myUser, 70);
                new MessageUtil().reactAchievement(message);
            }
        }

        if (level >= 80) {
            if (!myUser.hasAchievement("level80") && !hasHigherLevelAchievement(myUser, 80)
                    && new MyGuild(event.getGuild()).featureIsEnabled("achievements")) {
                myUser.setAchievement("level80");
                deleteLowerAchievements(myUser, 80);
                new MessageUtil().reactAchievement(message);
            }
        }

        if (level >= 90) {
            if (!myUser.hasAchievement("level90") && !hasHigherLevelAchievement(myUser, 90)
                    && new MyGuild(event.getGuild()).featureIsEnabled("achievements")) {
                myUser.setAchievement("level90");
                deleteLowerAchievements(myUser, 90);
                new MessageUtil().reactAchievement(message);
            }
        }

        if (level >= 100) {
            if (!myUser.hasAchievement("level100") && !hasHigherLevelAchievement(myUser, 100)
                    && new MyGuild(event.getGuild()).featureIsEnabled("achievements")) {
                myUser.setAchievement("level100");
                deleteLowerAchievements(myUser, 100);
                new MessageUtil().reactAchievement(message);
            }
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean hasHigherLevelAchievement(MyUser internalUser, int level) {
        var achievements = internalUser.getLevelAchievements();
        String highestLevelAchievement = (achievements.isEmpty() ? null : achievements.get(0));

        if (highestLevelAchievement != null) {
            int highestLevel = Integer.parseInt(highestLevelAchievement.substring(5));
            return highestLevel > level;
        } else return false;
    }

    public static void deleteLowerAchievements(MyUser internalUser, int level) {
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
