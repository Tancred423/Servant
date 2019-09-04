// Author: Tancred423 (https://github.com/Tancred423)
package servant;

import files.language.LanguageHandler;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.ReconnectedEvent;
import net.dv8tion.jda.core.events.ResumedEvent;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
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

        setPresence(event);

        System.out.println(event.getJDA().getSelfUser().getName() + " ready.");
    }

    public void onResume(ResumedEvent event) {
        setPresence(event);
    }

    public void onReconnect(ReconnectedEvent event) {
        setPresence(event);
    }

    private void setPresence(ReadyEvent event) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> {
            try {
                settingPresence();
            } catch (IOException | SAXException | ParserConfigurationException e) {
                new Log(e, null, event.getJDA().getSelfUser(), "ReadyListener - Presence", null).sendLog(false);
            }
        }, 0, 60, TimeUnit.SECONDS);
    }

    private void setPresence(ResumedEvent event) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> {
            try {
                settingPresence();
            } catch (IOException | SAXException | ParserConfigurationException e) {
                new Log(e, null, event.getJDA().getSelfUser(), "ReadyListener - Presence", null).sendLog(false);
            }
        }, 0, 60, TimeUnit.SECONDS);
    }

    private void setPresence(ReconnectedEvent event) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> {
            try {
                settingPresence();
            } catch (IOException | SAXException | ParserConfigurationException e) {
                new Log(e, null, event.getJDA().getSelfUser(), "ReadyListener - Presence", null).sendLog(false);
            }
        }, 0, 60, TimeUnit.SECONDS);
    }

    private void settingPresence() throws IOException, SAXException, ParserConfigurationException {
        var lang = Servant.config.getDefaultLanguage();

        if (counter == 0)
            Servant.jda.getPresence().setPresence(OnlineStatus.ONLINE, Game.playing(String.format(LanguageHandler.get(lang, "presence_0"), getVersion(), Servant.config.getDefaultPrefix())));
        else if (counter == 1)
            Servant.jda.getPresence().setPresence(OnlineStatus.ONLINE, Game.watching(String.format(LanguageHandler.get(lang, "presence_1"), Servant.jda.getUsers().size(), Servant.config.getDefaultPrefix())));
        else if (counter == 2)
            Servant.jda.getPresence().setPresence(OnlineStatus.ONLINE, Game.watching(String.format(LanguageHandler.get(lang, "presence_2"), Servant.jda.getGuilds().size(), Servant.config.getDefaultPrefix())));
        else if (counter == 3)
            Servant.jda.getPresence().setPresence(OnlineStatus.ONLINE, Game.playing(String.format(LanguageHandler.get(lang, "presence_3"), Servant.config.getDefaultPrefix(), Servant.config.getDefaultPrefix())));
        else if (counter == 4)
            Servant.jda.getPresence().setPresence(OnlineStatus.ONLINE, Game.playing(LanguageHandler.get(lang, "presence_4")));

        counter++;
        if (counter == 5) counter = 0;
    }

    private synchronized String getVersion() throws ParserConfigurationException, IOException, SAXException {
        var builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        var src = new InputSource();
        src.setCharacterStream(new StringReader(getPomXml()));
        return builder.parse(src).getElementsByTagName("version").item(0).getTextContent();
    }

    private synchronized String getPomXml() throws IOException {
        var bufferedReader = new BufferedReader(new FileReader(new File("pom.xml")));
        var stringBuilder = new StringBuilder();
        var line = bufferedReader.readLine();
        while (line != null) {
            stringBuilder.append(line).append("\n");
            line = bufferedReader.readLine();
        }
        return stringBuilder.toString();
    }
}
