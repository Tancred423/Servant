// Author: Tancred423 (https://github.com/Tancred423)
package useful.polls;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import utilities.Constants;
import utilities.EmoteUtil;
import utilities.MessageUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;
import zJdaUtilsLib.com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PollCommand extends Command {
    private final EventWaiter waiter;
    private String accept = "✅";
    private String decline = "❌";

    public PollCommand(EventWaiter waiter) {
        this.name = "poll";
        this.aliases = new String[] { "vote" };
        this.help = "Host a voting.";
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
                Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION
        };

        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent event) {
        var lang = LanguageHandler.getLanguage(event);
        var p = GuildHandler.getPrefix(event);

        var author = event.getAuthor();

        if (event.getArgs().isEmpty()) {
            var description = LanguageHandler.get(lang, "vote_description");
            var usage = String.format(LanguageHandler.get(lang, "vote_usage"), p, name, p, name);
            var hint = LanguageHandler.get(lang, "vote_hint");
            event.reply(MessageUtil.createUsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint));
            return;
        }

        var splitArgs = event.getArgs().split("/");

        if (splitArgs.length < 2 || splitArgs.length > 11) {
            event.reply(LanguageHandler.get(lang, "vote_amount"));
            event.reactWarning();
            return;
        }

        var question = splitArgs[0];
        List<String> answers = new ArrayList<>(Arrays.asList(splitArgs).subList(1, splitArgs.length));


        event.getChannel().sendMessage("Allow multiple answers?").queue(message -> {
            message.addReaction(accept).queue();
            message.addReaction(decline).queue();

            waiter.waitForEvent(GuildMessageReactionAddEvent.class,
                    e -> e.getUser().equals(author)
                            && (e.getReactionEmote().getName().equals(accept)
                            || e.getReactionEmote().getName().equals(decline)),
                    e -> {
                        if (e.getReactionEmote().getName().equals(accept))
                            processVote(event, question, answers, lang, true);
                        else processVote(event, question, answers, lang, false);

                        message.delete().queue(s -> {}, f -> {});
                        event.getMessage().delete().queue(s -> {}, f -> {});
                    }, 15, TimeUnit.MINUTES, () -> timeout(message, event, lang));
        });
    }

    private void processVote(CommandEvent event, String question, List<String> answers, String lang, boolean allowsMultipleAnswers) {
        var author = event.getAuthor();
        var eb = new EmbedBuilder();
        eb.setColor(new Master(author).getColor());
        eb.setAuthor(String.format(LanguageHandler.get(lang, "vote_started"), author.getName()), null, author.getEffectiveAvatarUrl());
        eb.setDescription(String.format(LanguageHandler.get(lang, "votes_end_manually"), event.getAuthor().getAsMention()) +
                "\n\n**" + String.format(LanguageHandler.get(lang, "vote_multiple"), allowsMultipleAnswers ? LanguageHandler.get(lang, "vote_allowed") : LanguageHandler.get(lang, "vote_forbidden")) + "**"
        );
        eb.setFooter(LanguageHandler.get(lang, "votes_active"), event.getSelfUser().getAvatarUrl());

        var dateIn7DaysOtd = OffsetDateTime.now(ZoneOffset.UTC).plusDays(7).toLocalDateTime();
        var dateIn7Days = Timestamp.valueOf(dateIn7DaysOtd);
        eb.setTimestamp(dateIn7DaysOtd);

        var sb = new StringBuilder();
        String[] emoji = EmoteUtil.getVoteEmotes(event.getJDA());
        for (int i = 0; i < answers.size(); i++) sb.append("\n").append(emoji[i]).append(" ").append(answers.get(i));

        eb.addField(question, sb.toString(), false);

        event.getChannel().sendMessage(eb.build()).queue(message -> {
            var poll = new Poll(event.getJDA(), lang, message);
            if (allowsMultipleAnswers) poll.set(message.getChannel().getIdLong(), message.getIdLong(), author.getIdLong(), "vote", dateIn7Days);
            else poll.set(message.getChannel().getIdLong(), message.getIdLong(), author.getIdLong(), "radio", dateIn7Days);
            for (int i = 0; i < answers.size(); i++) message.addReaction(emoji[i]).queue();

            var end = EmoteUtil.getEmoji("end");
            if (end != null) message.addReaction(end).queue();
        });
    }

    private void timeout(Message botMessage, CommandEvent event, String lang) {
        event.reactWarning();
        botMessage.delete().queue();
        event.reply(LanguageHandler.get(lang, "vote_timeout"));
    }
}
