package servant;

import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ReadyListener extends ListenerAdapter {
    public void onReady(ReadyEvent event) {
        Servant.jda = event.getJDA();
        System.out.println("Servant ready.");
    }
}
