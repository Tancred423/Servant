// Author: Tancred423 (https://github.com/Tancred423)
package fun;

import files.language.LanguageHandler;
import moderation.user.Master;
import net.dv8tion.jda.api.Permission;
import utilities.Constants;
import utilities.EmoteUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.util.concurrent.ThreadLocalRandom;

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
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EXT_EMOJI
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var random = ThreadLocalRandom.current().nextInt(1, 100 + 1); // 1-100

        if      (random > 30) random = ThreadLocalRandom.current().nextInt( 1,  5 + 1); //  1- 5 | 70% Chance
        else if (random > 25) random = 0;                                                             //  0    |  5% Chance
        else if (random > 20) random = ThreadLocalRandom.current().nextInt( 6, 10 + 1); //  6-10 |  5% Chance (1% chance each)
        else if (random > 15) random = ThreadLocalRandom.current().nextInt(11, 20 + 1); // 11-20 |  5% Chance (0,555% chance each)
        else if (random > 10) random = ThreadLocalRandom.current().nextInt(21, 30 + 1); // 21-30 |  5% Chance (0,555% chance each)
        else if (random >  5) random = ThreadLocalRandom.current().nextInt(31, 40 + 1); // 31-40 |  5% Chance (0,555% chance each)
        else                  random = ThreadLocalRandom.current().nextInt(41, 50 + 1); // 41-50 |  5% Chance (0,555% chance each)

        var jda = event.getJDA();
        var guild = event.getGuild();
        var user = event.getAuthor();

        var baguette1 = EmoteUtil.getEmoteMention(jda, "baguette1");
        var baguette2 = EmoteUtil.getEmoteMention(jda, "baguette2");
        var baguette3 = EmoteUtil.getEmoteMention(jda, "baguette3");

        var lang = LanguageHandler.getLanguage(event);
        var baguettes = baguette1 +
                String.valueOf(baguette2).repeat(random) +
                baguette3 +
                "\n(" + random + (random == 50 ? " - " + LanguageHandler.get(lang, "baguette_50") : (random == 49 ? " - " + LanguageHandler.get(lang, "baguette_49") : "")) + ")";

        event.reply(baguettes);

        // Baguette Counter
        var internalUser = new Master(event.getAuthor());
        var baguette = internalUser.getBaguette();
        if (baguette == null) internalUser.setBaguette(random, 1);
        else {
            if (random > baguette.getKey()) internalUser.setBaguette(random, 1);
            else if (random == baguette.getKey()) internalUser.setBaguette(random, baguette.getValue() + 1);
        }
    }
}
