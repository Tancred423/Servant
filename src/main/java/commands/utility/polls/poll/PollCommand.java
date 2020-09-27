// Author: Tancred423 (https://github.com/Tancred423)
package commands.utility.polls.poll;

import commands.utility.polls.Poll;
import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import servant.MyGuild;
import servant.MyUser;
import servant.Servant;
import utilities.*;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;
import zJdaUtilsLib.com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import java.awt.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PollCommand extends Command {
    private final EventWaiter waiter;
    private String accept;
    private String decline;

    public PollCommand(EventWaiter waiter) {
        this.name = "poll";
        this.aliases = new String[] { "vote", "voting" };
        this.help = "Host a poll";
        this.category = new Category("Utility");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.modCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION
        };

        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent event) {
        this.accept = event.getClient().getSuccess();
        this.decline = event.getClient().getError();

        var lang = LanguageHandler.getLanguage(event);
        var p = new MyGuild(event.getGuild()).getPrefix();

        var author = event.getAuthor();

        if (event.getArgs().isEmpty()) {
            var description = LanguageHandler.get(lang, "poll_description");
            var usage = String.format(LanguageHandler.get(lang, "poll_usage"), p, name, p, name);
            var hint = LanguageHandler.get(lang, "poll_hint");
            event.reply(MessageUtil.createUsageEmbed(name, event.getAuthor(), lang, description, aliases, usage, hint));
            return;
        }

        String topic;
        try {
            topic = Parser.parseText(lang, event.getArgs(), 0);
        } catch (ParseException e) {
            event.replyWarning(e.getMessage());
            return;
        }

        if (topic == null) {
            event.replyWarning(String.format(LanguageHandler.get(lang, "poll_questions_answers"), p));
            return;
        }

        var topicSplit = topic.split("/");

        if (topicSplit.length < 2 || topicSplit.length > 11) {
            event.reply(LanguageHandler.get(lang, "poll_amount"));
            event.reactWarning();
            return;
        }

        var question = topicSplit[0];
        List<String> answers = new ArrayList<>(Arrays.asList(topicSplit).subList(1, topicSplit.length));

        HashMap<Character, Integer> timeArgs;
        try {
            timeArgs = Parser.parseArguments(lang, event.getArgs());
        } catch (ParseException e) {
            event.replyWarning(e.getMessage());
            return;
        }

        var days = 0;
        var hours = 0;
        var minutes = 0;
        for (var timeArg : timeArgs.entrySet()) {
            if (timeArg.getKey().equals('d')) days += timeArg.getValue();
            else if (timeArg.getKey().equals('h')) hours += timeArg.getValue();
            else if (timeArg.getKey().equals('m')) minutes += timeArg.getValue();
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
            event.replyWarning(LanguageHandler.get(lang, "poll_30_days"));
            return;
        }


        event.getChannel().sendMessage(LanguageHandler.get(lang, "poll_multiplechoice_q")).queue(message -> {
            message.addReaction(accept).queue();
            message.addReaction(decline).queue();

            waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                    e -> e.getUser().equals(author)
                            && (e.getReactionEmote().getName().equals(accept)
                            || e.getReactionEmote().getName().equals(decline)),
                    e -> {
                        processVote(event, question, answers, lang, e.getReactionEmote().getName().equals(accept), eventTime);

                        message.delete().queue(s -> {}, f -> {});
                    }, 15, TimeUnit.MINUTES, () -> timeout(message, event, lang));
        });
    }

    private void processVote(CommandEvent event, String question, List<String> answers, String lang, boolean allowsMultipleAnswers, Instant eventTime) {
        var jda = event.getJDA();
        var author = event.getAuthor();
        var eb = new EmbedBuilder();
        eb.setColor(Color.decode(new MyUser(author).getColorCode()));
        eb.setAuthor(String.format(LanguageHandler.get(lang, "poll_started"), author.getName()), null, author.getEffectiveAvatarUrl());
        eb.setDescription(String.format(LanguageHandler.get(lang, "poll_end_manually"), event.getAuthor().getAsMention()) +
                "\n\n**" + String.format(LanguageHandler.get(lang, "poll_multiple"), allowsMultipleAnswers ? LanguageHandler.get(lang, "poll_allowed") : LanguageHandler.get(lang, "poll_forbidden")) + "**"
        );
        eb.setFooter(LanguageHandler.get(lang, "poll_ends_at"), ImageUtil.getUrl(jda, "clock"));

        eb.setTimestamp(eventTime);

        var sb = new StringBuilder();
        var emoji = Poll.getPollEmojis(jda);

        for (int i = 0; i < answers.size(); i++)
            sb.append("\n").append(emoji.get(i)).append(" ").append(answers.get(i));

        eb.addField(question, sb.toString(), false);

        event.getChannel().sendMessage(eb.build()).queue(message -> {
            var poll = new Poll(event.getJDA(), lang, message.getGuild().getIdLong(), message.getTextChannel().getIdLong(), message.getIdLong());
            if (allowsMultipleAnswers) poll.set(author.getIdLong(), "check", Timestamp.from(eventTime), answers.size());
            else poll.set(author.getIdLong(), "radio", Timestamp.from(eventTime), answers.size());
            for (int i = 0; i < answers.size(); i++) message.addReaction(emoji.get(i)).queue();

            var end = EmoteUtil.getEmoji(jda, "end");
            if (end != null) message.addReaction(end).queue();

            /* End poll to given time.
             * In case the bot restarts, this service will break.
             * Therefore we will run any current giveaway in the database
             * from listeners.ReadyListener#startExecutorNew.
             */
            var delayInMillis = eventTime.toEpochMilli() - Instant.now().toEpochMilli();
            Servant.scheduledService.schedule(
                    new PollEndTask(poll),
                    delayInMillis,
                    TimeUnit.MILLISECONDS
            );
        });
    }

    private void timeout(Message botMessage, CommandEvent event, String lang) {
        event.reactWarning();
        botMessage.delete().queue();
        event.reply(LanguageHandler.get(lang, "poll_timeout"));
    }
}
