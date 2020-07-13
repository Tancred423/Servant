package commands.dashboard;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.Permission;
import servant.MyMessage;
import utilities.Constants;
import utilities.MessageUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

public class DashboardCommand extends Command {
    public DashboardCommand() {
        this.name = "dashboard";
        this.aliases = new String[] {
                "serversetup",
                "setup",
                "createembed", "embed",
                "user", "master",
                "autorole",
                "bestofimage", "image",
                "bestofquote", "quote",
                "join",
                "joinmessage", "joinmsg",
                "leave",
                "leavemessage", "leavemsg",
                "levelrole", "rankrole",
                "livestream", "stream",
                "log",
                "mediaonlychannel", "mediaonly",
                "reactionrole",
                "server", "guild",
                "toggle",
                "voicelobby", "lobby",
                "bio"
        };
        this.help = "Shows dashboard command message";
        this.category = new Category("Dashboard");
        this.arguments = null;
        this.hidden = true;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.modCommand = false;
        this.cooldown = Constants.MOD_COOLDOWN;
        this.cooldownScope = CooldownScope.GUILD;
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var lang = LanguageHandler.getLanguage(event);

        var invoke = MessageUtil.removePrefix(event.getJDA(), event.getGuild().getIdLong(), true, event.getMessage().getContentDisplay().split(" ")[0]);
        if (invoke.equals("dashboard")) {
            // Simple dashboard link
            event.reply(String.format(LanguageHandler.get(lang, "dashboard_link"), Constants.WEBSITE_DASHBOARD));
        } else {
            // Say that the command is discontinued
            var myMessage = new MyMessage(event.getJDA(), event.getGuild().getIdLong(), event.getTextChannel().getIdLong(), event.getMessage().getIdLong());
            myMessage.setContent(event.getMessage().getContentDisplay());

            if (!myMessage.isCustomCommand())
                event.reply(String.format(LanguageHandler.get(lang, "dashboard_discontinued"), Constants.WEBSITE_DASHBOARD));
        }
    }
}
