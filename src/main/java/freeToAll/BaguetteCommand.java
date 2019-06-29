package freeToAll;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
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
            if (event.getGuild() != null) if (!new servant.Guild(event.getGuild().getIdLong()).getToggleStatus("baguette")) return;
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }

        int random = ThreadLocalRandom.current().nextInt(0, 6) + 1; // 1, 2, 3, 4 or 5
        int coinflip = ThreadLocalRandom.current().nextInt(0, 10) + 1; // 1 - 10
        if (coinflip == 10) random += ThreadLocalRandom.current().nextInt(0, 30) + 1; // Rarely a huge schlong
        if (coinflip ==  1) random = 0; // Rarely a veri smol one

        Emote baguette1 = event.getJDA().getGuildById(436925371577925642L).getEmoteById(594643668535607306L);
        Emote baguette2 = event.getJDA().getGuildById(436925371577925642L).getEmoteById(594643668405583886L);
        Emote baguette3 = event.getJDA().getGuildById(436925371577925642L).getEmoteById(594643666723667988L);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(baguette1.getAsMention());
        for (int i = 0; i < random; i++) stringBuilder.append(baguette2.getAsMention());
        stringBuilder.append(baguette3.getAsMention());

        event.reply(stringBuilder.toString());
    }
}
