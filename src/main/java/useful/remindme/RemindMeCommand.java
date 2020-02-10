// Author: Tancred423 (https://github.com/Tancred423)
package useful.remindme;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.guild.Server;
import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import servant.Servant;
import useful.InvalidTopicException;
import utilities.Constants;
import utilities.EmoteUtil;
import utilities.ImageUtil;
import utilities.MessageUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.concurrent.TimeUnit;

public class RemindMeCommand extends Command {
    public RemindMeCommand() {
        this.name = "remindme";
        this.aliases = new String[] {"reminder", "alarm"};
        this.help = "Set up a reminder.";
        this.category = new Category("Useful");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var guild = event.getGuild();
        var server = new Server(guild);
        var user = event.getAuthor();
        var master = new Master(user);

        var lang = LanguageHandler.getLanguage(event);
        var p = GuildHandler.getPrefix(event);

        if (event.getArgs().isEmpty()) {
            var description = LanguageHandler.get(lang, "remindme_description");
            var usage = String.format(LanguageHandler.get(lang, "remindme_usage"), p, name, p, name, p, name, p, name, p, name);
            var hint = String.format(LanguageHandler.get(lang, "remindme_hint"), p, name, p);
            event.reply(MessageUtil.createUsageEmbed(name, user, description, ownerCommand, userPermissions, aliases, usage, hint));
            return;
        }

        var contentSplit = event.getArgs().trim().replaceAll(" +", " ").replace("\\", "").split(" ");

        try {
            var topic = parseTopic(event, contentSplit);
            var eventTime = parseTime(event, contentSplit, topic);

            var upvote = EmoteUtil.getEmoji("upvote");
            if (upvote == null) return;

            event.getChannel().sendMessage(new EmbedBuilder()
                    .setColor(master.getColor())
                    .setAuthor(String.format(LanguageHandler.get(lang, "remindme_of"), user.getName()), null, user.getEffectiveAvatarUrl())
                    .setDescription((topic == null ? "" : String.format(LanguageHandler.get(lang, "remindme_topic"), topic)) + "\n" +
                           String.format(LanguageHandler.get(lang, "remindme_also"), upvote))
                    .setFooter(LanguageHandler.get(lang, "remindme_at"), ImageUtil.getImageUrl(event.getJDA(), "clock"))
                    .setTimestamp(OffsetDateTime.ofInstant(eventTime.toInstant(), ZoneId.ofOffset("", ZoneOffset.of(master.getOffset()))))
                    .build()).queue(message -> {
                message.addReaction(upvote).queue();

                var aiNumber = server.setRemindMe(event.getChannel().getIdLong(), message.getIdLong(), user.getIdLong(), eventTime, topic == null ? "" : topic);
                var remindMe = server.getRemindMe(aiNumber);

                /* Execute new RemindMe to given time.
                 * In case the bot restarts, this service will break.
                 * Therefore we will run any current RemindMe in the database
                 * from listeners.ReadyListener#startExecutorNew.
                 */
                var delayInMillis = eventTime.toInstant().toEpochMilli() - System.currentTimeMillis();
                Servant.remindMeService.schedule(
                        new RemindMeSenderTask(event.getJDA(), remindMe, lang),
                        delayInMillis,
                        TimeUnit.MILLISECONDS
                );
            });
        } catch (InvalidTopicException | InvalidTimeException e) {
            event.replyError(e.getMessage());
        }
    }

    private String parseTopic(CommandEvent event, String[] contentSplit) throws InvalidTopicException {
        var lang = LanguageHandler.getLanguage(event);
        if (contentSplit[0].startsWith("\"")) {
            if (contentSplit[0].endsWith("\"") && !contentSplit[0].equals("\"")) {
                // One Word topic
                var topic = contentSplit[0].replace("\"", "");
                if (topic.length() > 1000) throw new InvalidTopicException(LanguageHandler.get(lang, "remindme_topiclength"));
                else return topic;
            } else {
                // Multiple Word topic
                var topic = new StringBuilder();
                var counter = 0;
                for (var content : contentSplit) {
                    topic.append(content).append(" ");
                    if (content.endsWith("\"") && counter != 0) return topic.toString().replace("\"", "").trim();
                    counter++;
                }
            }
        } else return null;

        throw new InvalidTopicException(LanguageHandler.get(lang, "remindme_invalidtopic"));
    }

    private Timestamp parseTime(CommandEvent event, String[] contentSplit, String topic) throws InvalidTimeException {
        var lang = LanguageHandler.getLanguage(event);
        String days;
        String hours;
        String minutes;
        var master = new Master(event.getAuthor());

        var remindMe = ZonedDateTime.now(ZoneOffset.of(new Master(event.getAuthor()).getOffset()));
        if (topic == null) {
            // No topic
            var isTimeArguments = false;
            var firstArg = contentSplit[0];
            if (firstArg.endsWith("d") || firstArg.endsWith("h") || firstArg.endsWith("m")) isTimeArguments = true;

            if (isTimeArguments) {
                // Time Arguments
                for (String arg : contentSplit) {
                    try {
                        if (arg.toLowerCase().endsWith("d")) {
                            days = arg.replaceAll("d", "");
                            if (days.matches("[0-9]+")) remindMe = remindMe.plusDays(Integer.parseInt(days));
                            else {
                                event.reactError();
                                throw new InvalidTimeException(LanguageHandler.get(lang, "remindme_invalidtime"));
                            }
                        } else if (arg.toLowerCase().endsWith("h")) {
                            hours = arg.replaceAll("h", "");
                            if (hours.matches("[0-9]+")) remindMe = remindMe.plusHours(Integer.parseInt(hours));
                            else {
                                event.reactError();
                                throw new InvalidTimeException(LanguageHandler.get(lang, "remindme_invalidtime"));
                            }
                        } else if (arg.toLowerCase().endsWith("m")) {
                            minutes = arg.replaceAll("m", "");
                            if (minutes.matches("[0-9]+")) remindMe = remindMe.plusMinutes(Integer.parseInt(minutes));
                            else {
                                event.reactError();
                                throw new InvalidTimeException(LanguageHandler.get(lang, "remindme_invalidtime"));
                            }
                        } else {
                            event.reactError();
                            throw new InvalidTimeException(LanguageHandler.get(lang, "remindme_invalidtime"));
                        }
                    } catch (NumberFormatException e) {
                        throw new InvalidTimeException(LanguageHandler.get(lang, "remindme_toobig"));
                    }
                }
            } else {
                // Date and Time
                if (contentSplit.length > 1) {
                    // firstArg already exists
                    var secondArg = contentSplit[1];
                    var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");
                    try {
                        remindMe = ZonedDateTime.parse(firstArg + " " + secondArg + " " + master.getOffset(), formatter);
                    } catch (DateTimeParseException e) {
                        event.reactError();
                        throw new InvalidTimeException(LanguageHandler.get(lang, "remindme_invalidtime"));
                    }
                } else {
                    event.reactError();
                    throw new InvalidTimeException(LanguageHandler.get(lang, "remindme_missingtime"));
                }
            }

            // RemindMe can only be placed within the next month.
            if (remindMe.toInstant().toEpochMilli() > OffsetDateTime.now(ZoneOffset.UTC).plusMonths(1).toInstant().toEpochMilli()) {
                event.reactWarning();
                throw new InvalidTimeException(LanguageHandler.get(lang, "remindme_month"));
            } else return Timestamp.from(remindMe.toInstant());
        } else {
            // Has a topic
            if (contentSplit.length > 1) {
                var counter = 0;
                for (int i = 0; i < contentSplit.length; i++) {
                    if (contentSplit[i].endsWith("\"") && !(contentSplit[i].equals("\"") && counter == 0)) {
                        var isTimeArguments = false;

                        if (i+1 == contentSplit.length)
                            throw new InvalidTimeException(LanguageHandler.get(lang, "remindme_missingtime"));

                        var firstArg = contentSplit[i + 1];
                        if (firstArg.endsWith("d") || firstArg.endsWith("h") || firstArg.endsWith("m")) isTimeArguments = true;

                        if (isTimeArguments) {
                            // Time Arguments
                            for (int j = i + 1; j < contentSplit.length; j++) {
                                try {
                                    var arg = contentSplit[j];
                                    if (arg.toLowerCase().endsWith("d")) {
                                        days = arg.replaceAll("d", "");
                                        if (days.matches("[0-9]+"))
                                            remindMe = remindMe.plusDays(Integer.parseInt(days));
                                        else {
                                            event.reactError();
                                            throw new InvalidTimeException(LanguageHandler.get(lang, "remindme_invalidtime"));
                                        }
                                    } else if (arg.toLowerCase().endsWith("h")) {
                                        hours = arg.replaceAll("h", "");
                                        if (hours.matches("[0-9]+"))
                                            remindMe = remindMe.plusHours(Integer.parseInt(hours));
                                        else {
                                            event.reactError();
                                            throw new InvalidTimeException(LanguageHandler.get(lang, "remindme_invalidtime"));
                                        }
                                    } else if (arg.toLowerCase().endsWith("m")) {
                                        minutes = arg.replaceAll("m", "");
                                        if (minutes.matches("[0-9]+"))
                                            remindMe = remindMe.plusMinutes(Integer.parseInt(minutes));
                                        else {
                                            event.reactError();
                                            throw new InvalidTimeException(LanguageHandler.get(lang, "remindme_invalidtime"));
                                        }
                                    } else {
                                        event.reactError();
                                        throw new InvalidTimeException(LanguageHandler.get(lang, "remindme_invalidtime"));
                                    }
                                } catch (NumberFormatException e) {
                                    throw new InvalidTimeException(LanguageHandler.get(lang, "remindme_toobig"));
                                }
                            }
                        } else {
                            // Date and Time
                            // firstArg already exists
                            if (contentSplit.length > i + 2) {
                                var secondArg = contentSplit[i + 2];
                                var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");
                                try {
                                    remindMe = ZonedDateTime.parse(firstArg + " " + secondArg + " " + master.getOffset(), formatter);

                                    if (!remindMe.isAfter(ZonedDateTime.now(ZoneId.of(master.getOffset())))) {
                                        event.reactError();
                                        throw new InvalidTimeException(LanguageHandler.get(lang, "remindme_past"));
                                    }
                                } catch (DateTimeParseException e) {
                                    event.reactError();
                                    throw new InvalidTimeException(LanguageHandler.get(lang, "remindme_invalidtime"));
                                }
                            } else {
                                event.reactError();
                                throw new InvalidTimeException(LanguageHandler.get(lang, "remindme_missingtime"));
                            }
                        }

                        // RemindMe can only be placed within the next month.
                        if (remindMe.toInstant().toEpochMilli() > OffsetDateTime.now(ZoneOffset.UTC).plusMonths(1).toInstant().toEpochMilli()) {
                            event.reactWarning();
                            throw new InvalidTimeException(LanguageHandler.get(lang, "remindme_month"));
                        } else return Timestamp.from(remindMe.toInstant());
                    }
                    counter++;
                }
            } else throw new InvalidTimeException(LanguageHandler.get(lang, "remindme_missingtime"));
        }

        throw new InvalidTimeException(LanguageHandler.get(lang, "remindme_invalidtime"));
    }
}
