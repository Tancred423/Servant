package servant;

import config.ToggleFile;
import freeToAll.BaguetteCommand;
import moderation.JoinListener;
import moderation.*;
import freeToAll.CoinflipCommand;
import freeToAll.AvatarCommand;
import freeToAll.LevelCommand;
import freeToAll.LevelListener;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.examples.command.AboutCommand;
import com.jagrosh.jdautilities.examples.command.GuildlistCommand;
import com.jagrosh.jdautilities.examples.command.PingCommand;
import com.jagrosh.jdautilities.examples.command.ShutdownCommand;
import config.ConfigFile;
import interaction.*;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Game;
import moderation.GuildCommand;
import freeToAll.UserCommand;
import toggle.ToggleCommand;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.IOException;

public class Servant {
    public static JDA jda;
    public static ConfigFile config;
    public static ToggleFile toggle;

    public static void main(String[] args) throws IOException, LoginException {
        config = new ConfigFile();
        if (config.isMissing()) {
            System.out.println("The bot was shut down.");
            return;
        }

        toggle = new ToggleFile();

        EventWaiter waiter = new EventWaiter(); // Has to be added to JDABuilder.
        CommandClientBuilder client = new CommandClientBuilder(); // Command management by JDA utilities.

        client.setGame(Game.playing(config.getDefaultPrefix() + "help | v" + config.getBotVersion()));
        client.setOwnerId(config.getBotOwnerId());
        client.setEmojis("✅", "⚠", "❌"); // ✅, ⚠, ❌.
        client.setPrefix(config.getDefaultPrefix());

        client.addCommands(
                // Default JDA utilities commands.
                new AboutCommand(Color.decode(config.getDefaultColorCode()), "your multifuntional bot",
                        new String[]{"Moderation Tools", "Informative Commands", "Interactive Features", "Shit-post Features"},
                        Permission.ADMINISTRATOR),
                new GuildlistCommand(waiter), // BOT OWNER
                new PingCommand(),
                new ShutdownCommand(), // BOT OWNER

                // Moderation
                new AutoroleCommand(), // MANAGE ROLES
                new ClearCommand(), // MANAGE MESSAGES
                new GuildCommand(), // ADMINISTRATOR
                new JoinCommand(), // MANAGE CHANNEL
                new MediaOnlyChannelCommand(), // MANAGE CHANNEL
                new ToggleCommand(), // ADMINISTRATOR

                // Free to all
                new AvatarCommand(),
                new BaguetteCommand(),
                new CoinflipCommand(),
                new LevelCommand(),
                new UserCommand(),

                // Interaction commands.
                new AddGifCommand(), // BOT OWNER
                new BegCommand(),
                new CookieCommand(),
                new DabCommand(),
                new HighfiveCommand(),
                new HugCommand(),
                new KissCommand(),
                new PatCommand(),
                new SlapCommand()
        );

        new JDABuilder(AccountType.BOT)
                .setToken(config.getBotToken())

                // While loading.
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setGame(Game.playing("loading..."))

                // Add event listeners.
                .addEventListener(waiter)
                .addEventListener(client.build())
                .addEventListener(new ReadyListener())
                .addEventListener(new LevelListener())
                .addEventListener(new AutoroleListener())
                .addEventListener(new MediaOnlyChannelListener())
                .addEventListener(new JoinListener())

                // Start.
                .build();
    }
}
