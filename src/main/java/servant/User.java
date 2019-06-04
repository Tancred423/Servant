package servant;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    private long userId;
    private Color color;

    public User(long userId) throws SQLException {
        this.userId = userId;
        thisColor();
    }

    public Color getColor() { return color; }

    private void thisColor() throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT value FROM user_settings WHERE user_id=? AND setting=?");
        select.setLong(1, userId);
        select.setString(2, "color");
        ResultSet resultSet = select.executeQuery();
        String colorCode = null;
        if (resultSet.first()) colorCode = resultSet.getString("value");
        if (colorCode == null) color = Color.decode(Servant.config.getDefaultColorCode());
        else color = Color.decode(colorCode);
        connection.close();
    }

    public void setColor(String colorCode) throws SQLException {
        this.color = Color.decode(colorCode);

        Connection connection = Database.getConnection();
        if (hasEntry("user_settings", "setting", "color")) {
            //  Update.
            PreparedStatement update = connection.prepareStatement("UPDATE user_settings SET value=? WHERE user_id=? AND setting=?");
            update.setString(1, colorCode);
            update.setLong(2, userId);
            update.setString(3, "color");
            update.executeUpdate();
        } else {
            // Insert.
            PreparedStatement insert = connection.prepareStatement("INSERT INTO user_settings (user_id,setting,value) VALUES (?,?,?)");
            insert.setLong(1, userId);
            insert.setString(2, "color");
            insert.setString(3, colorCode);
            insert.executeUpdate();
        }
        connection.close();
    }

    public boolean unsetColor() throws SQLException {
        this.color = Color.decode(Servant.config.getDefaultColorCode());

        Connection connection = Database.getConnection();
        if (hasEntry("user_settings", "setting", "color")) {
            //  Delete.
            PreparedStatement delete = connection.prepareStatement("DELETE FROM user_settings WHERE user_id=? AND setting=?");
            delete.setLong(1, userId);
            delete.setString(2, "color");
            delete.executeUpdate();
            connection.close();
            return true;
        } else {
            // Nothing to delete.
            connection.close();
            return false;
        }
    }

    public int getInteractionCount(String interaction, boolean isShared) throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT " + (isShared ? "shared" : "received") + " FROM interaction_count WHERE user_id=? AND interaction=?");
        select.setLong(1, userId);
        select.setString(2, interaction.toLowerCase());
        ResultSet resultSet = select.executeQuery();
        int commandCount = 0;
        if (resultSet.first()) commandCount = resultSet.getInt(isShared ? "shared" : "received");
        connection.close();
        return commandCount;
    }

    private boolean hasEntry(String tableName, String column, String key) throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT * FROM "  + tableName + " WHERE user_id=? AND " + column + "=?");
        select.setLong(1, userId);
        select.setString(2, key);
        ResultSet resultSet = select.executeQuery();
        return resultSet.first();
    }

    public void incrementInteractionCount(String interaction, boolean isShared) throws SQLException {
        int count = getInteractionCount(interaction, isShared);
        Connection connection = Database.getConnection();
        if (hasEntry("interaction_count", "interaction", interaction)) {
            // Update.
            PreparedStatement update = connection.prepareStatement("UPDATE interaction_count SET " + (isShared ? "shared" : "received") + "=? WHERE user_id=? AND interaction=?");
            update.setInt(1, count + 1);
            update.setLong(2, userId);
            update.setString(3, interaction.toLowerCase());
            update.executeUpdate();
        } else {
            // Insert.
            PreparedStatement insert = connection.prepareStatement("INSERT INTO interaction_count (user_id,interaction,shared,received) VALUES (?,?,?,?)");
            insert.setLong(1, userId);
            insert.setString(2, interaction.toLowerCase());
            if (isShared) {
                insert.setInt(3, 1);
                insert.setInt(4, 0);
            } else {
                insert.setInt(3, 0);
                insert.setInt(4, 1);
            }
            insert.executeUpdate();
        }
        connection.close();
    }
}
