package interaction;

import servant.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class InteractionDatabase {
    public static String getGifUrl(String commandName) throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT gif FROM interaction WHERE command=?");
        select.setString(1, commandName);
        ResultSet resultSet = select.executeQuery();
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
