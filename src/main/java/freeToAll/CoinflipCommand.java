package freeToAll;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;
import moderation.guild.Guild;
import servant.Log;
import servant.User;

import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

public class CoinflipCommand extends Command {
    public CoinflipCommand() {
        this.name = "coinflip";
        this.aliases = new String[]{"cf", "cointoss", "ct"};
        this.help = "returns head or tail";
        this.category = new Category("Free to all");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.cooldown = 5;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[0];
    }

    @Override
    protected void execute(CommandEvent event) {
        // Enabled?
        try {
            if (event.getGuild() != null) if (!new Guild(event.getGuild().getIdLong()).getToggleStatus("coinflip")) return;
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }

        var coinflip = ThreadLocalRandom.current().nextInt(0, 2); // 0 or 1.
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
