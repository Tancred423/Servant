// Author: Tancred423 (https://github.com/Tancred423)
package easteregg;

import moderation.user.Master;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import utilities.Emote;
import utilities.MessageHandler;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

public class ThanksCommand extends Command {
    public ThanksCommand() {
        this.name = "thanks";
        this.aliases = new String[] { "thank", "thankyou" };
        this.help = "Thank Servant for her work.";
        this.category = new Category("EasterEggs");
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
        var guild = event.getGuild();
        var user = event.getAuthor();

        event.reply("You're welcome " + Emote.getEmoteMention(event.getJDA(), "love", guild, user));
        checkAchievement(user, event.getMessage());
    }

    private void checkAchievement(User user, Message message) {
        var master = new Master(user);
        var achievements = master.getAchievements();
        if (!achievements.containsKey("kind")) {
            master.setAchievement("kind", 10);
            new MessageHandler().reactAchievement(message);
        }
    }
}
