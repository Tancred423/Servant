// Author: Tancred423 (https://github.com/Tancred423)
package servant;

import easteregg.EasterEggsListener;
import files.language.LanguageHandler;
import fun.*;
import fun.embed.CreateEmbedCommand;
import fun.embed.EditEmbedCommand;
import fun.flip.FlipCommand;
import fun.flip.UnflipCommand;
import fun.level.BioCommand;
import fun.level.LevelRoleCommand;
import fun.level.ProfileCommand;
import fun.randomAnimal.BirdCommand;
import fun.randomAnimal.CatCommand;
import fun.randomAnimal.DogCommand;
import information.*;
import moderation.bestOfImage.BestOfImageCommand;
import moderation.bestOfImage.BestOfImageListener;
import moderation.bestOfQuote.BestOfQuoteCommand;
import moderation.bestOfQuote.BestOfQuoteListener;
import moderation.birthday.BirthdayCommand;
import moderation.birthday.BirthdayListener;
import moderation.leave.LeaveCommand;
import moderation.leave.LeaveListener;
import moderation.livestream.LivestreamCommand;
import moderation.livestream.LivestreamListener;
import moderation.toggle.ToggleCommand;
import moderation.user.UserCommand;
import owner.*;
import moderation.*;
import moderation.autorole.AutoroleCommand;
import moderation.autorole.AutoroleListener;
import moderation.guild.ServerCommand;
import moderation.guild.GuildManager;
import moderation.join.JoinCommand;
import moderation.lobby.VoiceLobbyCommand;
import moderation.lobby.VoiceLobbyListener;
import moderation.mediaOnlyChannel.MediaOnlyChannelCommand;
import moderation.mediaOnlyChannel.MediaOnlyChannelListener;
import moderation.reactionRole.ReactionRoleCommand;
import moderation.reactionRole.ReactionRoleListener;
import owner.blacklist.BlacklistCommand;
import patreon.PatreonListener;
import useful.alarm.AlarmCommand;
import useful.alarm.AlarmListener;
import useful.giveaway.GiveawayCommand;
import useful.reminder.ReminderCommand;
import useful.reminder.ReminderListener;
import useful.signup.SignupCommand;
import useful.signup.SignupListener;
import useful.votes.quickvote.QuickvoteCommand;
import useful.votes.quickvote.QuickvoteEndListener;
import useful.votes.quickvote.QuickvoteMultipleVoteListener;
import moderation.toggle.ToggleFile;
import moderation.join.JoinListener;
import fun.level.LevelListener;
import files.ConfigFile;
import interaction.*;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Game;
import useful.timezone.TimezoneCommand;
import useful.votes.vote.RadiovoteMultipleVoteListener;
import useful.votes.vote.VoteCommand;
import useful.votes.vote.VoteEndListener;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandClientBuilder;
import zJdaUtilsLib.com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import javax.security.auth.login.LoginException;
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

        LanguageHandler.initialize();

        toggle = new ToggleFile();

        var waiter = new EventWaiter(); // Has to be added to JDABuilder.
        var client = new CommandClientBuilder();

        client.setOwnerId(config.getBotOwnerId());
        client.setEmojis("✅", "⚠", "❌"); // ✅, ⚠, ❌.
        client.setPrefix(config.getDefaultPrefix());
        client.setServerInvite("discord.gg/4GpaH5V");
        client.setGuildSettingsManager(new GuildManager());
        client.addCommands(
                // Owner
                new AddGifCommand(),
                new BlacklistCommand(),
                new EvalCommand(),
                new RefreshCommand(),
                new ServerlistCommand(waiter),
                new ShutdownCommand(),

                // Moderation
                new AutoroleCommand(),
                new BirthdayCommand(),
                new BestOfImageCommand(),
                new BestOfQuoteCommand(),
                new ClearCommand(),
                new JoinCommand(),
                new LeaveCommand(),
                new LevelRoleCommand(),
                new LivestreamCommand(),
                new MediaOnlyChannelCommand(),
                new ReactionRoleCommand(),
                new RoleCommand(),
                new ServerCommand(),
                new ServerSetupCommand(waiter),
                new ToggleCommand(),
                new UserCommand(),
                new VoiceLobbyCommand(),

                // Information
                new BotInfoCommand(),
                new PatreonCommand(),
                new PingCommand(),
                new ServerInfoCommand(),

                // Useful
                new AlarmCommand(),
                new GiveawayCommand(),
                new QuickvoteCommand(),
                new ReminderCommand(),
                new SignupCommand(),
                new TimezoneCommand(),
                new VoteCommand(waiter),

                // Fun
                new AvatarCommand(),
                new BaguetteCommand(),
                new BirdCommand(),
                new BioCommand(),
                new CatCommand(),
                new CoinflipCommand(),
                new CreateEmbedCommand(waiter),
                new DogCommand(),
                new EditEmbedCommand(waiter),
                new FlipCommand(),
                new LoveCommand(),
                new ProfileCommand(),
                new ThanksCommand(),
                new UnflipCommand(),

                // Interaction
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

                .addEventListener(waiter)
                .addEventListener(client.build())
                .addEventListener(new AutoroleListener())
                .addEventListener(new BirthdayListener())
                .addEventListener(new InviteKickListener())
                .addEventListener(new JoinListener())
                .addEventListener(new LevelListener())
                .addEventListener(new VoiceLobbyListener())
                .addEventListener(new MediaOnlyChannelListener())
                .addEventListener(new PatreonListener())
                .addEventListener(new ReactionRoleListener())
                .addEventListener(new ReadyListener())
                .addEventListener(new LivestreamListener())
                .addEventListener(new QuickvoteEndListener())
                .addEventListener(new QuickvoteMultipleVoteListener())
                .addEventListener(new VoteEndListener())
                .addEventListener(new RadiovoteMultipleVoteListener())
                .addEventListener(new LeaveListener())
                .addEventListener(new EasterEggsListener())
                .addEventListener(new BestOfImageListener())
                .addEventListener(new BestOfQuoteListener())
                .addEventListener(new ReminderListener())
                .addEventListener(new AlarmListener())
                .addEventListener(new PrefixListener())
                .addEventListener(new SignupListener())

                .build();
    }
}
