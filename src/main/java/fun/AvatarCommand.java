// Author: Tancred423 (https://github.com/Tancred423)
package fun;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.user.Master;
import net.dv8tion.jda.api.Permission;
import utilities.Constants;
import utilities.MessageHandler;
import utilities.Parser;
import utilities.UsageEmbed;
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
        var p = GuildHandler.getPrefix(event);

        if (event.getArgs().isEmpty()) {
            var description = LanguageHandler.get(lang, "avatar_description");
            var usage = String.format(LanguageHandler.get(lang, "avatar_usage"), p, name);
            event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, null).getEmbed());
            return;
        }

        // Check mentioned user.
        if (!Parser.hasMentionedUser(event.getMessage())) {
            event.reply(LanguageHandler.get(lang, "invalid_mention"));
            return;
        }

        var user = event.getAuthor();
        var master = new Master(user);

        var message = event.getMessage();
        var channel = message.getChannel();
        var mentioned = message.getMentionedUsers().get(0);

        new MessageHandler().sendEmbed(
                channel,
                master.getColor(),
                "Avatar", null, null,
                null,
                null,
                String.format(LanguageHandler.get(lang, "avatar_stolen"), user.getAsMention(), mentioned.getAsMention()),
                null,
                mentioned.getAvatarUrl(),
                null,
                null
        );
    }
}
