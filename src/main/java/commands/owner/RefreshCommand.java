// Author: Tancred423 (https://github.com/Tancred423)
package commands.owner;

import servant.MyGuild;
import servant.MyUser;
import net.dv8tion.jda.api.Permission;
import servant.Servant;
import utilities.Constants;
import utilities.Parser;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

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
        this.modCommand = false;
        this.cooldown = Constants.OWNER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                assignLevelAchievement(event);
                clearLevelAchievements(event);
                event.reactSuccess();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Servant.fixedThreadPool);
    }

    private void assignLevelAchievement(CommandEvent event) {
        var shardManager = event.getJDA().getShardManager();
        if (shardManager != null) {
            var guilds = shardManager.getGuilds();
            for (var guild : guilds) {
                if (guild.getIdLong() == 264445053596991498L) continue;
                if (!new MyGuild(event.getGuild()).featureIsEnabled("achievements")) continue;
                var members = guild.getMembers();
                for (var member : members) {
                    var myUser = new MyUser(member.getUser());

                    var level = Parser.getLevelFromExp(myUser.getExp(guild.getIdLong()));
                    if (level >= 100) myUser.setAchievement("level100");
                    else if (level >= 90) myUser.setAchievement("level90");
                    else if (level >= 80) myUser.setAchievement("level80");
                    else if (level >= 70) myUser.setAchievement("level70");
                    else if (level >= 60) myUser.setAchievement("level60");
                    else if (level >= 50) myUser.setAchievement("level50");
                    else if (level >= 40) myUser.setAchievement("level40");
                    else if (level >= 30) myUser.setAchievement("level30");
                    else if (level >= 20) myUser.setAchievement("level20");
                    else if (level >= 10) myUser.setAchievement("level10");

                    // Extra
                    if (level >= 69) myUser.setAchievement("nicelevel");
                }
            }
        }
    }

    private void clearLevelAchievements(CommandEvent event) {
        var shardManager = event.getJDA().getShardManager();
        if (shardManager != null) {
            var users = shardManager.getUsers();
            for (var user : users) {
                var myUser = new MyUser(user);
                var levelAchievements = myUser.getLevelAchievements();
                if (!levelAchievements.isEmpty()) {
                    List<Integer> levels = new LinkedList<>();
                    for (var achievement : levelAchievements) levels.add(Integer.parseInt(achievement.substring(5)));
                    switch (Collections.max(levels)) {
                        // Falls through the cases and deletes all the lower levels.
                        // Level 69 is extra
                        case 100:
                            myUser.unsetAchievement("level90");
                        case 90:
                            myUser.unsetAchievement("level80");
                        case 80:
                            myUser.unsetAchievement("level70");
                        case 70:
                            myUser.unsetAchievement("level60");
                        case 60:
                            myUser.unsetAchievement("level50");
                        case 50:
                            myUser.unsetAchievement("level40");
                        case 40:
                            myUser.unsetAchievement("level30");
                        case 30:
                            myUser.unsetAchievement("level20");
                        case 20:
                            myUser.unsetAchievement("level10");
                    }
                }
            }
        }
    }
}
