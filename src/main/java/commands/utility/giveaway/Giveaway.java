package commands.utility.giveaway;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import servant.*;
import utilities.*;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static servant.Database.closeQuietly;

public class Giveaway {
    private final JDA jda;
    private final String lang;
    private final long guildId;
    private final long tcId;
    private final long msgId;

    public Giveaway(JDA jda, String lang, long guildId, long tcId, long msgId) {
        this.jda = jda;
        this.lang = lang;
        this.guildId = guildId;
        this.tcId = tcId;
        this.msgId = msgId;
    }

    public long getGuildId() {
        return guildId;
    }

    public long getTcId() {
        return tcId;
    }

    public long getMsgId() {
        return msgId;
    }

    public long getAuthorId() {
        Connection connection = null;
        var authorId = 0L;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT author_id " +
                            "FROM giveaways " +
                            "WHERE guild_id=? " +
                            "AND tc_id=? " +
                            "AND msg_id=?");
            select.setLong(1, guildId);
            select.setLong(2, tcId);
            select.setLong(3, msgId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) authorId = resultSet.getLong("author_id");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Giveaway#getAuthorId"));
        } finally {
            closeQuietly(connection);
        }

        return authorId;
    }

    public String getPrize() {
        Connection connection = null;
        var prize = "";

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT prize " +
                            "FROM giveaways " +
                            "WHERE guild_id=? " +
                            "AND tc_id=? " +
                            "AND msg_id=?");
            select.setLong(1, guildId);
            select.setLong(2, tcId);
            select.setLong(3, msgId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) prize = resultSet.getString("prize");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Giveaway#getPrize"));
        } finally {
            closeQuietly(connection);
        }

        return prize;
    }

    public Instant getEventTime() {
        Connection connection = null;
        Instant eventTime = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT event_time " +
                            "FROM giveaways " +
                            "WHERE guild_id=? " +
                            "AND tc_id=? " +
                            "AND msg_id=?");
            select.setLong(1, guildId);
            select.setLong(2, tcId);
            select.setLong(3, msgId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) eventTime = resultSet.getTimestamp("event_time").toInstant();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Giveaway#getEventTime"));
        } finally {
            closeQuietly(connection);
        }

        return eventTime;
    }

    public int getAmountWinners() {
        Connection connection = null;
        var amountWinners = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT amount_winners " +
                            "FROM giveaways " +
                            "WHERE guild_id=? " +
                            "AND tc_id=? " +
                            "AND msg_id=?");
            select.setLong(1, guildId);
            select.setLong(2, tcId);
            select.setLong(3, msgId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) amountWinners = resultSet.getInt("amount_winners");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Giveaway#getAmountWinners"));
        } finally {
            closeQuietly(connection);
        }

        return amountWinners;
    }

    public void purge() {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();

            // Giveaway
            var delete = connection.prepareStatement(
                    "DELETE FROM giveaways " +
                            "WHERE guild_id=? " +
                            "AND tc_id=? " +
                            "AND msg_id=?");
            delete.setLong(1, guildId);
            delete.setLong(2, tcId);
            delete.setLong(3, msgId);
            delete.executeUpdate();

            // Giveaway entries
            delete = connection.prepareStatement(
                    "DELETE FROM tmp_giveaway_participants " +
                            "WHERE msg_id=?");
            delete.setLong(1, msgId);
            delete.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Giveaway#purge"));
        } finally {
            closeQuietly(connection);
        }
    }

    public ArrayList<Long> getParticipants() {
        Connection connection = null;
        var participants = new ArrayList<Long>();

        if (new MyMessage(jda, guildId, tcId, msgId).isGiveaway()) {
            try {
                connection = Servant.db.getHikari().getConnection();
                var preparedStatement = connection.prepareStatement(
                        "SELECT user_id " +
                                "FROM tmp_giveaway_participants " +
                                "WHERE msg_id=?");
                preparedStatement.setLong(1, msgId);
                var resultSet = preparedStatement.executeQuery();
                if (resultSet.first()) {
                    do {
                        participants.add(resultSet.getLong("user_id"));
                    } while (resultSet.next());
                }
            } catch (SQLException e) {
                Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Giveaway#getParticipants"));
            } finally {
                closeQuietly(connection);
            }
        }

        return participants;
    }

    public boolean setParticipant(long userId) {
        var wasSet = false;
        if (new MyMessage(jda, guildId, tcId, msgId).isGiveaway() && !getParticipants().contains(userId)) {
            Connection connection = null;
            try {
                connection = Servant.db.getHikari().getConnection();
                var insert = connection.prepareStatement(
                        "INSERT INTO tmp_giveaway_participants (msg_id,user_id,guild_id,tc_id) " +
                                "VALUES (?,?,?,?)");
                insert.setLong(1, msgId);
                insert.setLong(2, userId);
                insert.setLong(3, guildId);
                insert.setLong(4, tcId);
                insert.executeUpdate();
                wasSet = true;
            } catch (SQLException e) {
                Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Giveaway#setParticipant"));
            } finally {
                closeQuietly(connection);
            }
        }
        return wasSet;
    }

    public void end() {
        var myMessage = new MyMessage(jda, guildId, tcId, msgId);
        if (!myMessage.isGiveaway()) return;

        var authorId = getAuthorId();
        var author = jda.getUserById(authorId);
        if (author == null) {
            purge();
            return;
        }

        var guild = jda.getGuildById(guildId);
        if (guild == null) {
            purge();
            return;
        }

        var tc = guild.getTextChannelById(tcId);
        if (tc == null) {
            purge();
            return;
        }

        tc.retrieveMessageById(msgId).queue(message -> {
            if (message == null) {
                purge();
                return;
            }

            var participants = getParticipants();
            var amountParticipants = participants.size();
            var now = LocalDateTime.now();
            var prize = getPrize();
            var amountWinners = getAmountWinners();

            EmbedBuilder eb;
            if (amountParticipants == 0) {
                eb = new EmbedBuilder();
                eb.setColor(Color.decode(new MyUser(author).getColorCode()));
                eb.setAuthor(String.format(LanguageHandler.get(lang, "giveaway_title"), prize), null, author.getEffectiveAvatarUrl());
                eb.setDescription(String.format(LanguageHandler.get(lang, "giveaway_description_nowinner"), author.getAsMention(), amountWinners));
                eb.setFooter(LanguageHandler.get(lang, "giveaway_endedat"), ImageUtil.getUrl(message.getJDA(), "clock"));
                eb.setTimestamp(now);
            } else {
                eb = new EmbedBuilder()
                        .setColor(Color.decode(new MyUser(message.getAuthor()).getColorCode()))
                        .setAuthor(String.format(LanguageHandler.get(lang, "giveaway_title"), prize), null, author.getEffectiveAvatarUrl())
                        .setDescription(String.format(LanguageHandler.get(lang, "giveaway_description_end"), author.getAsMention(), amountWinners))
                        .setFooter(LanguageHandler.get(lang, "giveaway_endedat"), null)
                        .setTimestamp(now);

                var sb = new StringBuilder();
                if (amountParticipants > amountWinners) {
                    for (int j = 0; j < amountWinners; j++) {
                        var winnerNumber = ThreadLocalRandom.current().nextInt(participants.size());
                        var winnerId = participants.get(winnerNumber);

                        var winner = message.getGuild().getMemberById(winnerId);

                        // Skip members who left the guild.
                        if (winner == null) {
                            j--;
                            continue;
                        }

                        if (sb.toString().length() + winner.getAsMention().length() + 2 > 1024) {
                            eb.addField("", sb.toString(), true);
                            sb = new StringBuilder();
                        }

                        sb.append(winner.getAsMention()).append("\n");
                        participants.remove(winnerNumber);
                    }
                } else {
                    for (var participantId : participants) {
                        var participant = message.getGuild().getMemberById(participantId);

                        // Skip members who left the guild.
                        if (participant == null) continue;

                        if (sb.toString().length() + participant.getAsMention().length() + 2 > 1024) {
                            eb.addField("", sb.toString(), true);
                            sb = new StringBuilder();
                        }
                        sb.append(participant.getAsMention()).append("\n");
                    }
                }
                eb.addField("", sb.toString(), true);
            }
            message.editMessage(eb.build()).queue();

            message.clearReactions().queue();
            purge();
        });
    }

    static void startGiveaway(CommandEvent event, String lang) throws ParseException {
        var jda = event.getJDA();
        var myGuild = new MyGuild(event.getGuild());
        var message = event.getMessage();
        var author = event.getAuthor();

        var prize = Parser.parseText(lang, event.getArgs(), 500);
        if (prize == null || prize.isEmpty())
            throw new ParseException(LanguageHandler.get(lang, "giveaway_emptyprize"), 0);

        var argList = Parser.parseArguments(lang, event.getArgs());

        var amountWinners = argList.get('w');
        if (amountWinners == null || amountWinners == 0)
            throw new ParseException(LanguageHandler.get(lang, "giveaway_zerowinners"), 0);

        var days = argList.get('d');
        var hours = argList.get('h');
        var mins = argList.get('m');

        var eventTime = Instant.now();
        var now = eventTime;
        if (days != null) eventTime = eventTime.plusMillis(TimeUtil.daysToMillis(days));
        if (hours != null) eventTime = eventTime.plusMillis(TimeUtil.hoursToMillis(hours));
        if (mins != null) eventTime = eventTime.plusMillis(TimeUtil.minutesToMillis(mins));

        if (eventTime.toEpochMilli() == now.toEpochMilli())
            throw new ParseException(LanguageHandler.get(lang, "missing_time_args"), 0);

        var nowIn30Days = now.plusMillis(TimeUtil.daysToMillis(30));

        if (eventTime.toEpochMilli() > nowIn30Days.toEpochMilli())
            throw new ParseException(LanguageHandler.get(lang, "giveaway_30_days"), 0);

        var eb = new EmbedBuilder();
        eb.setColor(Color.decode(new MyUser(message.getAuthor()).getColorCode()));
        eb.setAuthor(String.format(LanguageHandler.get(lang, "giveaway_title"), prize), null, message.getAuthor().getEffectiveAvatarUrl());
        eb.setDescription(String.format(LanguageHandler.get(lang, "giveaway_description_running"), message.getAuthor().getAsMention(), amountWinners, EmoteUtil.getEmoji(jda, "tada")));
        eb.appendDescription("\n\n" + String.format(LanguageHandler.get(lang, "giveaway_end_manually"), author.getAsMention()));
        eb.setFooter(LanguageHandler.get(lang, "giveaway_endsat"), ImageUtil.getUrl(event.getJDA(), "clock"));
        eb.setTimestamp(eventTime);

        var finalEventTime = eventTime;
        message.getTextChannel().sendMessage(eb.build()).queue((messageNew -> {
            myGuild.insertGiveawayToDb(messageNew.getChannel().getIdLong(),
                    messageNew.getIdLong(), message.getAuthor().getIdLong(), prize, Timestamp.from(finalEventTime), amountWinners);
            var emote = EmoteUtil.getEmoji(jda, "tada");
            var end = EmoteUtil.getEmoji(jda, "end");
            if (emote != null) messageNew.addReaction(emote).queue();
            if (end != null) messageNew.addReaction(end).queue();

            /* End giveaway to given time.
             * In case the bot restarts, this service will break.
             * Therefore we will run any current giveaway in the database
             * from listeners.ReadyListener#startExecutorNew.
             */
            var giveaway = new Giveaway(jda, lang, event.getGuild().getIdLong(), event.getTextChannel().getIdLong(), messageNew.getIdLong());
            var delayInMillis = finalEventTime.toEpochMilli() - Instant.now().toEpochMilli();
            Servant.scheduledService.schedule(
                    new GiveawayEndTask(giveaway),
                    delayInMillis,
                    TimeUnit.MILLISECONDS
            );
        }));
    }

    public static List<Giveaway> getList(JDA jda) {
        Connection connection = null;
        var giveaways = new ArrayList<Giveaway>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM giveaways",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                do {
                    var guildId = resultSet.getLong("guild_id");
                    var guild = jda.getGuildById(guildId);
                    if (guild == null) continue;
                    var lang = new MyGuild(guild).getLanguageCode();
                    giveaways.add(new Giveaway(jda, lang, guildId, resultSet.getLong("tc_id"), resultSet.getLong("msg_id")));
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Giveaway#getList"));
        } finally {
            closeQuietly(connection);
        }

        return giveaways;
    }
}
