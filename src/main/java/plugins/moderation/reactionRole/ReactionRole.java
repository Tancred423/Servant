package plugins.moderation.reactionRole;

import net.dv8tion.jda.api.JDA;
import servant.LoggingTask;
import servant.Servant;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import static servant.Database.closeQuietly;

public class ReactionRole {
    private final JDA jda;
    private final long msgId;

    public ReactionRole(JDA jda, long msgId) {
        this.jda = jda;
        this.msgId = msgId;
    }

    public ArrayList<Long> getRoleIds(String emoji) {
        Connection connection = null;
        var roleIds = new ArrayList<Long>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT role_id, emoji " +
                            "FROM reaction_roles " +
                            "WHERE msg_id=?");
            select.setLong(1, msgId);
            var resultSet = select.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getString("emoji").equals(emoji))
                    roleIds.add(resultSet.getLong("role_id"));
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "ReactionRole#getRoleIds"));
        } finally {
            closeQuietly(connection);
        }

        return roleIds;
    }

    public void deleteRoleId(long roleId) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement(
                    "DELETE FROM reaction_roles WHERE msg_id=? AND role_id=?");
            delete.setLong(1, msgId);
            delete.setLong(2, roleId);
            delete.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "ReactionRole#deleteRoleId"));
        } finally {
            closeQuietly(connection);
        }
    }
}
