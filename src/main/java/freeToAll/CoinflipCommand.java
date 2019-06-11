package freeToAll;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.doc.standard.RequiredPermissions;
import com.jagrosh.jdautilities.examples.doc.Author;
import net.dv8tion.jda.core.Permission;
import servant.Guild;
import servant.Log;
import servant.User;

import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

@CommandInfo(
        name = {"Coinflip", "Cointoss"},
        description = "Flip a coin!",
        usage = "coinflip"
)
@RequiredPermissions({Permission.MESSAGE_EMBED_LINKS})
@Author("Tancred")
public class CoinflipCommand extends Command {
    public CoinflipCommand() {
        this.name = "coinflip";
        this.aliases = new String[]{"cf", "cointoss", "ct"};
        this.help = "returns head or tail.";
        this.category = new Category("Free to all");
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        int coinflip = ThreadLocalRandom.current().nextInt(0, 2); // 0 or 1.
        if (coinflip == 0) event.reply("Head!");
        else event.reply("Tail!");

        // Statistics.
        try {
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            if (event.getGuild() != null) new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }
    }
}