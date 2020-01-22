// Author: Tancred423 (https://github.com/Tancred423)
package servant;

import easteregg.ThanksCommand;
import files.ConfigFile;
import files.language.LanguageHandler;
import fun.AvatarCommand;
import fun.BaguetteCommand;
import fun.CoinflipCommand;
import fun.LoveCommand;
import fun.embed.CreateEmbedCommand;
import fun.embed.EditEmbedCommand;
import fun.flip.FlipCommand;
import fun.flip.UnflipCommand;
import fun.level.BioCommand;
import fun.level.LevelRoleCommand;
import fun.level.ProfileCommand;
import fun.random.RandomCommand;
import fun.random.randomAnimal.BirdCommand;
import fun.random.randomAnimal.CatCommand;
import fun.random.randomAnimal.DogCommand;
import information.*;
import interaction.*;
import listeners.*;
import moderation.ClearCommand;
import moderation.RoleCommand;
import moderation.ServerSetupCommand;
import moderation.AutoRoleCommand;
import moderation.BestOfImageCommand;
import moderation.BestOfQuoteCommand;
import moderation.birthday.BirthdayCommand;
import moderation.guild.GuildCommand;
import moderation.guild.GuildManager;
import moderation.JoinCommand;
import moderation.LeaveCommand;
import moderation.livestream.LivestreamCommand;
import moderation.MediaOnlyChannelCommand;
import moderation.ReactionRoleCommand;
import moderation.toggle.ToggleCommand;
import moderation.toggle.ToggleFile;
import moderation.user.UserCommand;
import moderation.voicelobby.VoiceLobbyCommand;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import owner.*;
import owner.blacklist.BlacklistCommand;
import useful.alarm.AlarmCommand;
import useful.giveaway.GiveawayCommand;
import useful.polls.PollCommand;
import useful.polls.QuickpollCommand;
import useful.reminder.ReminderCommand;
import useful.signup.SignupCommand;
import useful.timezone.TimezoneCommand;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandClientBuilder;
import zJdaUtilsLib.com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servant {
    public static ConfigFile config;
    public static ToggleFile toggle;
    public static Database db;

    public static ExecutorService threadPool;
    public static ExecutorService profilePool;

    public static void main(String[] args) throws IOException, LoginException {
        config = new ConfigFile();
        if (config.isMissing()) {
            System.out.println("The bot was shut down.");
            return;
        }

        db = new Database();
        if (!db.connectToDatabase()) return;

        LanguageHandler.initialize();

        toggle = new ToggleFile();

        var availProcessors = Runtime.getRuntime().availableProcessors();
        System.out.println("Available Processors: " + availProcessors);
        threadPool = Executors.newFixedThreadPool(availProcessors + 1);
        profilePool = Executors.newCachedThreadPool();

        var waiter = new EventWaiter();
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
                new AutoRoleCommand(),
                new BestOfImageCommand(),
                new BestOfQuoteCommand(),
                new BirthdayCommand(),
                new ClearCommand(),
                new JoinCommand(),
                new LeaveCommand(),
                new LevelRoleCommand(),
                new LivestreamCommand(),
                new MediaOnlyChannelCommand(),
                new ReactionRoleCommand(),
                new RoleCommand(),
                new GuildCommand(),
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
                new QuickpollCommand(),
                new ReminderCommand(),
                new SignupCommand(),
                new TimezoneCommand(),
                new PollCommand(waiter),

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
                new RandomCommand(),
                new UnflipCommand(),

                // Interaction
                new BegCommand(),
                new CookieCommand(),
                new CopCommand(),
                new DabCommand(),
                new FlexCommand(),
                new HighfiveCommand(),
                new HugCommand(),
                new KissCommand(),
                new LickCommand(),
                new PatCommand(),
                new SlapCommand(),

                // Easter Eggs
                new ThanksCommand()
        );

        new JDABuilder(AccountType.BOT)
                .setToken(config.getBotToken())

                // While loading.
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setActivity(Activity.playing("loading..."))

                .addEventListeners(client.build())
                .addEventListeners(waiter)

                /* To not block the main thread, we will run CompletableFuture#runAsync
                 * with our own thread-pool.
                 * To reduce the amount of thrown events and created threads, we will
                 * handle all commands from the same event type in the same class and thread.
                 */
                .addEventListeners(new GuildJoinListener()) // Invite
                .addEventListeners(new GuildLeaveListener()) // Birthday, Kick
                .addEventListeners(new GuildMemberJoinListener()) // AutoRole, Join
                .addEventListeners(new GuildMemberLeaveListener()) // Leave
                .addEventListeners(new GuildMemberRoleAddListener()) // Patreon
                .addEventListeners(new GuildMessageDeleteListener())  // Birthday
                .addEventListeners(new GuildMessageReactionAddListener()) // BestOfImage, BestOfQuote, Quickpoll, Poll, Radiopoll, Reaction Role, Signup
                .addEventListeners(new GuildMessageReactionRemoveListener()) // Quickpoll, Radiopoll, Reaction Role
                .addEventListeners(new GuildVoiceJoinListener()) // VoiceLobby
                .addEventListeners(new GuildVoiceLeaveListener()) // VoiceLobby
                .addEventListeners(new GuildVoiceMoveListener()) // Voicelobby
                .addEventListeners(new MessageReceivedListener()) // EasterEggs, Level, MediaOnlyChannel, Prefix
                .addEventListeners(new ReadyListener()) // Birthday
                .addEventListeners(new TextChannelDeleteListener()) // Giveaway
                .addEventListeners(new UserActivityEndListener()) // Livestream
                .addEventListeners(new UserActivityStartListener()) // Livestream

                .build();
    }
}
