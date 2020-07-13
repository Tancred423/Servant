package commands.dashboard;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.Permission;
import servant.MyMessage;
import utilities.Constants;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

public class DiscontinuedCommand extends Command {
    public DiscontinuedCommand() {
        this.name = "role";
        this.aliases = new String[] {
                "serverinfo", "guildinfo"
        };
        this.help = "Shows discontinued command message";
        this.category = new Category("Dashboard");
        this.arguments = null;
        this.hidden = true;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.modCommand = false;
        this.cooldown = Constants.MOD_COOLDOWN;
        this.cooldownScope = CooldownScope.GUILD;
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var lang = LanguageHandler.getLanguage(event);

        var isFromGuild = event.getGuild() != null;
        var myMessage = new MyMessage(event.getJDA(), isFromGuild ? event.getGuild().getIdLong() : 0, isFromGuild ? event.getTextChannel().getIdLong() : 0, event.getMessage().getIdLong());
        myMessage.setContent(event.getMessage().getContentDisplay());

        if (!myMessage.isCustomCommand())
            event.reply(LanguageHandler.get(lang, "discontinued"));
    }
}
