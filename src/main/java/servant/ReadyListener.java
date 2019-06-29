package servant;

import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.sql.SQLException;

public class ReadyListener extends ListenerAdapter {
    public void onReady(ReadyEvent event) {
        Servant.jda = event.getJDA();
        try {
            Database.getConnection();
        } catch (SQLException e) {
            System.out.println("Couldn't reach database.");
            return;
        }
        System.out.println("Servant ready.");
    }
}
