package fun.level;

import moderation.guild.Guild;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.core.Permission;
import servant.Log;
import utilities.Constants;
import utilities.Parser;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;

public class BioCommand extends Command {
    public BioCommand() {
        this.name = "bio";
        this.aliases = new String[0];
        this.help = "Bio in profile";
        this.category = new Category("Fun");
        this.arguments = "[your text]";
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[0];
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!Toggle.isEnabled(event, "profile")) return;
        var args = event.getArgs();

        if (!Parser.isSqlInjection(args) && args.length() <= 2000) {
            try {
                new User(event.getAuthor().getIdLong()).setBio(args);
                event.reactSuccess();
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                event.reactWarning();
            }
        } else event.reactError();

        // Statistics.
        try {
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            if (event.getGuild() != null) new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }
    }
}
