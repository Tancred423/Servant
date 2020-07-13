package commands.fun.bubbleWrap;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import servant.MyUser;
import utilities.Constants;
import utilities.ImageUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class BubbleWrapCommand extends Command {
    public BubbleWrapCommand() {
        this.name = "bubblewrap";
        this.aliases = new String[] { "bubble", "wrap", "pop" };
        this.help = "POP POP POP!";
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
                Permission.MESSAGE_EXT_EMOJI
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var lang = LanguageHandler.getLanguage(event);

        var pop = "||POP||";
        var easterEgg = "||[POP](" + ImageUtil.getUrl(event.getJDA(), "bubblewrap") + ")||";
        var sb = new StringBuilder();

        var random = ThreadLocalRandom.current().nextInt(0, 187);

        for (var i = 0; i < 187; i++) {
            if (i == random) sb.append(easterEgg);
            else sb.append(pop);
            sb.append(" ");
        }
        var bubbleWrap = sb.toString().trim();

        var myUser = new MyUser(event.getAuthor());
        var eb = new EmbedBuilder();
        eb.setColor(Color.decode(myUser.getColorCode()))
                .setAuthor(LanguageHandler.get(lang, "bubblewrap_title"), null, null)
                .setTitle(LanguageHandler.get(lang, "bubblewrap_subtitle"))
                .setDescription(bubbleWrap)
                .setFooter(LanguageHandler.get(lang, "bubblewrap_footer"));

        event.reply(eb.build());
    }
}
