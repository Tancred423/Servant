// Author: Tancred423 (https://github.com/Tancred423)
package servant;

import files.language.LanguageHandler;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import useful.giveaway.Giveaway;
import useful.signup.Signup;
import utilities.Constants;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static utilities.DatabaseConn.closeQuietly;

public class ReadyListener extends ListenerAdapter {
    private int counter = 0;

    public void onReady(ReadyEvent event) {
        Servant.jda = event.getJDA();

        Connection connection = null;
        try {
            connection = Servant.db.getHikari().getConnection();
        } catch (SQLException e) {
            System.out.println("Couldn't reach database.");
            return;
        } finally {
            closeQuietly(connection);
        }

        setPresence();
        checkGiveaways(event.getJDA());
        checkSignups(event.getJDA());

        System.out.println(event.getJDA().getSelfUser().getName() + " ready.");
    }

    private void checkSignups(JDA jda) {
        var service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> Signup.checkSignups(jda), 0, 1, TimeUnit.MINUTES);
    }

    private void checkGiveaways(JDA jda) {
        var service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> Giveaway.checkGiveaways(jda), 0, 1, TimeUnit.MINUTES);
    }

    private void setPresence() {
        var service = Executors.newSingleThreadScheduledExecutor();
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
