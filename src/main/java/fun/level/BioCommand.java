// Author: Tancred423 (https://github.com/Tancred423)
package fun.level;

import files.language.LanguageHandler;
import moderation.user.Master;
import net.dv8tion.jda.api.Permission;
import utilities.Constants;
import utilities.Parser;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

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
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var args = event.getArgs();

        var user = event.getAuthor();
        var master = new Master(user);
        var lang = LanguageHandler.getLanguage(event);

        if (Parser.isSqlInjection(args)) {
            event.reactError();
            return;
        }

        if (args.length() > 30) {
            event.replyError(LanguageHandler.get(lang, "bio_maxlength"));
            return;
        }

        master.setBio(args);
        event.reactSuccess();
    }
}
