package freeToAll;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import moderation.guild.Guild;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Emote;
import servant.Log;

import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

public class BaguetteCommand extends Command {
    public BaguetteCommand() {
        this.name = "baguette";
        this.help = "how big is your baguette?";
        this.category = new Category("Free to all");
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        // Enabled?
        try {
            if (event.getGuild() != null) if (!new Guild(event.getGuild().getIdLong()).getToggleStatus("baguette")) return;
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }

        int random = ThreadLocalRandom.current().nextInt(0, 6) + 1; // 1, 2, 3, 4 or 5
        int coinflip = ThreadLocalRandom.current().nextInt(0, 10) + 1; // 1 - 10
        if (coinflip == 10) random += ThreadLocalRandom.current().nextInt(0, 30) + 1; // Rarely a huge schlong
        if (coinflip ==  1) random = 0; // Rarely a veri smol one

        Emote baguette1 = event.getJDA().getGuildById(599222484134264852L).getEmoteById(600597900568952835L);
        Emote baguette2 = event.getJDA().getGuildById(599222484134264852L).getEmoteById(600597898668802048L);
        Emote baguette3 = event.getJDA().getGuildById(599222484134264852L).getEmoteById(600597897804775436L);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(baguette1.getAsMention());
        for (int i = 0; i < random; i++) stringBuilder.append(baguette2.getAsMention());
        stringBuilder.append(baguette3.getAsMention());

        event.reply(stringBuilder.toString());

        // Statistics.
        try {
            new servant.User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            if (event.getGuild() != null) new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }
    }
}
