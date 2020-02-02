// Author: Tancred423 (https://github.com/Tancred423)
package listeners;

import files.language.LanguageHandler;
import moderation.birthday.BirthdayHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import servant.Servant;
import useful.alarm.Alarm;
import useful.giveaway.GiveawayHandler;
import useful.polls.Poll;
import useful.reminder.Reminder;
import useful.signup.Signup;
import utilities.Constants;
import utilities.Time;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static utilities.DatabaseConn.closeQuietly;

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

        startExecutor(jda);

        System.out.println(jda.getSelfUser().getName() + " ready.");
    }

    private void startExecutor(JDA jda) {
        var executor1minute = Executors.newSingleThreadScheduledExecutor();
        var executor5minutes = Executors.newSingleThreadScheduledExecutor();
        var executor15minutes = Executors.newSingleThreadScheduledExecutor();
        var executor24hours = Executors.newSingleThreadScheduledExecutor();

        var delayToNextMinute = Time.getDelayToNextMinuteInMillis();
        var delayToNext5Minutes = Time.getDelayToNext5MinutesInMillis();
        var delayToNextQuarter = Time.getDelayToNextQuarterInMillis();
        var delayToNextDay = Time.getDelayToNextDayInMillis();

        // 1 Minute Period
        executor1minute.scheduleAtFixedRate(() -> {
            Alarm.check(jda);
            GiveawayHandler.checkGiveaways(jda);
            Reminder.check(jda);
            Signup.checkSignups(jda);
            Poll.check(jda);
        }, delayToNextMinute, 60 * 1000, TimeUnit.MILLISECONDS);

        // 5 Minute Period
        executor5minutes.scheduleAtFixedRate(() -> settingPresence(jda), delayToNext5Minutes, 5 * 60 * 1000, TimeUnit.MILLISECONDS);

        // 15 Minute Period
        executor15minutes.scheduleAtFixedRate(() -> {
            // Birthday
            BirthdayHandler.checkBirthdays(jda);
            BirthdayHandler.updateLists(jda);
        }, delayToNextQuarter, 15 * 60 * 1000, TimeUnit.MILLISECONDS);

        // 24 Hour Period
        executor24hours.scheduleAtFixedRate(() -> {
            try {
                logServerAmount(jda);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }, delayToNextDay, 24 * 60 * 60 * 1000, TimeUnit.MILLISECONDS);
    }

    private void settingPresence(JDA jda) {
        var lang = Servant.config.getDefaultLanguage();

        if (counter == 0)
            jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing(String.format(LanguageHandler.get(lang, "presence_0"), Constants.VERSION, Servant.config.getDefaultPrefix())));
        else if (counter == 1)
            jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.watching(String.format(LanguageHandler.get(lang, "presence_1"), jda.getUsers().size(), Servant.config.getDefaultPrefix())));
        else if (counter == 2)
            jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.watching(String.format(LanguageHandler.get(lang, "presence_2"), jda.getGuilds().size(), Servant.config.getDefaultPrefix())));
        else if (counter == 3)
            jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing(String.format(LanguageHandler.get(lang, "presence_3"), Servant.config.getDefaultPrefix(), Servant.config.getDefaultPrefix())));
        else if (counter == 4)
            jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing(LanguageHandler.get(lang, "presence_4")));

        counter++;
        if (counter == 5) counter = 0;
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
