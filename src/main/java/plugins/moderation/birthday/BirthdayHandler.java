// Author: Tancred423 (https://github.com/Tancred423)
package plugins.moderation.birthday;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.sharding.ShardManager;
import servant.LoggingTask;
import servant.MyGuild;
import servant.MyUser;
import servant.Servant;
import utilities.Console;
import utilities.Constants;
import utilities.ImageUtil;
import utilities.StringUtil;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BirthdayHandler {
    public static void updateLists(ShardManager sm, JDA jda) {
        var guilds = sm.getGuilds();
        for (var guild : guilds) {
            if (guild.getIdLong() == 264445053596991498L) continue; // Discord Bot List
            var myGuild = new MyGuild(guild);
            var channelId = myGuild.getBirthdayListTcId();
            var messageId = myGuild.getBirthdayListMsgId();
            var authorId = myGuild.getBirthdayListAuthorId();
            var tc = guild.getTextChannelById(channelId);
            if (tc != null && messageId != 0L) {
                guild.retrieveMemberById(authorId).queue(authorMember -> {
                    tc.retrieveMessageById(messageId).queue(message -> {
                        try {
                            updateList(guild, authorMember, message);
                        } catch (ParseException e) {
                            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "BirthdayHandler#updateLists"));
                        }
                    }, f -> {
                    });
                });
            }
        }
    }

    public static void checkBirthdays(ShardManager sm) {
        var guilds = sm.getGuilds();
        for (var guild : guilds) {
            if (guild.getIdLong() == 264445053596991498L) continue; // Discord Bot List
            var myGuild = new MyGuild(guild);

            var now = OffsetDateTime.now(myGuild.getTimezone().toZoneId());
            var nowString = now.toString().substring(4, 10);
            var yday = now.minusDays(1);
            var ydayString = yday.toString().substring(4, 10);

            var birthdays = myGuild.getBirthdays();

            for (var birthday : birthdays.entrySet()) {
                var userId = birthday.getKey();
                guild.retrieveMemberById(userId).queue(member -> {
                    var user = member.getUser();
                    var myUser = new MyUser(user);

                    if (nowString.equals(birthday.getValue().substring(4))) {
                        if (!myUser.wasGratulated(guild.getIdLong())) {
                            myUser.setGratulated(guild.getIdLong());
                            gratulate(guild, userId);
                            grantOrRevokeBirthdayRoles(guild, userId, true);
                        }
                    } else if (ydayString.equals(birthday.getValue().substring(4))) {
                        myUser.unsetGratulated(guild.getIdLong());
                        grantOrRevokeBirthdayRoles(guild, userId, false);
                    }
                }, f -> {
                });
            }
        }
    }

    private static void gratulate(Guild guild, long userId) {
        var myGuild = new MyGuild(guild);
        guild.retrieveMemberById(userId).queue(birthdayMember -> {
            var birthdayTc = myGuild.getBirthdayAnnouncementTc();
            if (birthdayTc != null && birthdayMember != null) {
                birthdayTc.sendMessage(
                        String.format(LanguageHandler.get(myGuild.getLanguageCode(), "birthday_gratulation"), birthdayMember.getAsMention())
                ).queue();
            }
        });
    }

    private static void grantOrRevokeBirthdayRoles(Guild guild, long userId, boolean grant) {
        var myGuild = new MyGuild(guild);
        var birthdayRoles = myGuild.getBirthdayRoles();
        guild.retrieveMemberById(userId).queue(birthdayMember -> {
            if (birthdayMember != null) {
                if (grant)
                    guild.modifyMemberRoles(birthdayMember, birthdayRoles, null).queue();
                else if (birthdayMember.getRoles().contains(birthdayRoles.get(0)))
                    guild.modifyMemberRoles(birthdayMember, null, birthdayRoles).queue();
            }
        });
    }

    private static void updateList(Guild guild, Member authorMember, Message message) throws ParseException {
        var myGuild = new MyGuild(guild);

        var lang = myGuild.getLanguageCode();
        var countdown = StringUtil.fillWithWhitespace(LanguageHandler.get(lang, "birthday_countdown"),
                String.format(LanguageHandler.get(lang, "birthday_countdown_value"), 999).length());
        var date = StringUtil.fillWithWhitespace(LanguageHandler.get(lang, "birthday_date"), 10);
        var name = LanguageHandler.get(lang, "birthday_name");

        // birthDates: <Long UserId, String BirthDate>
        var birthDates = myGuild.getBirthdays();
        var fieldValues = new ArrayList<String>();

        if (birthDates.isEmpty()) {
            fieldValues.add(LanguageHandler.get(lang, "birthday_missing"));
        } else {
            // birthCountdowns: <Long UserId, Long Countdown>
            var birthCountdowns = new HashMap<Long, Long>();
            for (var entry : birthDates.entrySet()) birthCountdowns.put(entry.getKey(), getCountdown(entry.getValue()));
            // birthCountdownsSorted: Sorted by value (countdown)
            var birthCountdownsSorted = birthCountdowns.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

            guild.retrieveMembersByIds(birthCountdowns.keySet())
                    .onSuccess(members -> {
                        var memberMap = new HashMap<Long, Member>();
                        for (var member : members) memberMap.put(member.getIdLong(), member);

                        var sb = new StringBuilder();
                        // Title Row
                        sb.append("```c\n")
                                .append(countdown).append(" ").append(date).append(" ").append(name).append("\n")
                                .append("-".repeat(countdown.length())).append(" ").append("-".repeat(date.length())).append(" ").append("-".repeat(16)).append("\n");

                        // Birthdays

                        for (var entry : birthCountdownsSorted.entrySet()) {
                            var birthdayMember = memberMap.get(entry.getKey());
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
                                sb.append(String.format(entry.getValue() == 1 ? LanguageHandler.get(lang, "birthday_countdown_value_sin") : LanguageHandler.get(lang, "birthday_countdown_value"), StringUtil.pushWithWhitespace(String.valueOf(entry.getValue()), 3)))
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

                        var authorName = String.format(LanguageHandler.get(lang, "birthday_guild"), guild.getName() + (guild.getName().toLowerCase().endsWith("s") ? "'" : "'s"));
                        var description = String.format(LanguageHandler.get(lang, "birthday_howtoadd"), Constants.WEBSITE_DASHBOARD);
                        var footer = LanguageHandler.get(lang, "birthday_as_of");

                        var eb = new EmbedBuilder()
                                .setColor(Color.decode(authorMember == null ? Servant.config.getDefaultColorCode() : new MyUser(authorMember.getUser()).getColorCode()))
                                .setAuthor(authorName, null, guild.getIconUrl())
                                .setDescription(description)
                                .setTimestamp(OffsetDateTime.now())
                                .setFooter(footer, ImageUtil.getUrl(guild.getJDA(), "clock"));

                        var currentEmbedLength = authorName.length() + description.length() + footer.length();

                        var i = 0;
                        for (var fieldValue : fieldValues) {
                            if (i == 24 || currentEmbedLength + fieldValue.length() > 6000)
                                break; // Max 25 fields or 6000 characters
                            eb.addField(" ", fieldValue, false);
                            i++;
                        }

                        message.editMessage(eb.build()).queue(s -> {
                        }, f -> myGuild.unsetBirthdayList());
                    })
                    .onError(error -> {
                        Console.log(error.getMessage());
                    });
        }
    }

    private static long getCountdown(String date) throws ParseException {
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);

        int todayDay = calendar.get(Calendar.DAY_OF_MONTH);
        int todayMonth = calendar.get(Calendar.MONTH) + 1;
        int todayYear = calendar.get(Calendar.YEAR);

        int bdayDay = Integer.parseInt(date.substring(8, 10));
        int bdayMonth = Integer.parseInt(date.substring(5, 7));
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
