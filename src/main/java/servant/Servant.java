package servant;

import freeToAll.*;
import freeToAll.profile.ProfileCommand;
import moderation.stream.StreamCommand;
import moderation.stream.StreamListener;
import vote.QuickvoteCommand;
import vote.QuickvoteEndListener;
import vote.QuickvoteMultipleVoteListener;
import moderation.*;
import moderation.autorole.AutoroleCommand;
import moderation.autorole.AutoroleListener;
import moderation.guild.GuildCommand;
import moderation.guild.GuildManager;
import moderation.joinLeaveNotify.JoinLeaveNotifyCommand;
import moderation.lobby.LobbyCommand;
import moderation.lobby.LobbyListener;
import moderation.mediaOnlyChannel.MediaOnlyChannelCommand;
import moderation.mediaOnlyChannel.MediaOnlyChannelListener;
import freeToAll.embed.EmbedCommand;
import moderation.reactionRoles.ReactionRoleCommand;
import moderation.reactionRoles.ReactionRoleListener;
import patreon.PatreonListener;
import zChatLib.Bot;
import config.ToggleFile;
import moderation.joinLeaveNotify.JoinLeaveNotifyListener;
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
import chatbot.ChatbotListener;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.IOException;

public class Servant {
    public static JDA jda;
    public static ConfigFile config;
    public static ToggleFile toggle;
    public static Bot chatBot;

    public static void main(String[] args) throws IOException, LoginException {
        config = new ConfigFile();
        if (config.isMissing()) {
            System.out.println("The bot was shut down.");
            return;
        }

        toggle = new ToggleFile();

        var waiter = new EventWaiter(); // Has to be added to JDABuilder.
        var client = new CommandClientBuilder(); // Command management by JDA utilities.

        client.setOwnerId(config.getBotOwnerId());
        client.setEmojis("✅", "⚠", "❌"); // ✅, ⚠, ❌.
        client.setPrefix(config.getDefaultPrefix());
        client.setGuildSettingsManager(new GuildManager());
        client.addCommands(
                // Default JDA utilities commands.
                new AboutCommand(Color.decode(config.getDefaultColorCode()), "your multifuntional bot.",
                        new String[]{"Moderation Tools", "Informative Commands", "Interactive Features", "Shit-post Features"},
                        Permission.ADMINISTRATOR),
                new GuildlistCommand(waiter),
                new PingCommand(),
                new ShutdownCommand(),

                // Moderation
                new AutoroleCommand(),
                new ClearCommand(),
                new GuildCommand(),
                new JoinLeaveNotifyCommand(),
                new LobbyCommand(),
                new MediaOnlyChannelCommand(),
                new ReactionRoleCommand(),
                new StreamCommand(),
                new ToggleCommand(),

                // Free to all
                new AvatarCommand(),
                new BaguetteCommand(),
                new CoinflipCommand(),
                new EmbedCommand(waiter),
                new LevelCommand(),
                new LoveCommand(),
                new ProfileCommand(),
                new UserCommand(),

                // Vote
                new QuickvoteCommand(),

                // Interaction
                new AddGifCommand(),
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
                .addEventListener(new LobbyListener())
                .addEventListener(new MediaOnlyChannelListener())
                .addEventListener(new PatreonListener())
                .addEventListener(new ReactionRoleListener())
                .addEventListener(new ReadyListener())
                .addEventListener(new StreamListener())
                .addEventListener(new QuickvoteEndListener())
                .addEventListener(new QuickvoteMultipleVoteListener())

                // Start.
                .build();
    }
}
