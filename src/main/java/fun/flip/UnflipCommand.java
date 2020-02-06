// Author: Tancred423 (https://github.com/Tancred423)
package fun.flip;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import net.dv8tion.jda.api.Permission;
import utilities.Constants;
import utilities.MessageUtil;
import utilities.StringUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

public class UnflipCommand extends Command {
    public UnflipCommand() {
        this.name = "unflip";
        this.aliases = new String[0];
        this.help = "Unflip a user.";
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
            var description = LanguageHandler.get(lang, "unflip_description");
            var usage = String.format(LanguageHandler.get(lang, "unflip_usage"), p, name);
            var hint = String.format(LanguageHandler.get(lang, "unflip_hint"), p);
            event.reply(MessageUtil.createUsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint));
            return;
        }

        var mentioned = message.getMentionedMembers().get(0);
        var effectiveName = mentioned.getEffectiveName();
        var flipped = StringUtil.flipString(effectiveName);
        event.reply(flipped + "ノ( º _ ºノ)");
        var selfMember = event.getGuild().getMemberById(event.getSelfUser().getIdLong());
        if (selfMember != null && selfMember.canInteract(mentioned))
            event.getGuild().modifyNickname(mentioned, flipped).queue();
    }
}
