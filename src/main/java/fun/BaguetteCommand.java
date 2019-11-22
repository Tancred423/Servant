// Author: Tancred423 (https://github.com/Tancred423)
package fun;

import files.language.LanguageHandler;
import moderation.user.User;
import moderation.guild.Guild;
import net.dv8tion.jda.api.Permission;
import owner.blacklist.Blacklist;
import utilities.Emote;
import utilities.Constants;
import moderation.toggle.Toggle;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

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
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EXT_EMOJI
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                if (!Toggle.isEnabled(event, name)) return;
                if (Blacklist.isBlacklisted(event.getAuthor(), event.getGuild())) return;

                var random = ThreadLocalRandom.current().nextInt(1, 100 + 1); // 1-100

                if (random > 30) random = ThreadLocalRandom.current().nextInt(1, 5 + 1); // 1-5 | 70% Chance
                else if (random > 25) random = 0;  // 0 | 5% Chance
                else if (random > 20)
                    random = ThreadLocalRandom.current().nextInt(6, 10 + 1); // 6-10 | 5% Chance (1% chance each)
                else if (random > 15)
                    random = ThreadLocalRandom.current().nextInt(11, 20 + 1); // 11-20 | 5% Chance (0,555% chance each)
                else if (random > 10)
                    random = ThreadLocalRandom.current().nextInt(21, 30 + 1); // 21-30 | 5% Chance (0,555% chance each)
                else if (random > 5)
                    random = ThreadLocalRandom.current().nextInt(31, 40 + 1); // 31-40 | 5% Chance (0,555% chance each)
                else random = ThreadLocalRandom.current().nextInt(41, 50 + 1); // 41-50 | 5% Chance (0,555% chance each)

                var guild = event.getGuild();
                var author = event.getAuthor();
                var jda = event.getJDA();

                var baguette1 = Emote.getEmoteMention(jda, "baguette1", guild, author);
                var baguette2 = Emote.getEmoteMention(jda, "baguette2", guild, author);
                var baguette3 = Emote.getEmoteMention(jda, "baguette3", guild, author);

                var lang = LanguageHandler.getLanguage(event);
                var baguettes = baguette1 +
                        String.valueOf(baguette2).repeat(random) +
                        baguette3 +
                        "\n(" + random + (random == 50 ? " - " + LanguageHandler.get(lang, "baguette_50") : (random == 49 ? " - " + LanguageHandler.get(lang, "baguette_49") : "")) + ")";

                event.reply(baguettes);


                // Baguette Counter
                var internalUser = new User(event.getAuthor().getIdLong());
                var baguette = internalUser.getBaguette(guild, author);
                if (baguette.isEmpty()) {
                    internalUser.setBaguette(random, 1, guild, author);
                } else {
                    var currentBaguette = baguette.entrySet().iterator().next();
                    if (random > currentBaguette.getKey()) {
                        internalUser.setBaguette(random, 1, guild, author);
                    } else if (random == currentBaguette.getKey()) {
                        internalUser.setBaguette(random, currentBaguette.getValue() + 1, guild, author);
                    }
                }

                // Statistics.
                internalUser.incrementFeatureCount(name.toLowerCase(), guild, author);
                if (event.getGuild() != null)
                    new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
