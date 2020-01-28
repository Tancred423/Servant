// Author: Tancred423 (https://github.com/Tancred423)
package fun.flip;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import net.dv8tion.jda.api.Permission;
import utilities.Constants;
import utilities.StringFormat;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

public class FlipCommand extends Command {
    public FlipCommand() {
        this.name = "flip";
        this.aliases = new String[0];
        this.help = "Flip a user.";
        this.category = new Category("Fun");
        this.arguments = "@user";
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.NICKNAME_MANAGE
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var message = event.getMessage();
        var lang = LanguageHandler.getLanguage(event);
        var p = GuildHandler.getPrefix(event);

        if (message.getMentionedMembers().isEmpty()) {
            var description = LanguageHandler.get(lang, "flip_description");
            var usage = String.format(LanguageHandler.get(lang, "flip_usage"), p, name);
            var hint = String.format(LanguageHandler.get(lang, "flip_hint"), p);
            event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
            return;
        }

        var mentioned = message.getMentionedMembers().get(0);
        var effectiveName = mentioned.getEffectiveName();
        var flipped = StringFormat.flipString(effectiveName);
        event.reply("(╯°□°)╯︵ " + flipped);
        var selfMember = event.getGuild().getMemberById(event.getSelfUser().getIdLong());
        if (selfMember != null && selfMember.canInteract(mentioned))
            event.getGuild().modifyNickname(mentioned, flipped).queue();
    }
}
