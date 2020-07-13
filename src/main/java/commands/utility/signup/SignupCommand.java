// Author: Tancred423 (https://github.com/Tancred423)
package commands.utility.signup;

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

public class SignupCommand extends Command {
    public SignupCommand() {
        this.name = "signup";
        this.aliases = new String[] { "event" };
        this.help = "Organise events";
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
                Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var lang = LanguageHandler.getLanguage(event);
        var p = new MyGuild(event.getGuild()).getPrefix();

        if (event.getArgs().isEmpty()) {
            var description = LanguageHandler.get(lang, "signup_description");
            var usage = String.format(LanguageHandler.get(lang, "signup_usage"), p, name, p, name, p, name, p, name, p, name);
            var hint = LanguageHandler.get(lang, "signup_hint");
            event.reply(MessageUtil.createUsageEmbed(name, event.getAuthor(), lang, description, aliases, usage, hint));
            return;
        }

        try {
            var title = Parser.parseText(lang, event.getArgs(), 200);
            var args = Parser.parseArguments(lang, event.getArgs());

            var days = 0;
            var hours = 0;
            var minutes = 0;
            var participants = 0;

            for (var arg : args.entrySet()) {
                if (arg.getKey().equals('d')) days = arg.getValue();
                else if (arg.getKey().equals('h')) hours = arg.getValue();
                else if (arg.getKey().equals('m')) minutes = arg.getValue();
                else if (arg.getKey().equals('p')) participants = arg.getValue();
            }

            if (days == 0 && hours == 0 && minutes == 0) {
                event.replyWarning(LanguageHandler.get(lang, "missing_time_args"));
                return;
            }

            if (participants == 0) {
                event.replyWarning(String.format(LanguageHandler.get(lang, "signup_missing_participants"), p));
                return;
            }

            var now = Instant.now();
            var eventTime = now.plusMillis(TimeUtil.daysToMillis(days))
                    .plusMillis(TimeUtil.hoursToMillis(hours))
                    .plusMillis(TimeUtil.minutesToMillis(minutes));
            var nowIn30Days = now.plusMillis(TimeUtil.daysToMillis(30));

            if (eventTime.toEpochMilli() > nowIn30Days.toEpochMilli()) {
                event.replyWarning(LanguageHandler.get(lang, "signup_30_days"));
                return;
            }

            var jda = event.getJDA();
            var guild = event.getGuild();
            var author = event.getAuthor();
            var myAuthor = new MyUser(author);

            var eb = new EmbedBuilder();
            eb.setColor(Color.decode(myAuthor.getColorCode()));
            eb.setTitle(title == null ? LanguageHandler.get(lang, "signup_embedtitle") :
                    String.format(LanguageHandler.get(lang, "signup_embedtitle_topic"), title));
            eb.setDescription(String.format(LanguageHandler.get(lang, "signup_embeddescription"),
                    EmoteUtil.getEmoji(jda, "upvote"), participants,
                    author.getAsMention()));
            eb.setFooter(LanguageHandler.get(lang, "signup_event"),
                    ImageUtil.getUrl(jda, "clock"));
            eb.setTimestamp(eventTime);

            var upvote = EmoteUtil.getEmoji(jda, "upvote");
            var downvote = EmoteUtil.getEmoji(jda, "end");
            if (upvote == null || downvote == null) return;

            var finalParticipants = participants;
            event.getChannel().sendMessage(eb.build()).queue(sentMessage -> {
                sentMessage.addReaction(upvote).queue();
                sentMessage.addReaction(downvote).queue();

                var signup = new Signup(jda, guild.getIdLong(), event.getTextChannel().getIdLong(), sentMessage.getIdLong());
                signup.set(author.getIdLong(), finalParticipants, title, Timestamp.from(eventTime));

                /* Execute new RemindMe to given time.
                 * In case the bot restarts, this service will break.
                 * Therefore we will run any current RemindMe in the database
                 * from listeners.ReadyListener#startExecutorNew.
                 */
                var delayInMillis = eventTime.toEpochMilli() - now.toEpochMilli();
                Servant.scheduledService.schedule(
                        new SignupSenderTask(signup),
                        delayInMillis,
                        TimeUnit.MILLISECONDS
                );
            });
        } catch (ParseException e) {
            event.replyWarning(e.getMessage());
        }
    }
}
