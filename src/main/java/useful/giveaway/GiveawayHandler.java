// Author: Tancred423 (https://github.com/Tancred423)
package useful.giveaway;

import files.language.LanguageHandler;
import moderation.guild.Server;
import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import servant.Log;
import servant.Servant;
import utilities.EmoteUtil;
import utilities.ImageUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static servant.Database.closeQuietly;

public class GiveawayHandler {
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
                        var thisGuild = jda.getGuildById(guildId);
                        if (thisGuild == null) return;
                        var server = new Server(thisGuild);
                        var giveaway = resultSet.getTimestamp("time").toLocalDateTime()
                                .atZone(ZoneId.of(new Server(thisGuild)
                                        .getOffset()));
                        var amountWinners = resultSet.getInt("amount_winners");

                        var tc = thisGuild.getTextChannelById(channelId);
                        if (tc == null) return;
                        var hostUser = jda.getUserById(hostId);

                        if (hostUser != null) {
                            tc.retrieveMessageById(messageId).queue(message -> {
                                var guild = message.getGuild();
                                var author = jda.getUserById(hostId);
                                if (author == null) return;
                                var lang = new Server(guild).getLanguage();
                                var now = ZonedDateTime.now(ZoneOffset.of(new Server(message.getGuild()).getOffset()));

                                var remainingTimeMillis = zonedDateTimeDifference(now, giveaway);

                                if (remainingTimeMillis <= 0) announceWinners(message, amountWinners, prize, lang, author);
                                else {
                                    var remainingTimeString = formatDifference(remainingTimeMillis, lang);

                                    var eb = new EmbedBuilder();
                                    eb.setColor(new Master(hostUser).getColor());
                                    eb.setAuthor(String.format(LanguageHandler.get(lang, "giveaway_from"), author.getName()), null, author.getEffectiveAvatarUrl());
                                    eb.setDescription(String.format(LanguageHandler.get(lang, "giveaway_description_running"), prize, amountWinners, remainingTimeString, EmoteUtil.getEmoji("tada")));
                                    eb.appendDescription("\n\n" + String.format(LanguageHandler.get(lang, "giveaway_end_manually"), author.getAsMention()));
                                    eb.setFooter(LanguageHandler.get(lang, "giveaway_endsat"), ImageUtil.getImageUrl("clock", guild, author));
                                    eb.setTimestamp(giveaway);

                                    message.editMessage(eb.build()).queue();
                                }

                            }, failure -> server.deleteGiveawayFromDb( channelId, messageId));
                        } else server.deleteGiveawayFromDb(channelId, messageId);
                    }
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            new Log(e, null, jda.getSelfUser(), "giveaway", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public static void announceWinners(Message message, int amountWinners, String prize, String lang, User author) {
        var server = new Server(message.getGuild());

        if (message.getReactions().size() == 0) {
            message.getChannel().sendMessage(String.format(LanguageHandler.get(lang, "giveaway_noreactions"), message.getIdLong())).queue();
            server.deleteGiveawayFromDb(message.getChannel().getIdLong(), message.getIdLong());
            return;
        }

        var guild = message.getGuild();

        for (int i = 0; i < message.getReactions().size(); i++) {
            if (message.getReactions().get(i).getReactionEmote().getName().equals(EmoteUtil.getEmoji("tada"))) {
                message.getReactions().get(i).retrieveUsers().queue(participantsList -> {
                    participantsList.remove(message.getJDA().getSelfUser()); // Remove bot
                    var winners = new StringBuilder();
                    var amountParticipants = participantsList.size();
                    var now = ZonedDateTime.now(ZoneOffset.of(new Server(message.getGuild()).getOffset()));

                    if (amountParticipants == 0) {
                        var eb = new EmbedBuilder();
                        eb.setColor(new Master(author).getColor());
                        eb.setAuthor(String.format(LanguageHandler.get(lang, "giveaway_from"), author.getName()), null, author.getEffectiveAvatarUrl());
                        eb.setDescription(String.format(LanguageHandler.get(lang, "giveaway_description_nowinner"), prize, amountWinners));
                        eb.setFooter(LanguageHandler.get(lang, "giveaway_endedat"), ImageUtil.getImageUrl("clock", guild, author));
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

                        message.editMessage(new EmbedBuilder()
                                .setColor(new Master(author).getColor())
                                .setAuthor(String.format(LanguageHandler.get(lang, "giveaway_from"), author.getName()), null, author.getEffectiveAvatarUrl())
                                .setDescription(String.format(LanguageHandler.get(lang, "giveaway_description_end"), prize, amountWinners, winners))
                                .setFooter(LanguageHandler.get(lang, "giveaway_endedat"), null)
                                .setTimestamp(now).build()
                        ).queue();
                    }

                    message.clearReactions().queue();
                    server.deleteGiveawayFromDb(message.getChannel().getIdLong(), message.getIdLong());
                });
            }
        }
    }

    private static void sendWrongTimeArgumentError(Message message, String lang) {
        message.getChannel().sendMessage(LanguageHandler.get(lang, "giveaway_invalidtime")).queue();
    }

    static void sendWrongArgumentError(Message message, String lang) {
        message.getChannel().sendMessage(LanguageHandler.get(lang, "giveaway_wrongargument")).queue();
    }

    public static long zonedDateTimeDifference(ZonedDateTime zonedDateTime1, ZonedDateTime zonedDateTime2) {
        return ChronoUnit.MILLIS.between(zonedDateTime1, zonedDateTime2);
    }

    static void startGiveaway(CommandEvent event, String[] args, String lang) {
        var server = new Server(event.getGuild());
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

        var now = ZonedDateTime.now(ZoneOffset.of(new Server(message.getGuild()).getOffset()));
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
        eb.setColor(new Master(message.getAuthor()).getColor());
        eb.setAuthor(String.format(LanguageHandler.get(lang, "giveaway_from"), message.getAuthor().getName()), null, message.getAuthor().getEffectiveAvatarUrl());
        eb.setDescription(String.format(LanguageHandler.get(lang, "giveaway_description_running"), prize, amountWinners, remainingTimeString, EmoteUtil.getEmoji("tada")));
        eb.appendDescription("\n\n" + String.format(LanguageHandler.get(lang, "giveaway_end_manually"), author.getAsMention()));
        eb.setFooter(LanguageHandler.get(lang, "giveaway_endsat"), ImageUtil.getImageUrl("clock", guild, author));
        eb.setTimestamp(dateGiveaway);

        var finalPrize1 = prize;
        var finalAmountWinners = amountWinners;
        message.getTextChannel().sendMessage(eb.build()).queue((messageNew -> {
            message.delete().queue();
            server.insertGiveawayToDb(messageNew.getChannel().getIdLong(),
                    messageNew.getIdLong(), message.getAuthor().getIdLong(), finalPrize1, Timestamp.valueOf(dateGiveaway.toLocalDateTime()), finalAmountWinners);
            var emote = EmoteUtil.getEmoji("tada");
            var end = EmoteUtil.getEmoji("end");
            if (emote != null) messageNew.addReaction(emote).queue();
            if (end != null) messageNew.addReaction(end).queue();
        }));
    }
}
