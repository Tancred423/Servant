// Author: Tancred423 (https://github.com/Tancred423)
package utilities;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import servant.Log;
import servant.Servant;

import java.sql.Connection;
import java.sql.SQLException;

import static utilities.DatabaseConn.closeQuietly;

public class Image {
    public static String getImageUrl(String imageName, Guild guild, User user) {
        Connection connection = null;
        String imageUrl = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT image_url FROM image WHERE image_name=?");
            select.setString(1, imageName);
            var resultSet = select.executeQuery();
            if (resultSet.first()) imageUrl = resultSet.getString("image_url");
        } catch (SQLException e) {
            new Log(e, guild, user, "Image.java", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return imageUrl;
    }
}
