// Author: Tancred423 (https://github.com/Tancred423)
package interaction;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import servant.Log;
import servant.Servant;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static servant.Database.closeQuietly;

class InteractionDatabase {
    static String getGifUrl(String commandName, Guild guild, User user) {
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
            new Log(e, guild, user, "levelrole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return gif;
    }
}
