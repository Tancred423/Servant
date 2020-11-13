// Author: Tancred423 (https://github.com/Tancred423)
package listeners;

import commands.utility.giveaway.Giveaway;
import commands.utility.giveaway.GiveawayEndTask;
import commands.utility.polls.Poll;
import commands.utility.polls.poll.PollEndTask;
import commands.utility.polls.quickpoll.QuickpollEndTask;
import commands.utility.rating.Rating;
import commands.utility.rating.RatingEndTask;
import commands.utility.remindme.RemindMe;
import commands.utility.remindme.RemindMeSenderTask;
import commands.utility.signup.Signup;
import commands.utility.signup.SignupSenderTask;
import files.language.LanguageHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import plugins.moderation.birthday.BirthdayHandler;
import servant.Servant;
import utilities.Console;
import utilities.Constants;
import utilities.TimeUtil;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static servant.Database.closeQuietly;

public class ReadyListener extends ListenerAdapter {
    private int counter = 0;

    public void onReady(@NotNull ReadyEvent event) {
        // Check database connection
        Connection connection = null;
        try {
            connection = Servant.db.getHikari().getConnection();
        } catch (SQLException e) {
            System.out.println("Couldn't reach database.");
            return;
        } finally {
            closeQuietly(connection);
        }

        var jda = event.getJDA();
        var lang = Servant.config.getDefaultLanguage();

        startExecutor(jda, lang);

        startGiveaway(jda);
        startPoll(jda);
        startQuickpoll(jda);
        startRating(jda);
        startRemindMe(jda);
        startSignup(jda);

        Console.log(jda.getSelfUser().getName() + " ready on " + jda.getShardInfo());
        Console.log("Unavailable guilds: " + event.getGuildUnavailableCount());
    }

    private void startRating(JDA jda) {
        var ratingList = Rating.getList(jda);
        for (var rating : ratingList) {
            var delayInMillis = rating.getEventTime().toEpochMilli() - Instant.now().toEpochMilli();
            Servant.scheduledService.schedule(new RatingEndTask(rating), delayInMillis, TimeUnit.MILLISECONDS);
        }
    }

    private void startGiveaway(JDA jda) {
        var giveawayList = Giveaway.getList(jda);
        for (var giveaway : giveawayList) {
            var delayInMillis = giveaway.getEventTime().toEpochMilli() - Instant.now().toEpochMilli();
            Servant.scheduledService.schedule(new GiveawayEndTask(giveaway), delayInMillis, TimeUnit.MILLISECONDS);
        }
    }

    private void startPoll(JDA jda) {
        var checkpollList = Poll.getList(jda, "check");
        var radiopollList = Poll.getList(jda, "radio");
        var pollList = Stream.concat(checkpollList.stream(), radiopollList.stream()).collect(Collectors.toList());
        for (var poll : pollList) {
            var delayInMillis = poll.getEventTime().toEpochMilli() - Instant.now().toEpochMilli();
            Servant.scheduledService.schedule(new PollEndTask(poll), delayInMillis, TimeUnit.MILLISECONDS);
        }
    }

    private void startQuickpoll(JDA jda) {
        var quickpollList = Poll.getList(jda, "quick");
        for (var quickpoll : quickpollList) {
            var delayInMillis = quickpoll.getEventTime().toEpochMilli() - Instant.now().toEpochMilli();
            Servant.scheduledService.schedule(new QuickpollEndTask(quickpoll), delayInMillis, TimeUnit.MILLISECONDS);
        }
    }

    private void startRemindMe(JDA jda) {
        var remindMeList = RemindMe.getList(jda);
        for (var remindMe : remindMeList) {
            var delayInMillis = remindMe.getEventTime().toInstant().toEpochMilli() - Instant.now().toEpochMilli();
            Servant.scheduledService.schedule(new RemindMeSenderTask(remindMe), delayInMillis, TimeUnit.MILLISECONDS);
        }
    }

    private void startSignup(JDA jda) {
        var signupList = Signup.getList(jda);
        for (var signup : signupList) {
            var delayInMillis = signup.getEventTime().toInstant().toEpochMilli() - Instant.now().toEpochMilli();
            Servant.scheduledService.schedule(new SignupSenderTask(signup), delayInMillis, TimeUnit.MILLISECONDS);
        }
    }

    private void startExecutor(JDA jda, String lang) {
        var delayToNext5Minutes = TimeUtil.getDelayToNext5MinutesInMillis();
        var delayToNextQuarter = TimeUtil.getDelayToNextQuarterInMillis();
        var delayToNextDay = TimeUtil.getDelayToNextDayInMillis();

        // 5 Minute Period
        Servant.periodService.scheduleAtFixedRate(() -> settingPresence(jda, lang), delayToNext5Minutes, 5 * 60 * 1000, TimeUnit.MILLISECONDS);

        // 15 Minute Period
        Servant.periodService.scheduleAtFixedRate(() -> {
            // Birthday
            var sm = jda.getShardManager();
            if (sm != null) {
                BirthdayHandler.checkBirthdays(sm);
                BirthdayHandler.updateLists(sm, jda);
            }
        }, delayToNextQuarter, 15 * 60 * 1000, TimeUnit.MILLISECONDS);

        // 24 Hour Period
        Servant.periodService.scheduleAtFixedRate(() -> {
            try {
                logServerAmount(jda);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }, delayToNextDay, 24 * 60 * 60 * 1000, TimeUnit.MILLISECONDS);
    }

    private void settingPresence(JDA jda, String lang) {
        if (counter == 0)
            jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing(String.format(LanguageHandler.get(lang, "presence_0"), Constants.VERSION, Servant.config.getDefaultPrefix())));
        else if (counter == 1)
            jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.watching(String.format(LanguageHandler.get(lang, "presence_1"), jda.getUsers().size(), Servant.config.getDefaultPrefix())));
        else if (counter == 2)
            jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.watching(String.format(LanguageHandler.get(lang, "presence_2"), jda.getGuilds().size(), Servant.config.getDefaultPrefix())));
        else if (counter == 3)
            jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing(String.format(LanguageHandler.get(lang, "presence_3"), Servant.config.getDefaultPrefix(), Servant.config.getDefaultPrefix())));
        else if (counter == 4)
            jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing(String.format(LanguageHandler.get(lang, "presence_4"), Constants.WEBSITE, Servant.config.getDefaultPrefix())));
        else if (counter == 5)
            jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing(String.format(LanguageHandler.get(lang, "presence_5"), Servant.config.getDefaultPrefix())));
        else if (counter == 6)
            jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing(String.format(LanguageHandler.get(lang, "presence_6"), Servant.config.getDefaultPrefix())));

        counter++;
        if (counter == 7) counter = 0;
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "unchecked"})
    private void logServerAmount(JDA jda) throws IOException, ParseException {
        var currentDir = System.getProperty("user.dir");
        var logDir = currentDir + "/server_log";
        var jsonDir = logDir + "/server_log.json";

        // Create ./server_log if it does not exist already.
        var resources = new File(logDir);
        if (!resources.exists()) resources.mkdir();

        // Create ./server_log/server_log.json if it does not exist already.
        var jsonLog = new File(jsonDir);
        JSONObject json;
        if (jsonLog.exists() && !jsonLog.isDirectory()) {
            var obj = new JSONParser().parse(new FileReader(jsonDir));
            json = (JSONObject) obj;
        } else {
            json = new JSONObject();
        }

        json.put(OffsetDateTime.now(ZoneId.of(Constants.LOG_OFFSET)).toString().replaceAll("T", " ").substring(0, 19), jda.getGuilds().size());
        var file = new FileWriter(jsonDir);
        file.write(String.valueOf(json));
        file.close();
    }
}
