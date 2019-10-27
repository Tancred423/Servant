// Author: Tancred423 (https://github.com/Tancred423)
package useful.votes.vote;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import owner.blacklist.Blacklist;
import servant.Log;
import servant.Servant;
import useful.votes.VotesDatabase;
import utilities.Constants;
import utilities.Emote;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;
import zJdaUtilsLib.com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class VoteCommand extends Command {
    private final EventWaiter waiter;
    private String accept = "✅";
    private String decline = "❌";

    public VoteCommand(EventWaiter waiter) {
        this.name = "vote";
        this.aliases = new String[]{"v"};
        this.help = "Host a voting.";
        this.category = new Category("Useful");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION};

        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent event) {
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, name)) return;
            if (Blacklist.isBlacklisted(event.getAuthor(), event.getGuild())) return;

            var lang = LanguageHandler.getLanguage(event, name);
            var p = GuildHandler.getPrefix(event, name);

            var author = event.getAuthor();

            if (event.getArgs().isEmpty()) {
                try {
                    var description = LanguageHandler.get(lang, "vote_description");
                    var usage = String.format(LanguageHandler.get(lang, "vote_usage"), p, name, p, name);
                    var hint = LanguageHandler.get(lang, "vote_hint");
                    event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                }
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
                            if (e.getReactionEmote().getName().equals(accept)) processVote(event, question, answers, lang, true);
                            else processVote(event, question, answers, lang, false);

                            message.delete().queue();
                            event.getMessage().delete().queue();
                        }, 15, TimeUnit.MINUTES, () -> timeout(message, event, lang));
            });

            // Statistics.
            try {
                new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
                new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
            }
        });
    }

    private void processVote(CommandEvent event, String question, List<String> answers, String lang, boolean allowsMultipleAnswers) {
        try {
            var author = event.getAuthor();
            var eb = new EmbedBuilder();
            eb.setColor(new User(author.getIdLong()).getColor());
            eb.setAuthor(String.format(LanguageHandler.get(lang, "vote_started"), author.getName()), null, author.getEffectiveAvatarUrl());
            eb.setTitle(question);
            eb.setFooter(LanguageHandler.get(lang, "votes_active"), event.getSelfUser().getAvatarUrl());
            try {
                eb.setTimestamp(OffsetDateTime.now(ZoneId.of(new Guild(event.getGuild().getIdLong()).getOffset())));
            } catch (SQLException | DateTimeException e) {
                eb.setTimestamp(OffsetDateTime.now(ZoneId.of(Servant.config.getDefaultOffset())).getOffset());
            }

            String[] emoji = Emote.getVoteEmotes();
            for (int i = 0; i < answers.size(); i++) eb.appendDescription("\n" + emoji[i] + " " + answers.get(i));

            event.getChannel().sendMessage(eb.build()).queue(message -> {
                try {
                    if (allowsMultipleAnswers) VotesDatabase.setVote(message.getIdLong(), author.getIdLong(), "vote");
                    else  VotesDatabase.setVote(message.getIdLong(), author.getIdLong(), "radio");

                    for (int i = 0; i < answers.size(); i++) message.addReaction(emoji[i]).queue();
                    net.dv8tion.jda.core.entities.Emote end;

                    end = Emote.getEmote("end");
                    if (end == null) message.addReaction(Emote.getEmoji("end")).queue();
                    else message.addReaction(end).queue();
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                }
            });
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
        }
    }

    private void timeout(Message botMessage, CommandEvent event, String lang) {
        event.reactWarning();
        botMessage.delete().queue();
        event.reply(LanguageHandler.get(lang, "vote_timeout"));
    }
}
