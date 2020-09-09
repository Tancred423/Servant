package plugins.moderation.customcommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import servant.LoggingTask;
import servant.MyGuild;
import servant.MyUser;
import servant.Servant;
import utilities.Console;

import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static servant.Database.closeQuietly;

public class CustomCommand {
    private final JDA jda;
    private final String invoke;

    public CustomCommand(JDA jda, String invoke) {
        this.jda = jda;
        this.invoke = invoke;
    }

    public String getInvoke() {
        return invoke;
    }

    public ArrayList<String> getAliases() {
        Connection connection = null;
        var aliases = new ArrayList<String>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var preparedStatement = connection.prepareStatement(
                    "SELECT alias " +
                            "FROM custom_commands_aliases " +
                            "WHERE cc_id=?");
            preparedStatement.setInt(1, getCcId());
            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) aliases.add(resultSet.getString("alias"));
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "CustomCommand#getCcId"));
        } finally {
            closeQuietly(connection);
        }

        return aliases;
    }

    private int getCcId() {
        Connection connection = null;
        var ccId = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var preparedStatement = connection.prepareStatement(
                    "SELECT id " +
                            "FROM custom_commands " +
                            "WHERE invoke=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            preparedStatement.setString(1, invoke);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) ccId = resultSet.getInt("id");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "CustomCommand#getCcId"));
        } finally {
            closeQuietly(connection);
        }

        return ccId;
    }

    private String getNormalMessage() {
        Connection connection = null;
        var normalMsg = "";

        try {
            connection = Servant.db.getHikari().getConnection();
            var preparedStatement = connection.prepareStatement(
                    "SELECT normal_msg " +
                            "FROM custom_commands " +
                            "WHERE invoke=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            preparedStatement.setString(1, invoke);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) normalMsg = resultSet.getString("normal_msg").trim();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "CustomCommand#getNormalMessage"));
        } finally {
            closeQuietly(connection);
        }

        return normalMsg;
    }

    private MessageEmbed getEmbedMessage() {
        Connection connection = null;
        var eb = new EmbedBuilder();

        try {
            connection = Servant.db.getHikari().getConnection();
            var preparedStatement = connection.prepareStatement(
                    "SELECT * " +
                            "FROM custom_commands_embeds " +
                            "WHERE cc_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            preparedStatement.setInt(1, getCcId());
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) {
                var colorCode = resultSet.getString("colorcode");
                var authorName = resultSet.getString("author_name");
                var authorUrl = resultSet.getString("author_url");
                var authorIconUrl = resultSet.getString("author_icon_url");
                var title = resultSet.getString("title");
                var titleUrl = resultSet.getString("title_url");
                var thumbnailUrl = resultSet.getString("thumbnail_url");
                var description = resultSet.getString("description");
                var imageUrl= resultSet.getString("image_url");
                var footerText = resultSet.getString("footer");
                var footerIconUrl = resultSet.getString("footer_icon_url");
                var timestamp = resultSet.getTimestamp("timestamp");

                eb.setColor(Color.decode(colorCode));

                if (!authorName.isEmpty())
                    eb.setAuthor(authorName, authorUrl.isEmpty() ? null : authorUrl, authorIconUrl.isEmpty() ? null : authorIconUrl);

                if (!title.isEmpty())
                    eb.setTitle(title, titleUrl.isEmpty() ? null : titleUrl);

                if (!thumbnailUrl.isEmpty())
                    eb.setThumbnail(thumbnailUrl);

                if (!description.isEmpty())
                    eb.setDescription(description);

                if (!imageUrl.isEmpty())
                    eb.setImage(imageUrl);

                if (!footerText.isEmpty())
                    eb.setFooter(footerText, footerIconUrl.isEmpty() ? null : footerIconUrl);

                if (timestamp != null) {
                    eb.setTimestamp(timestamp.toInstant());
                }
            }

            preparedStatement = connection.prepareStatement(
                    "SELECT * " +
                            "FROM custom_commands_fields " +
                            "WHERE cc_id=? " +
                            "ORDER BY field_no ASC",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            preparedStatement.setInt(1, getCcId());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) {
                do {
                    var name = resultSet.getString("title");
                    var desc = resultSet.getString("description");
                    var isInline = resultSet.getBoolean("inline");
                    if (!name.isEmpty() || !desc.isEmpty()) eb.addField(name, desc, isInline);
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "CustomCommand#getEmbedMessage"));
        } finally {
            closeQuietly(connection);
        }

        return eb.build();
    }

    public void reply(MessageReceivedEvent event) {
        if (event == null) return;
        var normalMsg = getNormalMessage();
        if (normalMsg.isEmpty()) event.getTextChannel().sendMessage(getEmbedMessage()).queue();
        else event.getTextChannel().sendMessage(normalMsg).queue();

        logAndStatistics(event);
    }

    private void logAndStatistics(MessageReceivedEvent event) {
        // Log
        Console.logCmd(event, true);

        // Statistics
        new MyUser(event.getAuthor()).incrementCommandCount("customcommand");
        if (event.isFromGuild()) new MyGuild(event.getGuild()).incrementCommandCount("customcommand");
    }
}
