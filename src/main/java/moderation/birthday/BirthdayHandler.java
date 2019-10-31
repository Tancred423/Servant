// Author: Tancred423 (https://github.com/Tancred423)
package moderation.birthday;

import files.language.LanguageHandler;
import moderation.user.User;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import servant.Log;
import utilities.Image;
import utilities.StringFormat;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;

class BirthdayHandler {
    static void updateLists(JDA jda) {
        List<Guild> guilds = jda.getGuilds();
        for (var guild : guilds) {
            var internalGuild = new moderation.guild.Guild(guild.getIdLong());
            if (internalGuild.birthdayMessagesHasEntry(guild, guild.getOwner().getUser())) {
                var guildOwner = guild.getOwner().getUser();
                var channelId = internalGuild.getBirthdayMessageChannelId(guild, guildOwner);
                var messageId = internalGuild.getBirthdayMessageMessageId(guild, guildOwner);
                var authorId = internalGuild.getBirthdayMessageAuthorId(guild, guildOwner);
                guild.getTextChannelById(channelId).getMessageById(messageId).queue(message -> {
                    try {
                        message.editMessage(createList(guild, guild.getMemberById(authorId).getUser(), guild.getTextChannelById(channelId))).queue();
                    } catch (ParseException e) {
                        new Log(e, guild, guild.getMemberById(authorId).getUser(), "BirthdayHandler - Update Lists", null).sendLog(false);
                    }
                });
            }
        }
    }

    static void sendList(boolean isAutoUpdate, CommandEvent event) throws ParseException {
        var channel = event.getChannel();
        var guild = event.getGuild();
        var internalGuild = new moderation.guild.Guild(guild.getIdLong());
        var author = event.getAuthor();
        var embed = createList(guild, author, channel);
        if (embed != null) {
            channel.sendMessage(embed).queue(sentMessage -> {
                if (isAutoUpdate)
                    internalGuild.setBirthdayMessage(sentMessage.getChannel().getIdLong(), sentMessage.getIdLong(),
                            event.getAuthor().getIdLong(), event.getGuild(), event.getAuthor());
            });
        }
    }

    static void checkBirthdays(JDA jda) {
        List<Guild> guilds = jda.getGuilds();
        for (var guild : guilds) {
            var guildOwner = guild.getOwner().getUser();
            var internalGuild = new moderation.guild.Guild(guild.getIdLong());

            var now = OffsetDateTime.now(ZoneOffset.of(internalGuild.getOffset(guild, guildOwner)));
            var nowString = now.toString().substring(4, 10);

            Map<Long, String> birthdays = internalGuild.getBirthdays(guild, guildOwner);

            for (Map.Entry<Long, String> birthday : birthdays.entrySet()) {
                var userId = birthday.getKey();
                if (nowString.equals(birthday.getValue().substring(4))) {
                    if (!internalGuild.wasGratulated(userId, guild, guildOwner)) {
                        gratulate(guild, userId);
                        internalGuild.setGratulated(userId, guild, guildOwner);
                    }
                } else internalGuild.unsetGratulated(userId, guild, guildOwner);
            }
        }
    }

    private static void gratulate(Guild guild, long userId) {
        var guildOwner = guild.getOwner().getUser();
        var internalGuild = new moderation.guild.Guild(guild.getIdLong());
        guild.getTextChannelById(internalGuild.getBirthdayChannelId(guild, guildOwner)).sendMessage(
                String.format(LanguageHandler.get(internalGuild.getLanguage(guild, guildOwner), "birthday_gratulation"), guild.getMemberById(userId).getAsMention())
        ).queue();
    }
    
    private static MessageEmbed createList(Guild guild, net.dv8tion.jda.core.entities.User author, MessageChannel channel) throws ParseException {
        var internalGuild = new moderation.guild.Guild(guild.getIdLong());
        var internalAuthor = new User(author.getIdLong());

        Map<Long, String> birthdays = internalGuild.getBirthdays(guild, author);
        var sb = new StringBuilder();
        var lang = internalGuild.getLanguage(guild, author);
        var countdown = StringFormat.fillWithWhitespace(LanguageHandler.get(lang, "birthday_countdown"),
                String.format(LanguageHandler.get(lang, "birthday_countdown_value"), 999).length());
        var date = StringFormat.fillWithWhitespace(LanguageHandler.get(lang, "birthday_date"), 10);
        var name = LanguageHandler.get(lang, "birthday_name");

        Map<Long, String> birthdays2 = new HashMap<>();
        for (Map.Entry<Long, String> entry : birthdays.entrySet()) {
            if (guild.getMemberById(entry.getKey()) == null) internalGuild.unsetBirthday(entry.getKey(), guild, author);
            else birthdays2.put(getCooldown(entry.getValue()), entry.getValue() + " " + guild.getMemberById(entry.getKey()).getUser().getName());
        }

        Map<Long, String> birthdaysSorted = new TreeMap<>(birthdays2);

        sb.append("```c\n")
                .append(countdown).append(" ").append(date).append(" ").append(name).append("\n")
                .append("-".repeat(countdown.length())).append(" ").append("-".repeat(date.length())).append(" ").append("-".repeat(16)).append("\n");
        for (Map.Entry<Long, String> entry : birthdaysSorted.entrySet()) {
            sb.append(String.format(LanguageHandler.get(lang, "birthday_countdown_value"), StringFormat.pushWithWhitespace(String.valueOf(entry.getKey()), 3)))
                    .append(" ")
                    .append(entry.getValue()).append("\n");

        }
        sb.append("```");

        if (birthdays.isEmpty()) {
            channel.sendMessage(LanguageHandler.get(lang, "birthday_missing")).queue();
            return null;
        } else {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(internalAuthor.getColor(guild, author));
            eb.setAuthor(String.format(LanguageHandler.get(lang, "birthday_guild"), guild.getName() + (guild.getName().toLowerCase().endsWith("s") ? "'" : "'s")), null, guild.getIconUrl());
            eb.setDescription(sb.toString());
            eb.setFooter(LanguageHandler.get(lang, "birthday_as_of"), Image.getImageUrl("clock", guild, author));
            eb.setTimestamp(OffsetDateTime.now());

            return eb.build();
        }
    }

    private static long getCooldown(String date) throws ParseException {
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
