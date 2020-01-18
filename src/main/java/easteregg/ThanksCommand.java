// Author: Tancred423 (https://github.com/Tancred423)
package easteregg;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.user.User;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import owner.blacklist.Blacklist;
import servant.Servant;
import utilities.Emote;
import utilities.MessageHandler;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.util.concurrent.CompletableFuture;

public class ThanksCommand extends Command {
    public ThanksCommand() {
        this.name = "thanks";
        this.aliases = new String[] { "thank", "thankyou" };
        this.help = "Thank Servant for her work.";
        this.category = null;
        this.arguments = null;
        this.hidden = true;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.cooldown = 5;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[0];
    }

    @Override
    protected void execute(CommandEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                if (Blacklist.isBlacklisted(event.getAuthor(), event.getGuild())) return;

                var guild = event.getGuild();
                var user = event.getAuthor();
                var lang = LanguageHandler.getLanguage(event);

                event.reply("You're welcome " + Emote.getEmoteMention(event.getJDA(), "love", guild, user));
                checkAchievement(new User(event.getAuthor().getIdLong()), event.getMessage(), guild, user, lang);

                // Statistics.
                new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, user);
                if (event.getGuild() != null)
                    new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Servant.cpuPool);
    }

    private void checkAchievement(User internalUser, Message message, net.dv8tion.jda.api.entities.Guild guild, net.dv8tion.jda.api.entities.User user, String lang) {
        var achievements = internalUser.getAchievements(guild, user);
        if (!achievements.containsKey("kind")) {
            internalUser.setAchievement("kind", 10, guild, user);
            new MessageHandler().reactAchievement(message);
        }
    }
}
