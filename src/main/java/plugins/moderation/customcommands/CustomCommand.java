package plugins.moderation.customcommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import servant.*;
import utilities.ConsoleLog;
import utilities.MessageUtil;

import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static servant.Database.closeQuietly;

public class CustomCommand {
    private final MessageReceivedEvent event;
    private final String invoke;

    public CustomCommand(MessageReceivedEvent event) {
        this.event = event;
        var tmpInvoke = event.getMessage().getContentDisplay().split(" ")[0];
        this.invoke = MessageUtil.removePrefix(event.getJDA(), event.getGuild().getIdLong(), event.isFromGuild(), tmpInvoke);
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
            Servant.fixedThreadPool.submit(new LoggingTask(e, event.getJDA(), "CustomCommand#getCcId"));
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
            Servant.fixedThreadPool.submit(new LoggingTask(e, event.getJDA(), "CustomCommand#getNormalMessage"));
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
            Servant.fixedThreadPool.submit(new LoggingTask(e, event.getJDA(), "CustomCommand#getEmbedMessage"));
        } finally {
            closeQuietly(connection);
        }

        return eb.build();
    }

    public void reply() {
        var normalMsg = getNormalMessage();
        if (normalMsg.isEmpty()) event.getTextChannel().sendMessage(getEmbedMessage()).queue();
        else event.getTextChannel().sendMessage(normalMsg).queue();

        logAndStatistics();
    }

    private void logAndStatistics() {
        // Log
        ConsoleLog.send(event, true);

        // Statistics
        new MyUser(event.getAuthor()).incrementCommandCount("customcommand");
        if (event.isFromGuild()) new MyGuild(event.getGuild()).incrementCommandCount("customcommand");
    }
}
