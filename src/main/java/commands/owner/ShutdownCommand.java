// Modified by: Tancred423 (https://github.com/Tancred423)
package commands.owner;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.Permission;
import utilities.Constants;
import utilities.EmoteUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;
import zJdaUtilsLib.com.jagrosh.jdautilities.examples.doc.Author;

@Author("John Grosh (jagrosh)")
public class ShutdownCommand extends Command {
    public ShutdownCommand() {
        this.name = "shutdown";
        this.aliases = new String[0];
        this.help = "Shuts down bot.";
        this.category = new Category("Owner");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = true;
        this.modCommand = false;
        this.cooldown = Constants.OWNER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var jda = event.getJDA();
        var lang = LanguageHandler.getLanguage(event);
        System.out.println("Shutting down...");
        event.getChannel().sendMessage(String.format(LanguageHandler.get(lang, "shutdown_goodnight"), EmoteUtil.getEmote(jda, "love").getAsMention())).queue(
                s -> shutdown(event), f -> shutdown(event)
        );
    }

    private void shutdown(CommandEvent event) {
        event.getJDA().shutdown();
        System.exit(0);
    }
}
