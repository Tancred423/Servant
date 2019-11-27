// Author: Tancred423 (https://github.com/Tancred423)
package servant;

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
import useful.alarm.Alarm;
import useful.giveaway.Giveaway;
import useful.reminder.Reminder;
import useful.signup.Signup;
import utilities.Constants;
import utilities.Time;

import java.io.*;
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

        setPresence(jda);
        checkStuff(jda);
        startServerAmountLogging(jda);

        System.out.println(jda.getSelfUser().getName() + " ready.");
    }

    private void checkStuff(JDA jda) {
        var service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> {
            System.out.println("[" + OffsetDateTime.now(ZoneId.of(Constants.LOG_OFFSET)).toString().replaceAll("T", " ").substring(0, 19) + "] " + "Checking alarms, giveaways, reminders and signups.");
            Alarm.check(jda);
            Giveaway.checkGiveaways(jda);
            Reminder.check(jda);
            Signup.checkSignups(jda);
        }, Time.getDelayToNextMinuteInMillis(), 60 * 1000, TimeUnit.MILLISECONDS); // 1 minute
    }

    private void setPresence(JDA jda) {
        var service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> settingPresence(jda), 0, 5, TimeUnit.MINUTES);
    }

    private void settingPresence(JDA jda) {
        System.out.println("[" + OffsetDateTime.now(ZoneId.of(Constants.LOG_OFFSET)).toString().replaceAll("T", " ").substring(0, 19) + "] " + "Changing Presence.");
        var lang = Servant.config.getDefaultLanguage();

        if (counter == 0)
            jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing(String.format(LanguageHandler.get(lang, "presence_0"), Constants.VERSION, Servant.config.getDefaultPrefix())));
        else if (counter == 1)
            jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.watching(String.format(LanguageHandler.get(lang, "presence_1"), jda.getUsers().size(), Servant.config.getDefaultPrefix())));
        else if (counter == 2)
            jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.watching(String.format(LanguageHandler.get(lang, "presence_2"), jda.getGuilds().size(), Servant.config.getDefaultPrefix())));
        else if (counter == 3)
            jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing(String.format(LanguageHandler.get(lang, "presence_3"), Servant.config.getDefaultPrefix(), Servant.config.getDefaultPrefix())));

        counter++;
        if (counter == 4) counter = 0;
    }

    private void startServerAmountLogging(JDA jda) {
        var service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> {
            try {
                logServerAmount(jda);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }, 0, 24, TimeUnit.HOURS);
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "unchecked"})
    private void logServerAmount(JDA jda) throws IOException, ParseException {
        System.out.println("[" + OffsetDateTime.now(ZoneId.of(Constants.LOG_OFFSET)).toString().replaceAll("T", " ").substring(0, 19) + "] " + "Logging Server Amount.");
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
