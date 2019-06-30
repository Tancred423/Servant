package servant;

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

        System.out.println(event.getJDA().getSelfUser().getName() + " ready.");
    }

    private static String getResourcesPath() {
        return System.getProperty("user.dir") + File.separator + "resources";
    }
}
