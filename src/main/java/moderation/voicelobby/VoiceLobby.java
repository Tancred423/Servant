package moderation.voicelobby;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import servant.LoggingTask;
import servant.Servant;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import static servant.Database.closeQuietly;

public class VoiceLobby {
    private JDA jda;
    private long guildId;
    private long channelId;

    public VoiceLobby(JDA jda, long guildId, long channelId) {
        this.jda = jda;
        this.guildId = guildId;
        this.channelId = channelId;
    }

    // Active Lobbies
    public void setActive() {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var insert = connection.prepareStatement("INSERT INTO active_lobbies (channel_id) VALUES (?)");
            insert.setLong(1, channelId);
            insert.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "VoiceLobby#setActive"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void unsetActive() {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement("DELETE FROM active_lobbies WHERE channel_id=?");
            delete.setLong(1, channelId);
            delete.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "VoiceLobby#unsetActive"));
        } finally {
            closeQuietly(connection);
        }
    }

    public static List<Long> getActive(JDA jda) {
        Connection connection = null;
        var activeLobbies = new LinkedList<Long>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM active_lobbies");
            var resultSet = select.executeQuery();
            if (resultSet.first())
                do activeLobbies.add(resultSet.getLong("channel_id"));
                while (resultSet.next());
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "VoiceLobby#getActive"));
        } finally {
            closeQuietly(connection);
        }

        return activeLobbies;
    }

    // Lobbies
    private boolean hasEntry() {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM lobby WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "VoiceLobby#lobbyHasEntry"));
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public boolean isVoiceLobby() {
        Connection connection = null;
        var isLobby = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM lobby WHERE guild_id=? AND channel_id=?");
            select.setLong(1, guildId);
            select.setLong(2, channelId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) isLobby = true;
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "VoiceLobby#isVoiceLobby"));
        } finally {
            closeQuietly(connection);
        }

        return isLobby;
    }

    public void set() {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var insert = connection.prepareStatement("INSERT INTO lobby (guild_id,channel_id) VALUES (?,?)");
            insert.setLong(1, guildId);
            insert.setLong(2, channelId);
            insert.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "VoiceLobby#set"));
        } finally {
            closeQuietly(connection);
        }
    }

    public boolean unset() {
        Connection connection = null;
        var wasUnset = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (hasEntry()) {
                var delete = connection.prepareStatement("DELETE FROM lobby WHERE guild_id=? and channel_id=?");
                delete.setLong(1, guildId);
                delete.setLong(2, channelId);
                delete.executeUpdate();
                wasUnset = true;
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "VoiceLobby#unset"));
        } finally {
            closeQuietly(connection);
        }

        return wasUnset;
    }

    public static String getVoiceLobbyName(Member member, String lang) {
        return "⤷ " + member.getEffectiveName() +
                (member.getEffectiveName().toLowerCase().endsWith("s") ?
                        LanguageHandler.get(lang, "apostrophe") :
                        LanguageHandler.get(lang, "apostrophe_s")) + " Lobby"; }
}
