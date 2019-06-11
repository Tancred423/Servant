package servant;

import moderation.JoinListener;
import moderation.*;
import freeToAll.CoinflipCommand;
import freeToAll.StealAvatarCommand;
import level.LevelCommand;
import level.LevelListener;
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
import settings.UserSettingsCommand;
import toggle.ToggleCommand;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.IOException;

public class Servant {
    public static JDA jda;
    public static ConfigFile config;

    public static void main(String[] args) throws IOException, LoginException {
        config = new ConfigFile();
        if (config.isMissing()) {
            System.out.println("The bot was shut down.");
            return;
        }

        EventWaiter waiter = new EventWaiter(); // Has to be added to JDABuilder.
        CommandClientBuilder client = new CommandClientBuilder(); // Command management by JDA utilities.

        client.useDefaultGame();
        client.setOwnerId(config.getBotOwnerId());
        client.setEmojis("✅", "⚠", "❌"); // ✅, ⚠, ❌.
        client.setPrefix(config.getDefaultPrefix());

        client.addCommands(
                // Default JDA utilities commands.
                new AboutCommand(Color.decode(config.getDefaultColorCode()), "your multifuntional bot",
                        new String[]{"Cool Commands", "Nice Examples", "Lots of fun!"},
                        Permission.ADMINISTRATOR), // !about
                new GuildlistCommand(waiter), // !guildlist
                new PingCommand(), // !ping
                new ShutdownCommand(), // !shutdown

                // Owner
                new AddGifCommand(), // !addgif [interaction] [gif url]

                // Admin
                new ToggleCommand(), // !toggle [feature] [on|off|status]

                // Moderation
                new AutoroleCommand(), // !autorole [@role|role ID]
                new ClearCommand(), // !clear [1 - 100]
                new FileOnlyChannelCommand(), // !fo [set|unset] #channel
                new JoinCommand(), // !join [set|unset|status] [set: #channel]

                // Settings
                new UserSettingsCommand(), // !user [set|unset|show] [feature] [optional parameter like color code]

                // Free to all
                new StealAvatarCommand(), // !avatar @user
                new CoinflipCommand(), // !coinflip

                // Interaction commands.
                new HugCommand(), // !hug @user
                new SlapCommand(), // !slap @user
                new BegCommand(), // !beg @user
                new CookieCommand(), // !cookie @user
                new KissCommand(), // !kiss @user
                new PatCommand(), // !pat @user
                new HighfiveCommand(), // !highfive @user
                new DabCommand(), // !dab @user

                // Level
                new LevelCommand() // level [optional: leaderboard|@user]
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
                .addEventListener(new FileOnlyChannelListener())
                .addEventListener(new JoinListener())

                // Start.
                .build();
    }
}
