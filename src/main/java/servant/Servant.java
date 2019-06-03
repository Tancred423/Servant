package servant;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.examples.command.AboutCommand;
import com.jagrosh.jdautilities.examples.command.GuildlistCommand;
import com.jagrosh.jdautilities.examples.command.PingCommand;
import com.jagrosh.jdautilities.examples.command.ShutdownCommand;
import config.ConfigFile;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.IOException;

public class Servant {
    public static void main(String[] args) throws IOException, LoginException {
        ConfigFile config = new ConfigFile();
        if (config.isMissing()) {
            System.out.println("The bot was shut down.");
            return;
        }

        EventWaiter waiter = new EventWaiter(); // Has to be added to JDABuilder.
        CommandClientBuilder client = new CommandClientBuilder(); // Command management by JDA utilities.

        client.useDefaultGame();
        client.setOwnerId(config.getBotOwnerId());
        client.setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26"); // 😃, 😮, 😦.
        client.setPrefix(config.getDefaultPrefix());

        client.addCommands(
                // Default JDA utilities commands.
                new AboutCommand(Color.decode(config.getDefaultColorCode()), "your multifuntional bot",
                        new String[] { "Cool Commands", "Nice Examples", "Lots of fun!" },
                        Permission.ADMINISTRATOR), // !about
                new GuildlistCommand(waiter), // !guildlist
                new PingCommand(), // !ping
                new ShutdownCommand() // !shutdown
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

                // Start.
                .build();
    }
}
