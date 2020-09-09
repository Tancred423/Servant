package plugins.moderation.customcommands;

import net.dv8tion.jda.api.JDA;
import servant.LoggingTask;
import servant.Servant;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import static servant.Database.closeQuietly;

public class CustomCommandsHandler {
    public static CustomCommand getCustomCommandFromInvokeOrAlias(JDA jda, long guildId,  String invokeOrAlias) {
        Connection connection = null;
        var customCommands = new ArrayList<CustomCommand>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var preparedStatement = connection.prepareStatement(
                    "SELECT invoke " +
                            "FROM custom_commands " +
                            "WHERE guild_id=?");
            preparedStatement.setLong(1, guildId);
            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) customCommands.add(new CustomCommand(jda, resultSet.getString("invoke")));
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyMessage#isCustomCommand"));
        } finally {
            closeQuietly(connection);
        }

        for (var customCommand : customCommands) {
            if (customCommand.getInvoke().equals(invokeOrAlias)) {
                return customCommand;
            } else {
                for (var alias : customCommand.getAliases()) if (alias.equals(invokeOrAlias)) return customCommand;
            }
        }

        return null;
    }
}
