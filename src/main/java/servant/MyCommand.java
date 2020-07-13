package servant;

import net.dv8tion.jda.api.JDA;

import java.sql.Connection;
import java.sql.SQLException;

import static servant.Database.closeQuietly;

public class MyCommand {
    private JDA jda;
    private int id;
    private String name;
    private boolean isModCommand;
    private boolean isToggleable;
    private boolean isOwnerCommand;

    public MyCommand(JDA jda, String name) {
        this.jda = jda;
        this.name = name.toLowerCase();
        initialize();
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public boolean isModCommand() { return isModCommand; }
    public boolean isToggleable() { return isToggleable; }
    public boolean isOwnerCommand() { return isOwnerCommand; }

    private void initialize() {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT * " +
                            "FROM const_commands AS com " +
                            "INNER JOIN const_categories AS cat " +
                            "ON com.category_id = cat.id " +
                            "WHERE com.name=?");
            select.setString(1, name);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                this.id = resultSet.getInt("id");
                this.isModCommand = resultSet.getBoolean("is_mod");
                this.isToggleable = resultSet.getBoolean("is_toggleable");
            } else {
                this.id = 0;
                this.isModCommand = false;
                this.isToggleable = false;
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Master#userCommandCountsHasEntry"));
        } finally {
            closeQuietly(connection);
        }

        switch (name) {
            case "blacklist":
            case "addgif":
            case "addjif":
            case "eval":
            case "e":
            case "refresh":
            case "serverlist":
            case "guildlist":
            case "shutdown":
            case "thread":
            case "threads":
                this.isOwnerCommand = true;

            default:
                this.isOwnerCommand = false;
        }
    }
}
