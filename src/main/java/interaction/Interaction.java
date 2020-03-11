// Author: Tancred423 (https://github.com/Tancred423)
package interaction;

import net.dv8tion.jda.api.JDA;
import servant.LoggingTask;
import servant.Servant;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static servant.Database.closeQuietly;

public class Interaction {
    private String name;
    private int shared;
    private int received;

    public Interaction(String name, int shared, int received) {
        this.name = name;
        this.shared = shared;
        this.received = received;
    }

    public void setName(String name) { this.name = name; }
    public void setShared(int shared) { this.shared = shared; }
    public void setReceived(int received) { this.received = received; }

    public String getName() { return name; }
    public int getShared() { return shared; }
    public int getReceived() { return received; }

    public static String getGifUrl(JDA jda, String commandName) {
        Connection connection = null;
        String gif = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT gif FROM interaction WHERE interaction=?");
            select.setString(1, commandName);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                List<String> gifs = new ArrayList<>();
                do gifs.add(resultSet.getString("gif")); while (resultSet.next());
                int random = ThreadLocalRandom.current().nextInt(0, gifs.size());
                gif = gifs.get(random);
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Interaction#getGifUrl"));
        } finally {
            closeQuietly(connection);
        }

        return gif;
    }
}
