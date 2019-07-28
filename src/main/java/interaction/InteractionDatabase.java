package interaction;

import servant.Database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

class InteractionDatabase {
    static String getGifUrl(String commandName) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT gif FROM interaction WHERE interaction=?");
        select.setString(1, commandName);
        var resultSet = select.executeQuery();
        String gif = null;
        if (resultSet.first()) {
            List<String> gifs = new ArrayList<>();
            do gifs.add(resultSet.getString("gif")); while (resultSet.next());
            int random = ThreadLocalRandom.current().nextInt(0, gifs.size());
            gif = gifs.get(random);
        }
        connection.close();
        return gif;
    }
}
