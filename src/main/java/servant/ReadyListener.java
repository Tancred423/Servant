// Author: Tancred423 (https://github.com/Tancred423)
package servant;

import files.language.LanguageHandler;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import utilities.Constants;

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReadyListener extends ListenerAdapter {
    private int counter = 0;

    public void onReady(ReadyEvent event) {
        Servant.jda = event.getJDA();

        try {
            Database.getConnection();
        } catch (SQLException e) {
            System.out.println("Couldn't reach database.");
            return;
        }

        setPresence();

        System.out.println(event.getJDA().getSelfUser().getName() + " ready.");
    }

    private void setPresence() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(this::settingPresence, 0, 5, TimeUnit.MINUTES);
    }

    private void settingPresence() {
        var lang = Servant.config.getDefaultLanguage();

        if (counter == 0)
            Servant.jda.getPresence().setPresence(OnlineStatus.ONLINE, Game.playing(String.format(LanguageHandler.get(lang, "presence_0"), Constants.VERSION, Servant.config.getDefaultPrefix())));
        else if (counter == 1)
            Servant.jda.getPresence().setPresence(OnlineStatus.ONLINE, Game.watching(String.format(LanguageHandler.get(lang, "presence_1"), Servant.jda.getUsers().size(), Servant.config.getDefaultPrefix())));
        else if (counter == 2)
            Servant.jda.getPresence().setPresence(OnlineStatus.ONLINE, Game.watching(String.format(LanguageHandler.get(lang, "presence_2"), Servant.jda.getGuilds().size(), Servant.config.getDefaultPrefix())));
        else if (counter == 3)
            Servant.jda.getPresence().setPresence(OnlineStatus.ONLINE, Game.playing(String.format(LanguageHandler.get(lang, "presence_3"), Servant.config.getDefaultPrefix(), Servant.config.getDefaultPrefix())));

        counter++;
        if (counter == 4) counter = 0;
    }
}
