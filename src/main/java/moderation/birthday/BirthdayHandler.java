// Author: Tancred423 (https://github.com/Tancred423)
package moderation.birthday;

import files.language.LanguageHandler;
import moderation.guild.Server;
import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import servant.LoggingTask;
import utilities.ImageUtil;
import utilities.StringUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BirthdayHandler {
    public static void updateLists(JDA jda) {
        List<Guild> guilds = jda.getGuilds();
        for (var guild : guilds) {
            if (guild.getIdLong() == 264445053596991498L) continue; // Discord Bot List
            var server = new Server(guild);
            if (server.birthdayMessagesHasEntry()) {
                var channelId = server.getBirthdayMessageChannelId();
                var messageId = server.getBirthdayMessageMessageId();
                var authorId = server.getBirthdayMessageAuthorId();
                var tc = guild.getTextChannelById(channelId);
                if (tc == null) return;
                var authorMember = guild.getMemberById(authorId);
                if (authorMember == null) return;
                tc.retrieveMessageById(messageId).queue(message -> {
                    try {
                        var list = createList(guild, authorMember.getUser(), tc, true);
                        if (list == null) server.unsetBirthdayMessage();
                        else message.editMessage(list).queue();
                    } catch (ParseException e) {
                        new LoggingTask(e, jda, "BirthdayHandler#updateLists");
                    }
                });
            }
        }
    }

    static void sendList(boolean isAutoUpdate, CommandEvent event) throws ParseException {
        var channel = event.getChannel();
        var guild = event.getGuild();
        var server = new Server(guild);
        var user = event.getAuthor();
        var embed = createList(guild, user, channel, isAutoUpdate);
        if (embed != null) {
            channel.sendMessage(embed).queue(sentMessage -> {
                if (isAutoUpdate)
                    server.setBirthdayMessage(sentMessage.getChannel().getIdLong(), sentMessage.getIdLong(),
                            event.getAuthor().getIdLong());
            });
        }
    }

    public static void checkBirthdays(JDA jda) {
        List<Guild> guilds = jda.getGuilds();
        for (var guild : guilds) {
            if (guild.getIdLong() == 264445053596991498L) continue; // Discord Bot List
            var internalGuild = new Server(guild);

            var now = OffsetDateTime.now(ZoneOffset.of(internalGuild.getOffset()));
            var nowString = now.toString().substring(4, 10);

            Map<Long, String> birthdays = internalGuild.getBirthdays();

            for (Map.Entry<Long, String> birthday : birthdays.entrySet()) {
                var userId = birthday.getKey();
                if (nowString.equals(birthday.getValue().substring(4))) {
                    if (!internalGuild.wasGratulated(userId)) {
                        gratulate(guild, userId);
                        internalGuild.setGratulated(userId);
                    }
                } else internalGuild.unsetGratulated(userId);
            }
        }
    }

    private static void gratulate(Guild guild, long userId) {
        var guildOwner = guild.getOwner();
        if (guildOwner == null) return;
        var server = new Server(guild);
        var birthdayChannel = guild.getTextChannelById(server.getBirthdayChannelId());
        var birthdayMember = guild.getMemberById(userId);
        if (birthdayChannel != null && birthdayMember != null)
        birthdayChannel.sendMessage(
                String.format(LanguageHandler.get(server.getLanguage(), "birthday_gratulation"), birthdayMember.getAsMention())
        ).queue();
    }
    
    private static MessageEmbed createList(Guild guild, net.dv8tion.jda.api.entities.User author, MessageChannel channel, boolean isAutoUpdate) throws ParseException {
        var server = new Server(guild);
        var master = new Master(author);

        var lang = server.getLanguage();
        var countdown = StringUtil.fillWithWhitespace(LanguageHandler.get(lang, "birthday_countdown"),
                String.format(LanguageHandler.get(lang, "birthday_countdown_value"), 999).length());
        var date = StringUtil.fillWithWhitespace(LanguageHandler.get(lang, "birthday_date"), 10);
        var name = LanguageHandler.get(lang, "birthday_name");

        // birthDates: <Long UserId, String BirthDate>
        var birthDates = server.getBirthdays();

        if (birthDates.isEmpty()) {
            channel.sendMessage(LanguageHandler.get(lang, "birthday_missing")).queue();
            return null;
        }

        // birthCountdowns: <Long UserId, Long Countdown>
        var birthCountdowns = new HashMap<Long, Long>();
        for (var entry : birthDates.entrySet()) birthCountdowns.put(entry.getKey(), getCountdown(entry.getValue()));
        // birthCountdownsSorted: Sorted by value (countdown)
        var birthCountdownsSorted = birthCountdowns.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        var sb = new StringBuilder();
        // Title Row
        sb.append("```c\n")
                .append(countdown).append(" ").append(date).append(" ").append(name).append("\n")
                .append("-".repeat(countdown.length())).append(" ").append("-".repeat(date.length())).append(" ").append("-".repeat(16)).append("\n");

        var fieldValues = new ArrayList<String>();

        // Birthdays
        for (Map.Entry<Long, Long> entry : birthCountdownsSorted.entrySet()) {
            var birthdayMember = guild.getMemberById(entry.getKey());
            if (birthdayMember != null) {
                var birthdayUser = birthdayMember.getUser();

                /* Max length for a field is 1024. 57 is the max length of one row. 3 is the length of ```.
                 * In case this list is getting too long, we will make a new one.
                 */
                if (sb.length() >= 1024 - 57 - 3) {
                    fieldValues.add(sb.append("```").toString());
                    sb = new StringBuilder().append("```c\n");
                }

                // in %s days
                sb.append(String.format(LanguageHandler.get(lang, "birthday_countdown_value"), StringUtil.pushWithWhitespace(String.valueOf(entry.getValue()), 3)))
                    .append(" ")
                    // date
                    .append(birthDates.get(entry.getKey()))
                    .append(" ")
                    // name
                    .append(birthdayUser.getName())
                    .append("\n");
            }
        }

        fieldValues.add(sb.append("```").toString());

        var eb = new EmbedBuilder()
                .setColor(master.getColor())
                .setAuthor(String.format(LanguageHandler.get(lang, "birthday_guild"), guild.getName() + (guild.getName().toLowerCase().endsWith("s") ? "'" : "'s")), null, guild.getIconUrl())
                .setDescription(LanguageHandler.get(lang, "birthday_howtoadd")).setFooter(LanguageHandler.get(lang, "birthday_as_of"), isAutoUpdate ? ImageUtil.getImageUrl(guild.getJDA(), "clock") : null)
                .setTimestamp(OffsetDateTime.now());

        var i = 0;
        for (var fieldValue : fieldValues) {
            if (i == 24) break; // Max 25 fields
            eb.addField(" ", fieldValue, false);
            i++;
        }

        return eb.build();
    }

    private static long getCountdown(String date) throws ParseException {
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);

        int todayDay = calendar.get(Calendar.DAY_OF_MONTH);
        int todayMonth = calendar.get(Calendar.MONTH)+1;
        int todayYear = calendar.get(Calendar.YEAR);

        int bdayDay = Integer.parseInt(date.substring(8,10));
        int bdayMonth = Integer.parseInt(date.substring(5,7));
        int bdayYear;

        /* If the date (day and month) is before today, the year will be set to the next year. */
        if (bdayMonth < todayMonth || (bdayMonth == todayMonth && bdayDay < todayDay)) bdayYear = todayYear + 1;
        else bdayYear = todayYear;

        String bdayDateString = bdayYear + date.substring(4);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date bdayDate = simpleDateFormat.parse(bdayDateString);
        String todayDateString = simpleDateFormat.format(today);
        Date todayDate = simpleDateFormat.parse(todayDateString);

        return TimeUnit.MILLISECONDS.toDays(bdayDate.getTime() - todayDate.getTime());
    }
}
