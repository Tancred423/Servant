// Author: Tancred423 (https://github.com/Tancred423)
package servant;

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
import fun.level.*;
import information.BotInfoCommand;
import information.PatreonCommand;
import information.PingCommand;
import information.ServerInfoCommand;
import interaction.*;
import listeners.*;
import moderation.*;
import moderation.bestOfQuote.BestOfQuoteCommand;
import moderation.birthday.BirthdayCommand;
import moderation.guild.GuildCommand;
import moderation.guild.GuildManager;
import moderation.joinleave.JoinCommand;
import moderation.joinleave.JoinMessageCommand;
import moderation.joinleave.LeaveCommand;
import moderation.joinleave.LeaveMessageCommand;
import moderation.livestream.LivestreamCommand;
import moderation.log.LogCommand;
import moderation.reactionRole.ReactionRoleCommand;
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
import owner.statsForNerds.ThreadCommand;
import random.*;
import random.randomImgur.RandomCommand;
import useful.giveaway.GiveawayCommand;
import useful.polls.PollCommand;
import useful.polls.QuickpollCommand;
import useful.remindme.RemindMeCommand;
import useful.signup.SignupCommand;
import useful.TimezoneCommand;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandClientBuilder;
import zJdaUtilsLib.com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Servant {
    public static ConfigFile config;
    public static ToggleFile toggle;
    public static Database db;

    public static ExecutorService fixedThreadPool;
    public static ExecutorService cachedThreadPool;
    public static ScheduledExecutorService remindMeService;
    public static ScheduledExecutorService periodService;

    public static void main(String[] args) throws IOException, LoginException {
        // Config File
        config = new ConfigFile();
        if (config.isMissing()) {
            System.out.println("The bot was shut down.");
            return;
        }

        // Language File
        LanguageHandler.initialize();

        // Toggle File
        toggle = new ToggleFile();

        // Database
        db = new Database();
        if (!db.connectToDatabase()) return;

        var availProcessors = Runtime.getRuntime().availableProcessors();
        System.out.println("Available Processors: " + availProcessors);

        fixedThreadPool = Executors.newFixedThreadPool(availProcessors + 1);
        cachedThreadPool = Executors.newCachedThreadPool();
        remindMeService = Executors.newScheduledThreadPool(1);
        periodService = Executors.newScheduledThreadPool(1);

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
                new ThreadCommand(),

                // Moderation
                new AutoRoleCommand(),
                new BestOfImageCommand(),
                new BestOfQuoteCommand(),
                new BirthdayCommand(),
                new ClearCommand(),
                new JoinCommand(),
                new JoinMessageCommand(),
                new LeaveCommand(),
                new LeaveMessageCommand(),
                new LevelRoleCommand(),
                new LivestreamCommand(),
                new LogCommand(),
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
                new GiveawayCommand(),
                new QuickpollCommand(),
                new RemindMeCommand(),
                new SignupCommand(),
                new TimezoneCommand(),
                new PollCommand(waiter),

                // Fun
                new AchievementsCommand(),
                new AvatarCommand(),
                new BaguetteCommand(),
                new BioCommand(),
                new CoinflipCommand(),
                new CreateEmbedCommand(waiter),
                new EditEmbedCommand(waiter),
                new FlipCommand(),
                new LeaderboardCommand(),
                new LoveCommand(),
                new MostUsedCommandsCommand(),
                new ProfileCommand(),
                new UnflipCommand(),

                // Interaction
                new BegCommand(),
                new BiteCommand(),
                new BullyCommand(),
                new CookieCommand(),
                new CopCommand(),
                new DabCommand(),
                new FlexCommand(),
                new HappyBirthdayCommand(),
                new HighfiveCommand(),
                new HugCommand(),
                new KissCommand(),
                new LickCommand(),
                new PatCommand(),
                new PokeCommand(),
                new SlapCommand(),
                new WaveCommand(),
                new WinkCommand(),

                // Random
                new RandomCommand(),
                new BirdCommand(),
                new CatCommand(),
                new DogCommand(),
                new FoxCommand(),
                new KoalaCommand(),
                new MemeCommand(),
                new PandaCommand(),
                new PikachuCommand(),
                new RedPandaCommand(),
                new SlothCommand()
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
                .addEventListeners(new GuildMemberRoleAddListener()) // Log, Patreon
                .addEventListeners(new GuildMemberRoleRemoveListener()) // Log
                .addEventListeners(new GuildMessageDeleteListener())  // Birthday
                .addEventListeners(new GuildMessageReactionAddListener()) // BestOfImage, BestOfQuote, Quickpoll, Poll, Radiopoll, Reaction Role, Signup
                .addEventListeners(new GuildMessageReactionRemoveListener()) // Quickpoll, Radiopoll, Reaction Role
                .addEventListeners(new GuildUpdateBoostCountListener()) // Log
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
