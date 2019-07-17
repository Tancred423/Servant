package servant;

import moderation.*;
import moderation.autorole.AutoroleCommand;
import moderation.autorole.AutoroleListener;
import moderation.joinLeaveNotify.JoinLeaveNotifyCommand;
import moderation.mediaOnlyChannel.MediaOnlyChannelCommand;
import moderation.mediaOnlyChannel.MediaOnlyChannelListener;
import freeToAll.embed.EmbedCommand;
import moderation.reactionRoles.ReactionRoleCommand;
import moderation.reactionRoles.ReactionRoleListener;
import patreon.PatreonListener;
import zChatLib.Bot;
import config.ToggleFile;
import freeToAll.BaguetteCommand;
import moderation.joinLeaveNotify.JoinLeaveNotifyListener;
import freeToAll.CoinflipCommand;
import freeToAll.AvatarCommand;
import freeToAll.level.LevelCommand;
import freeToAll.level.LevelListener;
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
import freeToAll.UserCommand;
import chatbot.ChatbotListener;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.IOException;

public class Servant {
    public static JDA jda;
    public static ConfigFile config;
    static ToggleFile toggle;
    public static Bot chatBot;

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
                new AboutCommand(Color.decode(config.getDefaultColorCode()), "your multifuntional bot.",
                        new String[]{"Moderation Tools", "Informative Commands", "Interactive Features", "Shit-post Features"},
                        Permission.ADMINISTRATOR),
                new GuildlistCommand(waiter), // BOT OWNER
                new PingCommand(),
                new ShutdownCommand(), // BOT OWNER

                // Moderation
                new AutoroleCommand(), // MANAGE ROLES
                new ClearCommand(), // MANAGE MESSAGES
                new GuildCommand(), // ADMINISTRATOR
                new JoinLeaveNotifyCommand(), // MANAGE CHANNEL
                new MediaOnlyChannelCommand(), // MANAGE CHANNEL
                new ReactionRoleCommand(), // MANAGE CHANNEL, MANAGE ROLES
                new ToggleCommand(), // ADMINISTRATOR

                // Free to all
                new AvatarCommand(),
                new BaguetteCommand(),
                new CoinflipCommand(),
                new EmbedCommand(waiter),
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
                .addEventListener(new AutoroleListener())
                .addEventListener(new ChatbotListener())
                .addEventListener(new InviteKickListener())
                .addEventListener(new JoinLeaveNotifyListener())
                .addEventListener(new LevelListener())
                .addEventListener(new MediaOnlyChannelListener())
                .addEventListener(new PatreonListener())
                .addEventListener(new ReactionRoleListener())
                .addEventListener(new ReadyListener())

                // Start.
                .build();
    }
}
