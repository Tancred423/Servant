// Author: Tancred423 (https://github.com/Tancred423)
package freeToAll;

import moderation.guild.Guild;
import net.dv8tion.jda.core.Permission;
import servant.Emote;
import servant.Log;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

public class BaguetteCommand extends Command {
    public BaguetteCommand() {
        this.name = "baguette";
        this.aliases = new String[0];
        this.help = "How big is your baguette? \uD83D\uDE0F";
        this.category = new Category("Free to all");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.cooldown = 5;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[]{Permission.MESSAGE_EXT_EMOJI};
    }

    @Override
    protected void execute(CommandEvent event) {
        // Enabled?
        try {
            if (event.getGuild() != null) if (!new Guild(event.getGuild().getIdLong()).getToggleStatus("baguette")) return;
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }

        var random = ThreadLocalRandom.current().nextInt(0, 6) + 1; // 1, 2, 3, 4 or 5
        var coinflip = ThreadLocalRandom.current().nextInt(0, 10) + 1; // 1 - 10
        if (coinflip == 10) random += ThreadLocalRandom.current().nextInt(0, 30) + 1; // Rarely a huge schlong
        if (coinflip ==  1) random = 0; // Rarely a veri smol one

        net.dv8tion.jda.core.entities.Emote baguette1;
        net.dv8tion.jda.core.entities.Emote baguette2;
        net.dv8tion.jda.core.entities.Emote baguette3;

        try {
            baguette1 = Emote.getEmote("baguette1");
            baguette2 = Emote.getEmote("baguette2");
            baguette3 = Emote.getEmote("baguette3");
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            return;
        }

        var stringBuilder = new StringBuilder();
        stringBuilder.append(baguette1.getAsMention());
        for (int i = 0; i < random; i++) stringBuilder.append(baguette2.getAsMention());
        stringBuilder.append(baguette3.getAsMention());

        event.reply(stringBuilder.toString());

        // Statistics.
        try {
            new servant.User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            if (event.getGuild() != null) new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }
    }
}
