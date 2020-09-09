// Author: Tancred423 (https://github.com/Tancred423)
package servant;

import commands.dashboard.DashboardCommand;
import commands.dashboard.DiscontinuedCommand;
import commands.dashboard.LeaderboardCommand;
import commands.fun.avatar.AvatarCommand;
import commands.fun.baguette.BaguetteCommand;
import commands.fun.bubbleWrap.BubbleWrapCommand;
import commands.fun.coinFlip.CoinFlipCommand;
import commands.fun.flip.FlipCommand;
import commands.fun.love.LoveCommand;
import commands.fun.mirror.MirrorCommand;
import commands.fun.profile.AchievementsCommand;
import commands.fun.profile.CommandsCommand;
import commands.fun.profile.ProfileCommand;
import commands.interaction.*;
import commands.moderation.clear.ClearCommand;
import commands.moderation.editembed.EditEmbedCommand;
import commands.owner.EvalCommand;
import commands.owner.RefreshCommand;
import commands.owner.ServerlistCommand;
import commands.owner.ShutdownCommand;
import commands.owner.blacklist.BlacklistCommand;
import commands.owner.statsForNerds.ThreadCommand;
import commands.random.*;
import commands.random.randomImgur.RandomCommand;
import commands.standard.bofInfo.BotInfoCommand;
import commands.standard.help.HelpCommand;
import commands.standard.ping.PingCommand;
import commands.standard.supporter.SupporterCommand;
import commands.utility.TimezoneCommand;
import commands.utility.customCommands.CustomCommandsCommand;
import commands.utility.giveaway.GiveawayCommand;
import commands.utility.polls.poll.PollCommand;
import commands.utility.polls.quickpoll.QuickpollCommand;
import commands.utility.rate.RateCommand;
import commands.utility.remindme.RemindMeCommand;
import commands.utility.signup.SignupCommand;
import files.ConfigFile;
import files.language.LanguageHandler;
import listeners.*;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.jodah.expiringmap.ExpiringMap;
import plugins.moderation.livestream.LivestreamHandler;
import servant.guild.GuildManager;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandClientBuilder;
import zJdaUtilsLib.com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Servant {
    public static ConfigFile config;
    public static Database db;

    public static ExecutorService fixedThreadPool;
    public static ExecutorService cachedThreadPool;
    public static ScheduledExecutorService scheduledService;
    public static ScheduledExecutorService periodService;

    public static ArrayList<Command> commands;

    public static ExpiringMap<Long, MyMessage> myMessageCache;
    public static ExpiringMap<Long, String> myDeletedMessageCache;

    public static void main(String[] args) throws IOException, LoginException {
        // Config File
        config = new ConfigFile();
        if (config.isMissing()) {
            System.out.println("The bot was shut down.");
            return;
        }

        // Language File
        LanguageHandler.initialize();

        // Database
        db = new Database();
        if (!db.connectToDatabase()) return;

        // Thread Pools
        var availProcessors = Runtime.getRuntime().availableProcessors();
        System.out.println("Available Processors: " + availProcessors);

        fixedThreadPool = Executors.newFixedThreadPool(availProcessors + 1);
        cachedThreadPool = Executors.newCachedThreadPool();
        scheduledService = Executors.newScheduledThreadPool(1);
        periodService = Executors.newScheduledThreadPool(1);

        // Message Cache
        myMessageCache = ExpiringMap.builder()
                .maxSize(1000)
                .expiration(1, TimeUnit.HOURS)
                .build();

        myDeletedMessageCache = ExpiringMap.builder()
                .maxSize(1000)
                .expiration(30, TimeUnit.SECONDS)
                .build();

        // Livestream
        LivestreamHandler.activeStreamers = new ArrayList<>();

        // JDA Stuff
        var waiter = new EventWaiter();
        var client = new CommandClientBuilder();

        client.setOwnerId(config.getBotOwnerId());
        client.setEmojis("✅", "⚠", "❌"); // ✅, ⚠, ❌.
        client.setPrefix(config.getDefaultPrefix());
        client.setServerInvite("discord.gg/4GpaH5V");
        client.setGuildSettingsManager(new GuildManager());
        client.addCommands(
                // Discontinued
                new DashboardCommand(),
                new DiscontinuedCommand(),
                new LeaderboardCommand(),

                // Owner
                new BlacklistCommand(),
                new EvalCommand(),
                new RefreshCommand(),
                new ServerlistCommand(waiter),
                new ShutdownCommand(),
                new ThreadCommand(),

                // Standard
                new BotInfoCommand(),
                new HelpCommand(),
                new PingCommand(),
                new SupporterCommand(),

                // Moderation
                new ClearCommand(),
                new EditEmbedCommand(waiter),

                // Utility
                new CustomCommandsCommand(),
                new GiveawayCommand(),
                new PollCommand(waiter),
                new QuickpollCommand(),
                new RateCommand(),
                new RemindMeCommand(),
                new SignupCommand(),
                new TimezoneCommand(),

                // Fun
                new AchievementsCommand(),
                new AvatarCommand(),
                new BaguetteCommand(),
                new BubbleWrapCommand(),
                new CoinFlipCommand(),
                new CommandsCommand(),
                new FlipCommand(),
                new LoveCommand(),
                new MirrorCommand(),
                new ProfileCommand(),

                // Interaction
                new BegCommand(),
                new BirthdayCommand(),
                new BiteCommand(),
                new BullyCommand(),
                new CheersCommand(),
                new CookieCommand(),
                new CopCommand(),
                new DabCommand(),
                new FCommand(),
                new FlexCommand(),
                new HighfiveCommand(),
                new HugCommand(),
                new KissCommand(),
                new LickCommand(),
                new PatCommand(),
                new PokeCommand(),
                new SlapCommand(),
                new ShameCommand(),
                new WaveCommand(),
                new WinkCommand(),

                // Random
                new RandomCommand(),
                new BirdCommand(),
                new CatCommand(),
                new DogCommand(),
                new FennecCommand(),
                new FoxCommand(),
                new FrogCommand(),
                new KoalaCommand(),
                new PandaCommand(),
                new PikachuCommand(),
                new RedPandaCommand(),
                new SlothCommand(),
                new WolfCommand()
        );

        new DefaultShardManagerBuilder()
                .setToken(config.getBotToken())
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setActivity(Activity.playing("loading..."))
                .addEventListeners(client.build())
                .addEventListeners(waiter)

                /* To not block the main thread, we will run CompletableFuture#runAsync
                 * with our own thread-pool.
                 * To reduce the amount of thrown events and created threads, we will
                 * handle all commands from the same event type in the same class and thread.
                 */
                .addEventListeners(new CategoryCreateListener()) // Log
                .addEventListeners(new CategoryDeleteListener()) // Log
                .addEventListeners(new EmoteAddedListener()) // Log
                .addEventListeners(new EmoteRemovedListener()) // Log
                .addEventListeners(new GuildBanListener()) // Log
                .addEventListeners(new GuildInviteCreateListener()) // Log
                .addEventListeners(new GuildInviteDeleteListener()) // Log
                .addEventListeners(new GuildJoinListener()) // Invite
                .addEventListeners(new GuildLeaveListener()) // Purges
                .addEventListeners(new GuildMemberJoinListener()) // AutoRole, Join, Log
                .addEventListeners(new GuildMemberLeaveListener()) // Leave, Log
                .addEventListeners(new GuildMemberRoleAddListener()) // Log, Patreon
                .addEventListeners(new GuildMemberRoleRemoveListener()) // Log, Patreon
                .addEventListeners(new GuildMessageDeleteListener())  // Log, Purges
                .addEventListeners(new GuildMessageReactionAddListener()) // BestOfImage, BestOfQuote, Giveaway, Quickpoll, Poll, Rating, Reaction Role, RemindMe, Signup
                .addEventListeners(new GuildMessageReactionRemoveListener()) // Quickpoll, Poll, Rating, ReactionRole, Signup
                .addEventListeners(new GuildMessageUpdateListener()) // Log
                .addEventListeners(new GuildUnbanListener()) // Log
                .addEventListeners(new GuildUpdateBoostCountListener()) // Log
                .addEventListeners(new GuildUpdateBoostTierListener()) // Log
                .addEventListeners(new GuildVoiceJoinListener()) // VoiceLobby, Log
                .addEventListeners(new GuildVoiceLeaveListener()) // VoiceLobby, Log
                .addEventListeners(new GuildVoiceMoveListener()) // Voicelobby, Log
                .addEventListeners(new MessageReceivedListener()) // Prefix, MediaOnlyChannel, Cache, CustomCommands, Level, EasterEggs
                .addEventListeners(new ReadyListener()) // Presence, Birthday, Giveaway, Poll, Quickpoll, Rating, RemindMe, Signup
                .addEventListeners(new RoleCreateListener()) // Log
                .addEventListeners(new RoleDeleteListener()) // Log
                .addEventListeners(new TextChannelCreateListener()) // Log
                .addEventListeners(new TextChannelDeleteListener()) // Log, Purges
                .addEventListeners(new UserActivityEndListener()) // Livestream
                .addEventListeners(new UserActivityStartListener()) // Livestream
                .addEventListeners(new VoiceChannelCreateListener()) // Log
                .addEventListeners(new VoiceChannelDeleteListener()) // Log, Purges
        .build();
    }
}
