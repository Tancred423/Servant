// Author: Tancred423 (https://github.com/Tancred423)
package useful.alarm;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.api.Permission;
import owner.blacklist.Blacklist;
import servant.Servant;
import useful.InvalidTitleException;
import utilities.Constants;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;

public class AlarmCommand extends Command {
    public AlarmCommand() {
        this.name = "alarm";
        this.aliases = new String[0];
        this.help = "Set up an alarm.";
        this.category = new Category("Useful");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
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
        CompletableFuture.runAsync(() -> {
            try {
                if (!Toggle.isEnabled(event, name)) return;
                if (Blacklist.isBlacklisted(event.getAuthor(), event.getGuild())) return;

                var lang = LanguageHandler.getLanguage(event);
                var p = GuildHandler.getPrefix(event);

                if (event.getArgs().isEmpty()) {
                    var description = String.format(LanguageHandler.get(lang, "alarm_description"), p);
                    var usage = String.format(LanguageHandler.get(lang, "alarm_usage"), p, name, p, name, p, name);
                    var hint = String.format(LanguageHandler.get(lang, "alarm_hint"), p, name);
                    event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
                    return;
                }

                var contentSplit = event.getArgs().trim().replaceAll(" +", " ").replace("\\", "").split(" ");

                String title;
                try {
                    title = parseTitle(event, contentSplit);
                } catch (InvalidTitleException e) {
                    event.replyError(e.getMessage());
                    return;
                }

                ZonedDateTime alarmDate;
                try {
                    alarmDate = parseTime(event, contentSplit, title);
                } catch (InvalidTimeException e) {
                    event.replyError(e.getMessage());
                    return;
                }

                var guild = event.getGuild();
                var author = event.getAuthor();
                var internalAuthor = new User(author.getIdLong());

                var timestamp = Timestamp.valueOf(alarmDate.toLocalDateTime());
                if (internalAuthor.setAlarm(timestamp, title == null ? "" : title, guild, author)) event.reactSuccess();
                else {
                    event.replyError(LanguageHandler.get(lang, "alarm_alreadyset"));
                    event.reactError();
                }

                // Statistics.
                new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
                if (event.getGuild() != null)
                    new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Servant.threadPool);
    }

    private String parseTitle(CommandEvent event, String[] contentSplit) throws InvalidTitleException {
        var lang = LanguageHandler.getLanguage(event);
        if (contentSplit[0].startsWith("\"")) {
            if (contentSplit[0].endsWith("\"") && !contentSplit[0].equals("\"")) {
                // One Word Title
                var title = contentSplit[0].replace("\"", "");
                if (title.length() > 256) throw new InvalidTitleException(LanguageHandler.get(lang, "alarm_titlelength"));
                else return title;
            } else {
                // Multiple Word title
                var title = new StringBuilder();
                var counter = 0;
                for (var content : contentSplit) {
                    title.append(content).append(" ");
                    if (content.endsWith("\"") && counter != 0) return title.toString().replace("\"", "").trim();
                    counter++;
                }
            }
        } else return null;

        throw new InvalidTitleException(LanguageHandler.get(lang, "alarm_invalidtitle"));
    }

    private ZonedDateTime parseTime(CommandEvent event, String[] contentSplit, String title) throws InvalidTimeException {
        var lang = LanguageHandler.getLanguage(event);
        String days;
        String hours;
        String minutes;

        var alarm = ZonedDateTime.now(ZoneOffset.of(new User(event.getAuthor().getIdLong()).getOffset(event.getGuild(), event.getAuthor())));
        if (title == null) {
            // No title
            for (String arg : contentSplit) {
                try {
                    if (arg.toLowerCase().endsWith("d")) {
                        days = arg.replaceAll("d", "");
                        if (days.matches("[0-9]+")) alarm = alarm.plusDays(Integer.parseInt(days));
                        else {
                            event.reactError();
                            throw new InvalidTimeException(LanguageHandler.get(lang, "alarm_invalidtime"));
                        }
                    } else if (arg.toLowerCase().endsWith("h")) {
                        hours = arg.replaceAll("h", "");
                        if (hours.matches("[0-9]+")) alarm = alarm.plusHours(Integer.parseInt(hours));
                        else {
                            event.reactError();
                            throw new InvalidTimeException(LanguageHandler.get(lang, "alarm_invalidtime"));
                        }
                    } else if (arg.toLowerCase().endsWith("m")) {
                        minutes = arg.replaceAll("m", "");
                        if (minutes.matches("[0-9]+")) alarm = alarm.plusMinutes(Integer.parseInt(minutes));
                        else {
                            event.reactError();
                            throw new InvalidTimeException(LanguageHandler.get(lang, "alarm_invalidtime"));
                        }
                    } else {
                        event.reactError();
                        throw new InvalidTimeException(LanguageHandler.get(lang, "alarm_invalidtime"));
                    }
                } catch (NumberFormatException e) {
                    throw new InvalidTimeException(LanguageHandler.get(lang, "alarm_toobig"));
                }
            }

            return alarm;
        } else {
            // Has a title
            if (contentSplit.length > 1) {
                var counter = 0;
                for (int i = 0; i < contentSplit.length; i++) {
                    if (contentSplit[i].endsWith("\"") && !(contentSplit[i].equals("\"") && counter == 0)) {
                        for (int j = i + 1; j < contentSplit.length; j++) {
                            var arg = contentSplit[j];
                            if (arg.toLowerCase().endsWith("d")) {
                                days = arg.replaceAll("d", "");
                                if (days.matches("[0-9]+")) alarm = alarm.plusDays(Integer.parseInt(days));
                                else {
                                    event.reactError();
                                    throw new InvalidTimeException(LanguageHandler.get(lang, "alarm_invalidtime"));
                                }
                            } else if (arg.toLowerCase().endsWith("h")) {
                                hours = arg.replaceAll("h", "");
                                if (hours.matches("[0-9]+")) alarm = alarm.plusHours(Integer.parseInt(hours));
                                else {
                                    event.reactError();
                                    throw new InvalidTimeException(LanguageHandler.get(lang, "alarm_invalidtime"));
                                }
                            } else if (arg.toLowerCase().endsWith("m")) {
                                minutes = arg.replaceAll("m", "");
                                if (minutes.matches("[0-9]+")) alarm = alarm.plusMinutes(Integer.parseInt(minutes));
                                else {
                                    event.reactError();
                                    throw new InvalidTimeException(LanguageHandler.get(lang, "alarm_invalidtime"));
                                }
                            } else {
                                event.reactError();
                                throw new InvalidTimeException(LanguageHandler.get(lang, "alarm_invalidtime"));
                            }
                        }

                        return alarm;
                    }
                    counter++;
                }
            } else throw new InvalidTimeException(LanguageHandler.get(lang, "alarm_missingtime"));
        }

        throw new InvalidTimeException(LanguageHandler.get(lang, "alarm_invalidtime"));
    }
}
