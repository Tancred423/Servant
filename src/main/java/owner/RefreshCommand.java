// Author: Tancred423 (https://github.com/Tancred423)
package owner;

import moderation.guild.Guild;
import moderation.user.User;
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

                var guild = event.getGuild();
                var author = event.getAuthor();
                // Statistics.
                new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
                if (event.getGuild() != null)
                    new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Servant.cpuPool);
    }

    private void assignLevelAchievement(CommandEvent event) {
        var guilds = event.getJDA().getGuilds();
        for (var guild : guilds) {
            if (guild.getIdLong() == 264445053596991498L) continue;
            var members = guild.getMembers();
            for (var member : members) {
                var internalUser = new User(member.getUser().getIdLong());

                var level = Parser.getLevelFromExp(internalUser.getExp(guild.getIdLong(), guild, member.getUser()));
                if (level >= 100) internalUser.setAchievement("level100", 100, guild, member.getUser());
                else if (level >= 90) internalUser.setAchievement("level90", 90, guild, member.getUser());
                else if (level >= 80) internalUser.setAchievement("level80", 80, guild, member.getUser());
                else if (level >= 70) internalUser.setAchievement("level70", 70, guild, member.getUser());
                else if (level >= 60) internalUser.setAchievement("level60", 60, guild, member.getUser());
                else if (level >= 50) internalUser.setAchievement("level50", 50, guild, member.getUser());
                else if (level >= 40) internalUser.setAchievement("level40", 40, guild, member.getUser());
                else if (level >= 30) internalUser.setAchievement("level30", 30, guild, member.getUser());
                else if (level >= 20) internalUser.setAchievement("level20", 20, guild, member.getUser());
                else if (level >= 10) internalUser.setAchievement("level10", 10, guild, member.getUser());

                // Extra
                if (level >= 69) internalUser.setAchievement("nicelevel", 69, guild, member.getUser());
            }
        }
    }

    private void clearLevelAchievements(CommandEvent event) {
        var users = event.getJDA().getUsers();
        for (var user : users) {
            var guild = event.getGuild();
            var internalUser = new User(user.getIdLong());
            var levelAchievements = internalUser.getLevelAchievements(guild, user);
            if (!levelAchievements.isEmpty()) {
                List<Integer> levels = new LinkedList<>();
                for (var achievement : levelAchievements) levels.add(Integer.parseInt(achievement.substring(5)));
                switch (Collections.max(levels)) {
                    // Falls through the cases and deletes all the lower levels.
                    // Level 69 stays as a joke.
                    case 100: internalUser.unsetAchievement("level90", guild, user);
                    case 90: internalUser.unsetAchievement("level80", guild, user);
                    case 80: internalUser.unsetAchievement("level70", guild, user);
                    case 70: internalUser.unsetAchievement("level60", guild, user);
                    case 60: internalUser.unsetAchievement("level50", guild, user);
                    case 50: internalUser.unsetAchievement("level40", guild, user);
                    case 40: internalUser.unsetAchievement("level30", guild, user);
                    case 30: internalUser.unsetAchievement("level20", guild, user);
                    case 20: internalUser.unsetAchievement("level10", guild, user);
                }
            }
        }
    }
}
