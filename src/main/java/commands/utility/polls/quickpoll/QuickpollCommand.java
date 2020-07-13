// Author: Tancred423 (https://github.com/Tancred423)
package commands.utility.polls.quickpoll;

import commands.utility.polls.Poll;
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
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class QuickpollCommand extends Command {
    public QuickpollCommand() {
        this.name = "quickpoll";
        this.aliases = new String[] { "quickvote" };
        this.help = "Smol poll with yes/no";
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
    }

    @Override
    protected void execute(CommandEvent event) {
        var jda = event.getJDA();
        var guild = event.getGuild();
        var myGuild = new MyGuild(guild);
        var user = event.getAuthor();
        var myUser = new MyUser(user);
        var message = event.getMessage();

        var lang = LanguageHandler.getLanguage(event);
        var p = myGuild.getPrefix();

        if (event.getArgs().isEmpty()) {
            var description = String.format(LanguageHandler.get(lang, "quickpoll_description"), EmoteUtil.getEmoji(jda, "upvote"), EmoteUtil.getEmoji(jda, "downvote"));
            var usage = String.format(LanguageHandler.get(lang, "quickpoll_usage"), p, name, p, name, p, name);
            var hint = String.format(LanguageHandler.get(lang, "quickpoll_hint"), p, name, p);
            event.reply(MessageUtil.createUsageEmbed(name, user, lang, description, aliases, usage, hint));
            return;
        }

        String topic;
        HashMap<Character, Integer> timeArgs;
        try {
            topic = Parser.parseText(lang, event.getArgs(), 0);
            timeArgs = Parser.parseArguments(lang, event.getArgs());
        } catch (ParseException e) {
            event.reply(e.getMessage());
            return;
        }

        var days = timeArgs.get('d');
        days = days == null ? 0 : days;
        var hours = timeArgs.get('h');
        hours = hours == null ? 0 : hours;
        var minutes = timeArgs.get('m');
        minutes = minutes == null ? 0 : minutes;

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

        var eb = new EmbedBuilder();
        eb.setColor(Color.decode(myUser.getColorCode()));
        eb.setAuthor(String.format(LanguageHandler.get(lang, "quickpoll_started"), user.getName()), null, user.getEffectiveAvatarUrl());
        if (topic != null) eb.setDescription("**" + topic + "**");
        eb.appendDescription("\n\n" + String.format(LanguageHandler.get(lang, "poll_end_manually"), user.getAsMention()));
        eb.setFooter(LanguageHandler.get(lang, "poll_ends_at"), ImageUtil.getUrl(jda, "clock"));
        eb.setTimestamp(eventTime);

        var upvote = EmoteUtil.getEmoji(jda, "upvote");
        var downvote = EmoteUtil.getEmoji(jda, "downvote");
        var end = EmoteUtil.getEmoji(jda, "end");

        if (upvote == null || downvote == null || end == null) return;

        message.getChannel().sendMessage(eb.build()).queue(sentMessage -> {
            sentMessage.addReaction(upvote).queue();
            sentMessage.addReaction(downvote).queue();
            sentMessage.addReaction(end).queue();
            var poll = new Poll(event.getJDA(), lang, guild.getIdLong(), message.getTextChannel().getIdLong(), sentMessage.getIdLong());
            poll.set(user.getIdLong(), "quick", Timestamp.from(eventTime), 0);

            /* End giveaway to given time.
             * In case the bot restarts, this service will break.
             * Therefore we will run any current giveaway in the database
             * from listeners.ReadyListener#startExecutorNew.
             */
            var delayInMillis = eventTime.toEpochMilli() - Instant.now().toEpochMilli();
            Servant.scheduledService.schedule(
                    new QuickpollEndTask(poll),
                    delayInMillis,
                    TimeUnit.MILLISECONDS
            );
        });
    }
}
