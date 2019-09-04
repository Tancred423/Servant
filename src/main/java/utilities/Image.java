// Author: Tancred423 (https://github.com/Tancred423)
package utilities;

import servant.Database;

import java.sql.SQLException;

public class Image {
    public static String getImageUrl(String imageName) {
        try {
            var connection = Database.getConnection();
            var select = connection.prepareStatement("SELECT image_url FROM image WHERE image_name=?");
            select.setString(1, imageName);
            var resultSet = select.executeQuery();
            String imageUrl = null;
            if (resultSet.first()) imageUrl = resultSet.getString("image_url");
            connection.close();
            return imageUrl;
        } catch (SQLException e) {
            return null;
        }
    }
}
