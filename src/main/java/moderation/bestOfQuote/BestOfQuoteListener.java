// Author: Tancred423 (https://github.com/Tancred423)
package moderation.bestOfQuote;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import owner.blacklist.Blacklist;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BestOfQuoteListener extends ListenerAdapter {
    private static List<Long> temporaryBlacklist = new ArrayList<>();

    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        CompletableFuture.runAsync(() -> {
            if (event.getUser().isBot()) return;
            if (!Toggle.isEnabled(event, "bestofquote")) return;
            if (Blacklist.isBlacklisted(event.getUser(), event.getGuild())) return;

            var guild = event.getGuild();
            var user = event.getUser();
            var messageId = event.getMessageIdLong();

            if (temporaryBlacklist.contains(messageId)) return;
            temporaryBlacklist.add(messageId);

            var internalGuild = new Guild(event.getGuild().getIdLong());
            if (internalGuild.bestOfQuoteIsBlacklisted(messageId, guild, user)) {
                temporaryBlacklist.remove(messageId);
                return;
            }

            event.getChannel().retrieveMessageById(messageId).queue(message -> {
                var attachments = message.getAttachments();
                if (!attachments.isEmpty()) {
                    temporaryBlacklist.remove(messageId);
                    return;
                }

                var voteEmote = internalGuild.getBestOfQuoteEmote(event.getJDA(), guild, user);
                var voteEmoji = internalGuild.getBestOfQuoteEmoji(guild, user);
                var reactionCount = 0;

                var reactionEmote = event.getReactionEmote();
                if (voteEmote != null) {
                    // Emote
                    // todo: never null?
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

                var number = internalGuild.getBestOfQuoteNumber(guild, user);
                var percentage = internalGuild.getBestOfQuotePercentage(guild, user);

                var onlineMemberCount = 0;
                var members = event.getGuild().getMembers();
                for (var member : members) if (member.getOnlineStatus() == OnlineStatus.ONLINE) onlineMemberCount++;
                var onlineMemberPercentage = (int) Math.ceil(onlineMemberCount * percentage / 100.0);
                var lang = new Guild(event.getGuild().getIdLong()).getLanguage(guild, user);

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
            }, failure -> {});

            final var executor = new ScheduledThreadPoolExecutor(1);
            executor.schedule(() -> {
                temporaryBlacklist.remove(messageId);
            }, 10, TimeUnit.SECONDS);
        });
    }

    private static void sendBestOf(GuildMessageReactionAddEvent event, int reactionCount, String lang) {
        var user = event.getUser();
        var guild = event.getGuild();
        var internalGuild = new Guild(guild.getIdLong());
        event.getChannel().retrieveMessageById(event.getMessageId()).queue(message -> {
            var channel = internalGuild.getBestOfQuoteChannel(guild, user);
            var author = message.getAuthor();

            var eb = new EmbedBuilder();
            eb.setColor(new User(author.getIdLong()).getColor(guild, user));
            eb.setAuthor(author.getName() + "#" + author.getDiscriminator(), null, author.getEffectiveAvatarUrl());
            eb.setTitle(!message.getContentDisplay().isEmpty() ? message.getContentDisplay() : null);
            eb.setDescription("[" + LanguageHandler.get(lang, "bestof_jump") + "](" + message.getJumpUrl() + ")");
            eb.setFooter(String.format(LanguageHandler.get(lang, "bestof_footer"), reactionCount, event.getChannel().getName()), null);
            eb.setTimestamp(message.getTimeCreated());

            channel.sendMessage(eb.build()).queue();

            // Spam prevention. Every message just once.
            internalGuild.addBestOfQuoteBlacklist(event.getMessageIdLong(), guild, user);
        });
    }
}
