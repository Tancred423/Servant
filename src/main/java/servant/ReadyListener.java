package servant;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.ReconnectedEvent;
import net.dv8tion.jda.core.events.ResumedEvent;
import zChatLib.Bot;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.File;
import java.sql.SQLException;

public class ReadyListener extends ListenerAdapter {
    public void onReady(ReadyEvent event) {
        Servant.jda = event.getJDA();
        Servant.chatBot = new Bot("super", getResourcesPath());

        try {
            Database.getConnection();
        } catch (SQLException e) {
            System.out.println("Couldn't reach database.");
            return;
        }

        setPresence(Servant.jda);

        System.out.println(event.getJDA().getSelfUser().getName() + " ready.");
    }

    public void onResume(ResumedEvent event) {
        setPresence(Servant.jda);
    }

    public void onReconnect(ReconnectedEvent event) {
        setPresence(Servant.jda);
    }

    private String getResourcesPath() {
        return System.getProperty("user.dir") + File.separator + "resources";
    }

    private void setPresence(JDA jda) {
        jda.getPresence().setPresence(OnlineStatus.ONLINE, Game.playing(Servant.config.getDefaultPrefix() + "help | v" + Servant.config.getBotVersion()));
    }
}
