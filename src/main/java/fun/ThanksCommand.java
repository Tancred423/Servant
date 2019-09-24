package fun;

import moderation.user.User;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import servant.Log;
import utilities.Emote;
import utilities.MessageHandler;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;

public class ThanksCommand extends Command {
    public ThanksCommand(String botName) {
        this.name = "thanks";
        this.aliases = new String[]{"thank", "thankyou"};
        this.help = "Thank " + botName + " for her work.";
        this.category = new Category("Fun");
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
        try {
            event.reply("You're welcome " + Emote.getEmoteMention("love"));
            checkAchievement(new User(event.getAuthor().getIdLong()), event.getMessage());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
        }
    }

    private void checkAchievement(User internalUser, Message message) throws SQLException {
        var achievements = internalUser.getAchievements();
        if (!achievements.containsKey("kind")) {
            internalUser.setAchievement("kind", 10);
            new MessageHandler().reactAchievement(message);
        }
    }
}
