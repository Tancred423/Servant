// Author: Tancred423 (https://github.com/Tancred423)
package commands.fun.mirror;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.Permission;
import servant.MyGuild;
import utilities.Constants;
import utilities.MessageUtil;
import utilities.StringUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

public class MirrorCommand extends Command {
    public MirrorCommand() {
        this.name = "mirror";
        this.aliases = new String[]{"unmirror"};
        this.help = "Mirror a user or text";
        this.category = new Category("Fun");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.modCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.botPermissions = new Permission[]{
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.NICKNAME_MANAGE
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var message = event.getMessage();
        var lang = LanguageHandler.getLanguage(event);
        var p = new MyGuild(event.getGuild()).getPrefix();

        if (event.getArgs().isEmpty()) {
            var description = LanguageHandler.get(lang, "mirror_description");
            var usage = String.format(LanguageHandler.get(lang, "mirror_usage"), p, name, p, aliases[0]);
            event.reply(MessageUtil.createUsageEmbed(name, event.getAuthor(), lang, description, aliases, usage, null));
            return;
        }

        if (message.getMentionedMembers().size() == 0) {
            // Flip text
            var mirrored = StringUtil.mirror(event.getArgs());
            event.reply(event.getMessage().getContentRaw().toLowerCase().contains("unmirror") ? mirrored + "☐ノ( º _ ºノ)" : "(ノ°□°)ノ☐ " + mirrored);
        } else {
            // Flip user
            var mentioned = message.getMentionedMembers().get(0);
            var effectiveName = mentioned.getEffectiveName();
            var mirrored = StringUtil.mirror(effectiveName);
            event.reply(event.getMessage().getContentRaw().toLowerCase().contains("unmirror") ? mirrored + "☐ノ( º _ ºノ)" : "(ノ°□°)ノ☐ " + mirrored);
            var selfMember = event.getGuild().getMemberById(event.getSelfUser().getIdLong());
            if (selfMember != null && selfMember.canInteract(mentioned))
                event.getGuild().modifyNickname(mentioned, mirrored).queue();
        }
    }
}
