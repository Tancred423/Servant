// Author: Tancred423 (https://github.com/Tancred423)
package useful.alarm;

import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import servant.Log;
import utilities.Time;

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AlarmListener extends ListenerAdapter {
    public void onReady(ReadyEvent event) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> {
            try {
                Alarm.check(event.getJDA());
            } catch (SQLException e) {
                new Log(e, null, event.getJDA().getSelfUser(), "alarm", null).sendLog(false);
            }
        }, Time.getDelayToNextMinuteInMillis(), 60 * 1000, TimeUnit.MILLISECONDS); // 1 minute period
    }
}
