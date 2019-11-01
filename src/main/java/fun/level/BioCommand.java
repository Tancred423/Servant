// Author: Tancred423 (https://github.com/Tancred423)
package fun.level;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.core.Permission;
import utilities.Constants;
import utilities.Parser;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.util.concurrent.CompletableFuture;

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
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, "profile")) return;
            var args = event.getArgs();

            var guild = event.getGuild();
            var author = event.getAuthor();
            var lang = LanguageHandler.getLanguage(event);

            if (Parser.isSqlInjection(args)) {
                event.reactError();
                return;
            }

            if (args.length() > 50) {
                event.replyError(LanguageHandler.get(lang, "bio_maxlength"));
                return;
            }

            new User(event.getAuthor().getIdLong()).setBio(args, guild, author);
            event.reactSuccess();

            // Statistics.
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
            if (event.getGuild() != null) new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
        });
    }
}
