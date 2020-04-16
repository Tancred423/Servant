// Author: Tancred423 (https://github.com/Tancred423)
package fun;

import files.language.LanguageHandler;
import moderation.user.Master;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import utilities.Constants;
import utilities.MessageUtil;
import utilities.Parser;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

public class AvatarCommand extends Command {
    public AvatarCommand() {
        this.name = "avatar";
        this.aliases = new String[] { "ava" };
        this.help = "Returns mentioned user's avatar.";
        this.category = new Category("Fun");
        this.arguments = "@user";
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected void execute(CommandEvent event) {
        var lang = LanguageHandler.getLanguage(event);

        var user = event.getAuthor();
        var master = new Master(user);

        var message = event.getMessage();
        var channel = message.getChannel();

        User mentioned;
        String description;

        if (event.getArgs().isEmpty()) {
            mentioned = user;
            description = String.format(LanguageHandler.get(lang, "avatar_self"), user.getAsMention());
        } else {
            // Check mentioned user.
            if (Parser.hasMentionedUser(event.getMessage())) {
                mentioned = message.getMentionedUsers().get(0);
                description = String.format(LanguageHandler.get(lang, "avatar_stolen"), user.getAsMention(), mentioned.getAsMention());
            } else {
                event.reply(LanguageHandler.get(lang, "invalid_mention"));
                return;
            }
        }


        var avaUrl = mentioned.getAvatarUrl() + "?size=2048";

        new MessageUtil().sendEmbed(
                channel,
                master.getColor(),
                "Avatar", null, null,
                null,
                null,
                description,
                null,
                avaUrl,
                null,
                null
        );
    }
}
