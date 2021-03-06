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
import commands.fun.profile.NewProfileCommand;
import commands.fun.profile.ProfileCommand;
import commands.fun.tictactoe.TicTacToeCommand;
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
import commands.standard.bofInfo.BotInfoCommand;
import commands.standard.help.HelpCommand;
import commands.standard.ping.PingCommand;
import commands.standard.supporter.SupporterCommand;
import commands.utility.TimezoneCommand;
import commands.utility.birthday.SetBirthdayCommand;
import commands.utility.customCommands.CustomCommandsCommand;
import commands.utility.giveaway.GiveawayCommand;
import commands.utility.polls.poll.PollCommand;
import commands.utility.polls.quickpoll.QuickpollCommand;
import commands.utility.rating.RatingCommand;
import commands.utility.remindme.RemindMeCommand;
import commands.utility.signup.SignupCommand;
import files.ConfigFile;
import files.language.LanguageHandler;
import listeners.*;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.jodah.expiringmap.ExpiringMap;
import plugins.moderation.livestream.LivestreamHandler;
import servant.guild.GuildManager;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandClientBuilder;
import zJdaUtilsLib.com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
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
                new RatingCommand(),
                new RemindMeCommand(),
                new SetBirthdayCommand(),
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
                new NewProfileCommand(),
                new TicTacToeCommand(waiter),

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

        DefaultShardManagerBuilder.create(config.getBotToken(), EnumSet.allOf(GatewayIntent.class))
                .disableIntents(GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.DIRECT_MESSAGE_TYPING, GatewayIntent.DIRECT_MESSAGE_REACTIONS)
                .enableCache(CacheFlag.ACTIVITY, CacheFlag.EMOTE)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setActivity(Activity.playing("loading..."))
                .addEventListeners(
                        client.build(),
                        waiter
                )

                /* To not block the main thread, we will run CompletableFuture#runAsync
                 * with our own thread-pool.
                 * To reduce the amount of thrown events and created threads, we will
                 * handle all commands from the same event type in the same class and thread.
                 */
                .addEventListeners(
                        new CategoryCreateListener(), // Log
                        new CategoryDeleteListener(), // Log
                        new EmoteAddedListener(), // Log
                        new EmoteRemovedListener(), // Log
                        new GuildBanListener(), // Log
                        new GuildInviteCreateListener(), // Log
                        new GuildInviteDeleteListener(), // Log
                        new GuildJoinListener(), // Invite
                        new GuildLeaveListener(), // Purges
                        new GuildMemberJoinListener(), // AutoRole, Join, Log
                        new GuildMemberLeaveListener(), // Leave, Log
                        new GuildMemberRoleAddListener(), // Log, Patreon
                        new GuildMemberRoleRemoveListener(), // Log, Patreon
                        new GuildMessageDeleteListener(),  // Log, Purges
                        new GuildMessageReactionAddListener(), // BestOfImage, BestOfQuote, Giveaway, Quickpoll, Poll, Rating, Reaction Role, RemindMe, Signup
                        new GuildMessageReactionRemoveListener(), // Quickpoll, Poll, Rating, ReactionRole, Signup
                        new GuildMessageUpdateListener(), // Log
                        new GuildUnbanListener(), // Log
                        new GuildUpdateBoostCountListener(), // Log
                        new GuildUpdateBoostTierListener(), // Log
                        new GuildVoiceJoinListener(), // VoiceLobby, Log
                        new GuildVoiceLeaveListener(), // VoiceLobby, Log
                        new GuildVoiceMoveListener(), // Voicelobby, Log
                        new MessageReceivedListener(), // Prefix, MediaOnlyChannel, Cache, CustomCommands, Level, EasterEggs
                        new ReadyListener(), // Presence, Birthday, Giveaway, Poll, Quickpoll, Rating, RemindMe, Signup
                        new RoleCreateListener(), // Log
                        new RoleDeleteListener(), // Log
                        new TextChannelCreateListener(), // Log
                        new TextChannelDeleteListener(), // Log, Purges
                        new UserActivityEndListener(), // Livestream
                        new UserActivityStartListener(), // Livestream
                        new VoiceChannelCreateListener(), // Log
                        new VoiceChannelDeleteListener() // Log, Purges
                )
                .build();
    }
}
