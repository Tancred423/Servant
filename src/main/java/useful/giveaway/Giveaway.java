// Author: Tancred423 (https://github.com/Tancred423)
package useful.giveaway;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import servant.Log;
import servant.Servant;
import utilities.Emote;
import utilities.Image;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static servant.Servant.jda;
import static utilities.DatabaseConn.closeQuietly;

public class Giveaway {
    private static ZonedDateTime getDate(ZonedDateTime date, String timeString) throws NumberFormatException {
        var timeArray = timeString.split(" ");
        String days;
        String hours;
        String minutes;

        for (var time : timeArray)
            if (time.toLowerCase().endsWith("d")) {
                days = time.replaceAll("d", "");
                date = date.plusDays(Integer.parseInt(days));
            } else if (time.toLowerCase().endsWith("h")) {
                hours = time.replaceAll("h", "");
                date = date.plusHours(Integer.parseInt(hours));
            } else if (time.toLowerCase().endsWith("m")) {
                minutes = time.replaceAll("m", "");
                date = date.plusMinutes(Integer.parseInt(minutes));
            }

        return date;
    }

    private static String formatDifference(long millis, String lang) {
        var days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        var hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        var minutes = TimeUnit.MILLISECONDS.toMinutes(millis);

        return (days == 0 ? "" : String.format(LanguageHandler.get(lang, "giveaway_days"), days) + " ") +
                (hours == 0 ? "" : String.format(LanguageHandler.get(lang, "giveaway_hours"), hours) + " ") +
                String.format(LanguageHandler.get(lang, "giveaway_minutes"), minutes);
    }

    public static void checkGiveaways(JDA jda) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM giveawaylist");
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                do {
                    var guildId = resultSet.getLong("guild_id");
                    if (jda.getGuildById(guildId) != null) {
                        var channelId = resultSet.getLong("channel_id");
                        var messageId = resultSet.getLong("message_id");
                       var hostId = resultSet.getLong("host_id");
                        var prize = resultSet.getString("prize");
                        var giveaway = resultSet.getTimestamp("time").toLocalDateTime()
                                .atZone(ZoneId.of(new Guild(jda.getGuildById(guildId).getIdLong())
                                        .getOffset(jda.getGuildById(guildId), jda.getSelfUser())));
                        var amountWinners = resultSet.getInt("amount_winners");

                        if (jda.getGuildById(guildId) != null
                                && jda.getGuildById(guildId).getTextChannelById(channelId) != null
                                && jda.getGuildById(guildId).getTextChannelById(channelId).getMessageById(messageId) != null
                                && jda.getUserById(hostId) != null) {
                            jda.getGuildById(guildId).getTextChannelById(channelId).getMessageById(messageId).queue(message -> {
                                var guild = message.getGuild();
                                var author = jda.getUserById(hostId);
                                var lang = new Guild(guild.getIdLong()).getLanguage(guild, jda.getSelfUser());
                                var now = ZonedDateTime.now(ZoneOffset.of(new Guild(message.getGuild().getIdLong()).getOffset(guild, author)));

                                var remainingTimeMillis = zonedDateTimeDifference(now, giveaway);

                                if (remainingTimeMillis <= 0) announceWinners(message, amountWinners, prize, lang, author);
                                else {
                                    var remainingTimeString = formatDifference(remainingTimeMillis, lang);

                                    var eb = new EmbedBuilder();
                                    eb.setColor(new moderation.user.User(message.getAuthor().getIdLong()).getColor(guild, author));
                                    eb.setAuthor(String.format(LanguageHandler.get(lang, "giveaway_from"), author.getName()), null, author.getEffectiveAvatarUrl());
                                    eb.setDescription(String.format(LanguageHandler.get(lang, "giveaway_description_running"), prize, amountWinners, remainingTimeString, Emote.getEmoji("tada")));
                                    eb.setFooter(LanguageHandler.get(lang, "giveaway_endsat"), Image.getImageUrl("clock", guild, author));
                                    eb.setTimestamp(giveaway);

                                    message.editMessage(eb.build()).queue();
                                }

                            });
                        } else
                            deleteGiveawayFromDb(guildId, channelId, messageId, jda.getGuildById(guildId), jda.getSelfUser());
                    }
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            new Log(e, null, jda.getSelfUser(), "giveaway", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    private static void announceWinners(Message message, int amountWinners, String prize, String lang, User author) {
        if (message.getReactions().size() == 0) {
            message.getChannel().sendMessage(LanguageHandler.get(lang, "giveaway_noreactions")).queue();
            return;
        }

        var guild = message.getGuild();

        for (int i = 0; i < message.getReactions().size(); i++) {
            if (message.getReactions().get(i).getReactionEmote().getName().equals(Emote.getEmoji("tada"))) {
                message.getReactions().get(i).getUsers().queue(participantsList -> {
                    participantsList.remove(message.getJDA().getSelfUser()); // Remove bot
                    var winners = new StringBuilder();
                    var amountParticipants = participantsList.size();
                    var now = ZonedDateTime.now(ZoneOffset.of(new Guild(message.getGuild().getIdLong()).getOffset(guild, author)));

                    if (amountParticipants == 0) {
                        var eb = new EmbedBuilder();
                        eb.setColor(new moderation.user.User(author.getIdLong()).getColor(guild, author));
                        eb.setAuthor(String.format(LanguageHandler.get(lang, "giveaway_from"), author.getName()), null, author.getEffectiveAvatarUrl());
                        eb.setDescription(String.format(LanguageHandler.get(lang, "giveaway_description_nowinner"), prize, amountWinners));
                        eb.setFooter(LanguageHandler.get(lang, "giveaway_endedat"), Image.getImageUrl("clock", guild, author));
                        eb.setTimestamp(now);
                        message.editMessage(eb.build()).queue();
                    } else {
                        if (amountParticipants > amountWinners) {
                            for (int j = 0; j < amountWinners; j++) {
                                var winnerNumber = ThreadLocalRandom.current().nextInt(participantsList.size());
                                winners.append(" - ").append(participantsList.get(winnerNumber).getAsMention()).append("\n");
                                participantsList.remove(winnerNumber);
                            }
                        } else for (var participant : participantsList) winners.append(" - ").append(participant.getAsMention()).append("\n");

                        var eb = new EmbedBuilder();
                        eb.setColor(new moderation.user.User(message.getAuthor().getIdLong()).getColor(guild, author));
                        eb.setAuthor(String.format(LanguageHandler.get(lang, "giveaway_from"), author.getName()), null, author.getEffectiveAvatarUrl());
                        eb.setDescription(String.format(LanguageHandler.get(lang, "giveaway_description_end"), prize, amountWinners, winners));
                        eb.setFooter(LanguageHandler.get(lang, "giveaway_endedat"), null);
                        eb.setTimestamp(now);
                        message.editMessage(eb.build()).queue();
                    }

                    message.clearReactions().queue();
                    deleteGiveawayFromDb(message.getGuild().getIdLong(), message.getChannel().getIdLong(), message.getIdLong(), guild, author);
                });
            }
        }
    }

    // Inserts an entry for a new giveaway into the database table "giveawaylist".
    private static void insertGiveawayToDb(long guildId, long channelId, long messageId, long hostId, String prize, Timestamp time, int amountWinners, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var preparedStatement = connection.prepareStatement("INSERT INTO giveawaylist(guild_id,channel_id,message_id,host_id,prize,time,amount_winners) VALUES(?,?,?,?,?,?,?)");
            preparedStatement.setLong(1, guildId);
            preparedStatement.setLong(2, channelId);
            preparedStatement.setLong(3, messageId);
            preparedStatement.setLong(4, hostId);
            preparedStatement.setString(5, prize);
            preparedStatement.setTimestamp(6, time);
            preparedStatement.setInt(7, amountWinners);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            new Log(e, guild, user, "giveaway", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    // Deletes the giveaway entry from the database table "giveawaylist".
    static void deleteGiveawayFromDb(long guildId, long channelId, long messageId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement("DELETE FROM giveawaylist WHERE guild_id=? AND channel_id=? AND message_id=?");
            delete.setLong(1, guildId);
            delete.setLong(2, channelId);
            delete.setLong(3, messageId);
            delete.executeUpdate();
        } catch (SQLException e) {
            new Log(e, guild, user, "giveaway", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    static void deleteGiveawayFromDb(long guildId, long channelId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement("DELETE FROM giveawaylist WHERE guild_id=? AND channel_id=?");
            delete.setLong(1, guildId);
            delete.setLong(2, channelId);
            delete.executeUpdate();
        } catch (SQLException e) {
            new Log(e, guild, user, "giveaway", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    static void deleteGiveawayFromDb(long guildId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement("DELETE FROM giveawaylist WHERE guild_id=?");
            delete.setLong(1, guildId);
            delete.executeUpdate();
        } catch (SQLException e) {
            new Log(e, guild, user, "giveaway", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    // Getter for all running giveaways on the current guild.
    private static String getRunningGiveaways(JDA jda, ResultSet resultSet, String lang) throws SQLException {
        var giveawayList = new StringBuilder();
        do giveawayList.append("- ")
                .append(jda.getGuildById(resultSet.getLong("guild_id")).getTextChannelById(resultSet.getLong("channel_id")).getAsMention())
                .append(" ").append(LanguageHandler.get(lang, "giveaway_messageid")).append(" ").append(resultSet.getLong("message_id"))
                .append(" ").append(LanguageHandler.get(lang, "giveaway_prize")).append(" ").append(resultSet.getString("prize"))
                .append("\n"); while (resultSet.next());

        return giveawayList.toString();
    }

    private static void sendWrongTimeArgumentError(Message message, String lang) {
        message.getChannel().sendMessage(LanguageHandler.get(lang, "giveaway_invalidtime")).queue();
    }

    static void sendWrongArgumentError(Message message, String lang) {
        message.getChannel().sendMessage(LanguageHandler.get(lang, "giveaway_wrongargument")).queue();
    }

    static String getCurrentGiveaways(Message message, String lang, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var currentGiveaways = LanguageHandler.get(lang, "giveaway_nocurrent");

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM giveawaylist WHERE guild_id=?");
            select.setLong(1, message.getGuild().getIdLong());
            var resultSet = select.executeQuery();
            if (resultSet.first()) currentGiveaways = getRunningGiveaways(jda, resultSet, lang);
        } catch (SQLException e) {
            new Log(e, guild, user, "giveaway", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return currentGiveaways;
    }

    public static long zonedDateTimeDifference(ZonedDateTime zonedDateTime1, ZonedDateTime zonedDateTime2) {
        return ChronoUnit.MILLIS.between(zonedDateTime1, zonedDateTime2);
    }

    static void startGiveaway(CommandEvent event, String[] args, String lang) {
        var message = event.getMessage();
        var argsString = new StringBuilder();
        var prize = "";
        var amountWinners = 0;
        var sb = new StringBuilder();

        var guild = event.getGuild();
        var author = event.getAuthor();

        for (int i = 0; i < args.length; i++) {
            argsString.append(args[i]).append(" ");

            try {
                if (args[i].endsWith("\"") && !args[i+1].equals("\"")) {
                    prize = argsString.toString().replaceAll("\"", "");
                    if (!args[i + 1].matches("[0-9]+")) {
                        if (args[0].equalsIgnoreCase("\"\""))
                            event.reply(LanguageHandler.get(lang, "giveaway_emptyprize"));
                        else event.reply(LanguageHandler.get(lang, "giveaway_invalidwinneramount"));
                        event.reactError();
                        return;
                    }
                    amountWinners = Integer.parseInt(args[i + 1]);
                    for (int j = i + 2; j < args.length; j++) sb.append(args[j]).append(" ");
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                sendWrongArgumentError(message, lang);
                return;
            }
        }

        if (amountWinners < 1) {
            event.reply(LanguageHandler.get(lang, "giveaway_zerowinners"));
            event.reactWarning();
            return;
        }

        if (prize.trim().isEmpty()) {
            event.reply(LanguageHandler.get(lang, "giveaway_emptyprize"));
            return;
        }

        var timeArray = sb.toString().split(" ");
        String days;
        String hours;
        String minutes;

        for (var time : timeArray) {
            if (time.toLowerCase().endsWith("d")) {
                days = time.replaceAll("d", "");
                if (!days.matches("[0-9]+")) {
                    sendWrongTimeArgumentError(message, lang);
                    return;
                }
            } else if (time.toLowerCase().endsWith("h")) {
                hours = time.replaceAll("h", "");
                if (!hours.matches("[0-9]+")) {
                    sendWrongTimeArgumentError(message, lang);
                    return;
                }
            } else if (time.toLowerCase().endsWith("m")) {
                minutes = time.replaceAll("m", "");
                if (!minutes.matches("[0-9]+")) {
                    sendWrongTimeArgumentError(message, lang);
                    return;
                }
            } else {
                sendWrongTimeArgumentError(message, lang);
                return;
            }
        }

        var now = ZonedDateTime.now(ZoneOffset.of(new Guild(message.getGuild().getIdLong()).getOffset(guild, author)));
        ZonedDateTime dateGiveaway;
        try {
            dateGiveaway = getDate(now, sb.toString().trim());
        } catch (NumberFormatException e) {
            sendWrongArgumentError(message, lang);
            return;
        }

        if (dateGiveaway == null) {
            message.getChannel().sendMessage(LanguageHandler.get(lang, "giveaway_messedupargs")).queue();
            return;
        }

        var remainingTimeMillis = zonedDateTimeDifference(now, dateGiveaway);
        var remainingTimeString = formatDifference(remainingTimeMillis, lang);

        var eb = new EmbedBuilder();
        eb.setColor(new moderation.user.User(message.getAuthor().getIdLong()).getColor(guild, author));
        eb.setAuthor(String.format(LanguageHandler.get(lang, "giveaway_from"), message.getAuthor().getName()), null, message.getAuthor().getEffectiveAvatarUrl());
        eb.setDescription(String.format(LanguageHandler.get(lang, "giveaway_description_running"), prize, amountWinners, remainingTimeString, Emote.getEmoji("tada")));
        eb.setFooter(LanguageHandler.get(lang, "giveaway_endsat"), Image.getImageUrl("clock", guild, author));
        eb.setTimestamp(dateGiveaway);

        var finalPrize1 = prize;
        var finalAmountWinners = amountWinners;
        message.getTextChannel().sendMessage(eb.build()).queue((messageNew -> {
            message.delete().queue();
            insertGiveawayToDb(messageNew.getGuild().getIdLong(), messageNew.getChannel().getIdLong(),
                    messageNew.getIdLong(), message.getAuthor().getIdLong(), finalPrize1, Timestamp.valueOf(dateGiveaway.toLocalDateTime()), finalAmountWinners, guild, author);
            messageNew.addReaction(Emote.getEmoji("tada")).queue();
        }));
    }
}
