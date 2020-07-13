// Author: Tancred423 (https://github.com/Tancred423)
package commands.dashboard;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.Permission;
import servant.MyMessage;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

public class LeaderboardCommand extends Command {
    public LeaderboardCommand() {
        this.name = "leaderboard";
        this.aliases = new String[] { "levelranking" };
        this.help = "Displays the level ranking of the current server.";
        this.category = new Category("Fun");
        this.arguments = null;
        this.hidden = true;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.modCommand = false;
        this.cooldown = 0;
        this.cooldownScope = CooldownScope.USER;
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
            event.reply(String.format(LanguageHandler.get(lang, "leaderboard_website"), event.getGuild().getIdLong()));
    }
}
