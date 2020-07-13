// Author: Tancred423 (https://github.com/Tancred423)
package commands.fun.baguette;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.Permission;
import servant.MyUser;
import utilities.Constants;
import utilities.EmoteUtil;
import utilities.MathUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

public class BaguetteCommand extends Command {
    public BaguetteCommand() {
        this.name = "baguette";
        this.aliases = new String[0];
        this.help = "How big is your baguette? \uD83D\uDE0F";
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
        var random = MathUtil.randomBiased(50, 3.0f); // 1 is linear. The higher the rarer are high numbers.

        var jda = event.getJDA();

        var baguette1 = EmoteUtil.getEmote(jda, "baguette1").getAsMention();
        var baguette2 = EmoteUtil.getEmote(jda, "baguette2").getAsMention();
        var baguette3 = EmoteUtil.getEmote(jda, "baguette3").getAsMention();

        var lang = LanguageHandler.getLanguage(event);
        var baguettes = baguette1 +
                baguette2.repeat(random) +
                baguette3 +
                "\n(" + random + (random == 50 ? " - " + LanguageHandler.get(lang, "baguette_50") : (random == 49 ? " - " + LanguageHandler.get(lang, "baguette_49") : "")) + ")";

        event.reply(baguettes);

        // Baguette Counter
        var internalUser = new MyUser(event.getAuthor());
        var baguette = internalUser.getBaguette();
        if (baguette == null) internalUser.setBaguette(random, 1);
        else {
            if (random > baguette.getSize()) internalUser.setBaguette(random, 1);
            else if (random == baguette.getSize()) internalUser.setBaguette(random, baguette.getCounter() + 1);
        }
    }
}
