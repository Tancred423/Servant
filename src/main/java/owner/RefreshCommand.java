// Author: Tancred423 (https://github.com/Tancred423)
package owner;

import moderation.guild.Guild;
import moderation.user.User;
import net.dv8tion.jda.core.Permission;
import servant.Log;
import utilities.Constants;
import utilities.Parser;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RefreshCommand extends Command {
    public RefreshCommand() {
        this.name = "refresh";
        this.aliases = new String[0];
        this.help = "Refreshes achievements and more";
        this.category = new Category("Owner");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = true;
        this.cooldown = Constants.OWNER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[0];
    }

    @Override
    protected void execute(CommandEvent event) {
        CompletableFuture.runAsync(() -> {
            var success = assignLevelAchievement(event);
            success = clearLevelAchievements(event);

            if (success) event.reactSuccess();
            else event.reactWarning();

            // Statistics.
            try {
                new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
                if (event.getGuild() != null) new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
            }
        });
    }

    private boolean assignLevelAchievement(CommandEvent event) {
        var success = true;
        var guilds = event.getJDA().getGuilds();
        for (var guild : guilds) {
            if (guild.getIdLong() == 264445053596991498L) continue;
            var members = guild.getMembers();
            for (var member : members) {
                try {
                    var internalUser = new User(member.getUser().getIdLong());

                    var level = Parser.getLevelFromExp(internalUser.getExp(guild.getIdLong()));
                    if (level >= 100) internalUser.setAchievement("level100", 100);
                    else if (level >= 90) internalUser.setAchievement("level90", 90);
                    else if (level >= 80) internalUser.setAchievement("level80", 80);
                    else if (level >= 70) internalUser.setAchievement("level70", 70);
                    else if (level >= 60) internalUser.setAchievement("level60", 60);
                    else if (level >= 50) internalUser.setAchievement("level50", 50);
                    else if (level >= 40) internalUser.setAchievement("level40", 40);
                    else if (level >= 30) internalUser.setAchievement("level30", 30);
                    else if (level >= 20) internalUser.setAchievement("level20", 20);
                    else if (level >= 10) internalUser.setAchievement("level10", 10);

                    // Extra
                    if (level >= 69) internalUser.setAchievement("level69", 69);
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                    success = false;
                }
            }
        }
        return success;
    }

    private boolean clearLevelAchievements(CommandEvent event) {
        var success = true;
        var users = event.getJDA().getUsers();
        try {
            for (var user : users) {
                var internalUser = new User(user.getIdLong());
                var levelAchievements = internalUser.getLevelAchievements();
                if (!levelAchievements.isEmpty()) {
                    List<Integer> levels = new LinkedList<>();
                    for (var achievement : levelAchievements) levels.add(Integer.parseInt(achievement.substring(5)));
                    switch (Collections.max(levels)) {
                        // Falls through the cases and deletes all the lower levels.
                        // Level 69 stays as a joke.
                        case 100: internalUser.unsetAchievement("level90");
                        case 90: internalUser.unsetAchievement("level80");
                        case 80: internalUser.unsetAchievement("level70");
                        case 70: internalUser.unsetAchievement("level60");
                        case 60: internalUser.unsetAchievement("level50");
                        case 50: internalUser.unsetAchievement("level40");
                        case 40: internalUser.unsetAchievement("level30");
                        case 30: internalUser.unsetAchievement("level20");
                        case 20: internalUser.unsetAchievement("level10");
                    }
                }
            }
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            success = false;
        }
        return success;
    }
}
