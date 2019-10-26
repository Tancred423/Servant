// Author: Tancred423 (https://github.com/Tancred423)
package moderation.bestOfQuote;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import owner.blacklist.Blacklist;
import servant.Log;
import servant.Servant;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BestOfQuoteListener extends ListenerAdapter {
    private static List<Long> temporaryBlacklist = new ArrayList<>();

    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if (event.getUser().isBot()) return;
        if (!Toggle.isEnabled(event, "bestOfQuote")) return;
//        if (Blacklist.isBlacklisted(event.getUser(), event.getGuild())) return;

        var messageId = event.getMessageIdLong();

        if (temporaryBlacklist.contains(messageId)) return;
        temporaryBlacklist.add(messageId);

        var internalGuild = new Guild(event.getGuild().getIdLong());
        try {
            if (internalGuild.bestOfQuoteIsBlacklisted(messageId)) {
                temporaryBlacklist.remove(messageId);
                return;
            }
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getUser(), "bestOfQuote", null).sendLog(false);
            temporaryBlacklist.remove(messageId);
            return;
        }

        event.getChannel().getMessageById(messageId).queue(message -> {
            var attachments = message.getAttachments();
            if (!attachments.isEmpty()) {
                temporaryBlacklist.remove(messageId);
                return;
            }

            Emote voteEmote;
            String voteEmoji;
            try {
                voteEmote = internalGuild.getBestOfQuoteEmote();
                voteEmoji = internalGuild.getBestOfQuoteEmoji();
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getUser(), "bestOfQuote", null).sendLog(false);
                temporaryBlacklist.remove(messageId);
                return;
            }

            var reactionCount = 0;

            var reactionEmote = event.getReactionEmote();
            if (voteEmote != null) {
                // Emote
                if (reactionEmote.getEmote() == null) return;
                if (!reactionEmote.getEmote().equals(voteEmote)) {
                    temporaryBlacklist.remove(messageId);
                    return;
                }
                var messageReactions = message.getReactions();

                for (var mr : messageReactions)
                    if (mr.getReactionEmote().getEmote().equals(voteEmote)) {
                        reactionCount = mr.getCount();
                        break;
                    }
                if (reactionCount == 0) {
                    temporaryBlacklist.remove(messageId);
                    return;
                }
            } else if (voteEmoji != null) {
                // Emoji
                if (!reactionEmote.getName().equals(voteEmoji)) {
                    temporaryBlacklist.remove(messageId);
                    return;
                }
                var messageReactions = message.getReactions();

                for (var mr : messageReactions)
                    if (mr.getReactionEmote().getName().equals(voteEmoji)) {
                        reactionCount = mr.getCount();
                        break;
                    }
                if (reactionCount == 0) {
                    temporaryBlacklist.remove(messageId);
                    return;
                }
            } else {
                temporaryBlacklist.remove(messageId);
                return;
            }

            int number;
            int percentage;
            try {
                number = internalGuild.getBestOfQuoteNumber();
                percentage = internalGuild.getBestOfQuotePercentage();
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getUser(), "bestOfQuote", null).sendLog(false);
                temporaryBlacklist.remove(messageId);
                return;
            }

            var onlineMemberCount = 0;
            var members = event.getGuild().getMembers();
            for (var member : members) if (member.getOnlineStatus() == OnlineStatus.ONLINE) onlineMemberCount++;
            var onlineMemberPercentage = (int) Math.ceil(onlineMemberCount * percentage / 100.0);

            String lang;
            try {
                lang = new Guild(event.getGuild().getIdLong()).getLanguage();
            } catch (SQLException e) {
                lang = Servant.config.getDefaultLanguage();
            }

            if (number != 0 && percentage != 0) {
                if (reactionCount >= number && reactionCount >= onlineMemberPercentage)
                    sendBestOf(event, reactionCount, lang);
                else temporaryBlacklist.remove(messageId);
            } else if (number != 0) {
                if (reactionCount >= number)
                    sendBestOf(event, reactionCount, lang);
                else temporaryBlacklist.remove(messageId);
            } else if (percentage != 0) {
                if (reactionCount >= onlineMemberPercentage)
                    sendBestOf(event, reactionCount, lang);
                else temporaryBlacklist.remove(messageId);
            } else temporaryBlacklist.remove(messageId);
        });

        final var executor = new ScheduledThreadPoolExecutor(1);
        executor.schedule(() -> {
            temporaryBlacklist.remove(messageId);
        }, 10, TimeUnit.SECONDS);
    }

    private static void sendBestOf(GuildMessageReactionAddEvent event, int reactionCount, String lang) {
        var internalGuild = new Guild(event.getGuild().getIdLong());
        event.getChannel().getMessageById(event.getMessageId()).queue(message -> {
            try {
                var channel = internalGuild.getBestOfQuoteChannel();
                var author = message.getAuthor();

                var eb = new EmbedBuilder();
                try {
                    eb.setColor(new User(author.getIdLong()).getColor());
                } catch (SQLException e) {
                    eb.setColor(Color.decode(Servant.config.getDefaultColorCode()));
                }
                eb.setAuthor(author.getName() + "#" + author.getDiscriminator(), null, author.getEffectiveAvatarUrl());
                eb.setTitle(!message.getContentDisplay().isEmpty() ? message.getContentDisplay() : null);
                eb.setDescription("[" + LanguageHandler.get(lang, "bestof_jump") + "](" + message.getJumpUrl() + ")");
                eb.setFooter(String.format(LanguageHandler.get(lang, "bestof_footer"), reactionCount, event.getChannel().getName()), null);
                eb.setTimestamp(message.getCreationTime());

                channel.sendMessage(eb.build()).queue();

                // Spam prevention. Every message just once.
                internalGuild.addBestOfQuoteBlacklist(event.getMessageIdLong());
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getUser(), "bestOfQuote", null).sendLog(false);
            }
        });
    }
}
