// Author: Tancred423 (https://github.com/Tancred423)
package commands.fun.avatar;

import servant.MyUser;
import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import utilities.Constants;
import utilities.Parser;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.awt.*;

public class AvatarCommand extends Command {
    public AvatarCommand() {
        this.name = "avatar";
        this.aliases = new String[] { "ava" };
        this.help = "Steal someone's avatar";
        this.category = new Category("Fun");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.modCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected void execute(CommandEvent event) {
        var lang = LanguageHandler.getLanguage(event);

        var user = event.getAuthor();
        var myUser = new MyUser(user);

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

        // To get max image resolution. Also works for images smaller than 2048px.
        var avaUrl = mentioned.getAvatarUrl() + "?size=2048";

        channel.sendMessage(
                new EmbedBuilder()
                        .setColor(Color.decode(myUser.getColorCode()))
                        .setTitle(LanguageHandler.get(lang, "avatar_avatar"))
                        .setDescription(description)
                        .setImage(avaUrl)
                        .build()
        ).queue();
    }
}
