// Author: Tancred423 (https://github.com/Tancred423)
package fun;

import files.language.LanguageHandler;
import moderation.user.User;
import moderation.guild.Guild;
import net.dv8tion.jda.core.Permission;
import owner.blacklist.Blacklist;
import utilities.Emote;
import servant.Log;
import utilities.Constants;
import moderation.toggle.Toggle;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
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
        this.botPermissions = new Permission[]{Permission.MESSAGE_EXT_EMOJI};
    }

    @Override
    protected void execute(CommandEvent event) {
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, name)) return;
            if (Blacklist.isBlacklisted(event.getAuthor(), event.getGuild())) return;

            var random = ThreadLocalRandom.current().nextInt(1, 100 + 1); // 1-100

            if (random > 30) random = ThreadLocalRandom.current().nextInt(1, 5 + 1); // 1-5 | 70% Chance
            else if (random > 25) random = 0;  // 0 | 5% Chance
            else if (random > 20) random = ThreadLocalRandom.current().nextInt(6, 10 + 1); // 6-10 | 5% Chance (1% chance each)
            else if (random > 15) random = ThreadLocalRandom.current().nextInt(11, 20 + 1); // 11-20 | 5% Chance (0,555% chance each)
            else if (random > 10) random = ThreadLocalRandom.current().nextInt(21, 30 + 1); // 21-30 | 5% Chance (0,555% chance each)
            else if (random > 5) random = ThreadLocalRandom.current().nextInt(31, 40 + 1); // 31-40 | 5% Chance (0,555% chance each)
            else random = ThreadLocalRandom.current().nextInt(41, 50 + 1); // 41-50 | 5% Chance (0,555% chance each)

            String baguette1;
            String baguette2;
            String baguette3;

            try {
                baguette1 = Emote.getEmoteMention("baguette1");
                baguette2 = Emote.getEmoteMention("baguette2");
                baguette3 = Emote.getEmoteMention("baguette3");
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                return;
            }

            var lang = LanguageHandler.getLanguage(event, name);
            String baguettes = baguette1 +
                    String.valueOf(baguette2).repeat(random) +
                    baguette3 +
                    "\n(" + random + (random == 50 ? " - " + LanguageHandler.get(lang, "baguette_50") : (random == 49 ? " - " + LanguageHandler.get(lang, "baguette_49") : "")) + ")";

            event.reply(baguettes);


            try {
                // Baguette Counter
                var internalUser = new User(event.getAuthor().getIdLong());
                var baguette = internalUser.getBaguette();
                if (baguette.isEmpty()) {
                    internalUser.setBaguette(random, 1);
                } else {
                    var currentBaguette = baguette.entrySet().iterator().next();
                    if (random > currentBaguette.getKey()) {
                        internalUser.setBaguette(random, 1);
                    } else if (random == currentBaguette.getKey()) {
                        internalUser.setBaguette(random, currentBaguette.getValue() + 1);
                    }
                }

                // Statistics.
                internalUser.incrementFeatureCount(name.toLowerCase());
                if (event.getGuild() != null) new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
            }
        });
    }
}
