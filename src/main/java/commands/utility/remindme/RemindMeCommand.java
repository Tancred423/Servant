// Author: Tancred423 (https://github.com/Tancred423)
package commands.utility.remindme;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import servant.MyGuild;
import servant.MyUser;
import servant.Servant;
import utilities.*;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.awt.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class RemindMeCommand extends Command {
    public RemindMeCommand() {
        this.name = "remindme";
        this.aliases = new String[] { "reminder", "alarm" };
        this.help = "Set up a reminder";
        this.category = new Category("Utility");
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
        var jda = event.getJDA();
        var guild = event.getGuild();
        var myGuild = new MyGuild(guild);
        var user = event.getAuthor();
        var myUser = new MyUser(user);

        var lang = LanguageHandler.getLanguage(event);
        var p = myGuild.getPrefix();

        if (event.getArgs().isEmpty()) {
            var description = LanguageHandler.get(lang, "remindme_description");
            var usage = String.format(LanguageHandler.get(lang, "remindme_usage"), p, name, p, name, p, name, p, name, p, name);
            var hint = String.format(LanguageHandler.get(lang, "remindme_hint"), p, name, p);
            event.reply(MessageUtil.createUsageEmbed(name, user, lang, description, aliases, usage, hint));
            return;
        }

        try {
            var topic = Parser.parseText(lang, event.getArgs(), 1000);
            var timeArgs = Parser.parseArguments(lang, event.getArgs());

            var days = 0;
            var hours = 0;
            var minutes = 0;

            for (var timeArg : timeArgs.entrySet()) {
                if (timeArg.getKey().equals('d')) days = timeArg.getValue();
                else if (timeArg.getKey().equals('h')) hours = timeArg.getValue();
                else if (timeArg.getKey().equals('m')) minutes = timeArg.getValue();
            }

            if (days == 0 && hours == 0 && minutes == 0) {
                event.replyWarning(LanguageHandler.get(lang, "missing_time_args"));
                return;
            }

            var now = Instant.now();
            var eventTime = now.plusMillis(TimeUtil.daysToMillis(days))
                    .plusMillis(TimeUtil.hoursToMillis(hours))
                    .plusMillis(TimeUtil.minutesToMillis(minutes));
            var nowIn30Days = now.plusMillis(TimeUtil.daysToMillis(30));

            if (eventTime.toEpochMilli() > nowIn30Days.toEpochMilli()) {
                event.replyWarning(LanguageHandler.get(lang, "remindme_30_days"));
                return;
            }

            var upvote = EmoteUtil.getEmoji(jda, "upvote");
            if (upvote == null) return;

            event.getChannel().sendMessage(new EmbedBuilder()
                    .setColor(Color.decode(myUser.getColorCode()))
                    .setAuthor(String.format(LanguageHandler.get(lang, "remindme_of"), user.getName()), null, user.getEffectiveAvatarUrl())
                    .setDescription((topic == null ? "" : String.format(LanguageHandler.get(lang, "remindme_topic"), topic)) + "\n" +
                           String.format(LanguageHandler.get(lang, "remindme_also"), upvote))
                    .setFooter(LanguageHandler.get(lang, "remindme_at"), ImageUtil.getUrl(event.getJDA(), "clock"))
                    .setTimestamp(eventTime)
                    .build()).queue(message -> {
                message.addReaction(upvote).queue();

                var remindMe = new RemindMe(jda, guild.getIdLong(), event.getTextChannel().getIdLong(), message.getIdLong());
                remindMe.set(user.getIdLong(), Timestamp.from(eventTime), topic == null ? "" : topic);

                /* Execute new RemindMe to given time.
                 * In case the bot restarts, this service will break.
                 * Therefore we will run any current RemindMe in the database
                 * from listeners.ReadyListener#startExecutorNew.
                 */
                var delayInMillis = eventTime.toEpochMilli() - now.toEpochMilli();
                Servant.scheduledService.schedule(
                        new RemindMeSenderTask(remindMe),
                        delayInMillis,
                        TimeUnit.MILLISECONDS
                );
            });
        } catch (ParseException e) {
            event.replyWarning(e.getMessage());
        }
    }
}
