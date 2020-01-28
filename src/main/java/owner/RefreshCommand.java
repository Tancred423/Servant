// Author: Tancred423 (https://github.com/Tancred423)
package owner;

import moderation.user.Master;
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
        this.cooldown = Constants.OWNER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
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
        }, Servant.threadPool);
    }

    private void assignLevelAchievement(CommandEvent event) {
        var guilds = event.getJDA().getGuilds();
        for (var guild : guilds) {
            if (guild.getIdLong() == 264445053596991498L) continue;
            var members = guild.getMembers();
            for (var member : members) {
                var internalUser = new Master(member.getUser());

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
                if (level >= 69) internalUser.setAchievement("nicelevel", 69);
            }
        }
    }

    private void clearLevelAchievements(CommandEvent event) {
        var users = event.getJDA().getUsers();
        for (var user : users) {
            var master = new Master(user);
            var levelAchievements = master.getLevelAchievements();
            if (!levelAchievements.isEmpty()) {
                List<Integer> levels = new LinkedList<>();
                for (var achievement : levelAchievements) levels.add(Integer.parseInt(achievement.substring(5)));
                switch (Collections.max(levels)) {
                    // Falls through the cases and deletes all the lower levels.
                    // Level 69 stays as a joke.
                    case 100: master.unsetAchievement("level90");
                    case 90: master.unsetAchievement("level80");
                    case 80: master.unsetAchievement("level70");
                    case 70: master.unsetAchievement("level60");
                    case 60: master.unsetAchievement("level50");
                    case 50: master.unsetAchievement("level40");
                    case 40: master.unsetAchievement("level30");
                    case 30: master.unsetAchievement("level20");
                    case 20: master.unsetAchievement("level10");
                }
            }
        }
    }
}
