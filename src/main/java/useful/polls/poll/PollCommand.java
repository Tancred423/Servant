// Author: Tancred423 (https://github.com/Tancred423)
package useful.polls.poll;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import owner.blacklist.Blacklist;
import servant.Servant;
import useful.polls.PollsDatabase;
import utilities.Constants;
import utilities.Emote;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;
import zJdaUtilsLib.com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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
        CompletableFuture.runAsync(() -> {
            try {
                if (!Toggle.isEnabled(event, name)) return;
                if (Blacklist.isBlacklisted(event.getAuthor(), event.getGuild())) return;

                var lang = LanguageHandler.getLanguage(event);
                var p = GuildHandler.getPrefix(event);

                var guild = event.getGuild();
                var user = event.getAuthor();

                var author = event.getAuthor();

                if (event.getArgs().isEmpty()) {
                    var description = LanguageHandler.get(lang, "vote_description");
                    var usage = String.format(LanguageHandler.get(lang, "vote_usage"), p, name, p, name);
                    var hint = LanguageHandler.get(lang, "vote_hint");
                    event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
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

                                message.delete().queue();
                                event.getMessage().delete().queue();
                            }, 15, TimeUnit.MINUTES, () -> timeout(message, event, lang));
                });

                // Statistics.
                new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, user);
                new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Servant.threadPool);
    }

    private void processVote(CommandEvent event, String question, List<String> answers, String lang, boolean allowsMultipleAnswers) {
        var guild = event.getGuild();
        var user = event.getAuthor();
        var author = event.getAuthor();
        var eb = new EmbedBuilder();
        eb.setColor(new User(author.getIdLong()).getColor(guild, user));
        eb.setAuthor(String.format(LanguageHandler.get(lang, "vote_started"), author.getName()), null, author.getEffectiveAvatarUrl());
        eb.setDescription(allowsMultipleAnswers ? LanguageHandler.get(lang, "vote_multiple") : LanguageHandler.get(lang, "vote_single"));
        eb.setFooter(LanguageHandler.get(lang, "votes_active"), event.getSelfUser().getAvatarUrl());

        var dateIn7DaysOtd = OffsetDateTime.now(ZoneOffset.UTC).plusDays(7).toLocalDateTime();
        var dateIn7Days = Timestamp.valueOf(dateIn7DaysOtd);
        eb.setTimestamp(dateIn7DaysOtd);

        var sb = new StringBuilder();
        String[] emoji = Emote.getVoteEmotes(guild, user);
        for (int i = 0; i < answers.size(); i++) sb.append("\n").append(emoji[i]).append(" ").append(answers.get(i));

        eb.addField(question, sb.toString(), false);

        event.getChannel().sendMessage(eb.build()).queue(message -> {
            if (allowsMultipleAnswers) PollsDatabase.setVote(guild.getIdLong(), message.getChannel().getIdLong(), message.getIdLong(), author.getIdLong(), "vote", dateIn7Days, guild, user);
            else  PollsDatabase.setVote(guild.getIdLong(), message.getChannel().getIdLong(), message.getIdLong(), author.getIdLong(), "radio", dateIn7Days, guild, user);
            for (int i = 0; i < answers.size(); i++) message.addReaction(emoji[i]).queue();

            var end = Emote.getEmoji("end");
            if (end != null) message.addReaction(end).queue();
        });
    }

    private void timeout(Message botMessage, CommandEvent event, String lang) {
        event.reactWarning();
        botMessage.delete().queue();
        event.reply(LanguageHandler.get(lang, "vote_timeout"));
    }
}
