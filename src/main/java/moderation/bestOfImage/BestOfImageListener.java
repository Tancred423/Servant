// Author: Tancred423 (https://github.com/Tancred423)
package moderation.bestOfImage;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import owner.blacklist.Blacklist;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BestOfImageListener extends ListenerAdapter {
    private static List<Long> temporaryBlacklist = new ArrayList<>();

    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        if (event.getGuild().getIdLong() == 264445053596991498L) return; // Discord Bot List
        CompletableFuture.runAsync(() -> {
            if (event.getUser().isBot()) return;

            if (!Toggle.isEnabled(event, "bestofimage")) return;
            if (Blacklist.isBlacklisted(event.getUser(), event.getGuild())) return;

            var guild = event.getGuild();
            var user = event.getUser();
            var messageId = event.getMessageIdLong();

            if (temporaryBlacklist.contains(messageId)) return;
            temporaryBlacklist.add(messageId);

            var internalGuild = new Guild(event.getGuild().getIdLong());
            if (internalGuild.bestOfImageIsBlacklisted(messageId, guild, user)) {
                temporaryBlacklist.remove(messageId);
                return;
            }

            event.getChannel().retrieveMessageById(messageId).queue(message -> {
                var attachments = message.getAttachments();
                if (attachments.isEmpty()) {
                    temporaryBlacklist.remove(messageId);
                    return;
                }
                for (var attachment : attachments) if (!attachment.isImage()) {
                    temporaryBlacklist.remove(messageId);
                    return;
                }

                var voteEmote = internalGuild.getBestOfImageEmote(guild, user);
                var voteEmoji = internalGuild.getBestOfImageEmoji(guild, user);
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

                var number = internalGuild.getBestOfImageNumber(guild, user);
                var percentage = internalGuild.getBestOfImagePercentage(guild, user);

                var onlineMemberCount = 0;
                var members = event.getGuild().getMembers();
                for (var member : members) if (member.getOnlineStatus() == OnlineStatus.ONLINE) onlineMemberCount++;
                var onlineMemberPercentage = (int) Math.ceil(onlineMemberCount * percentage / 100.0);

                var lang = new Guild(event.getGuild().getIdLong()).getLanguage(guild, user);

                if (number != 0 && percentage != 0) {
                    if (reactionCount >= number && reactionCount >= onlineMemberPercentage)
                        sendBestOf(event, reactionCount, attachments, lang);
                    else temporaryBlacklist.remove(messageId);
                } else if (number != 0) {
                    if (reactionCount >= number)
                        sendBestOf(event, reactionCount, attachments, lang);
                    else temporaryBlacklist.remove(messageId);
                } else if (percentage != 0) {
                    if (reactionCount >= onlineMemberPercentage)
                        sendBestOf(event, reactionCount, attachments, lang);
                    else temporaryBlacklist.remove(messageId);
                } else temporaryBlacklist.remove(messageId);
            }, failure -> {});

            final var executor = new ScheduledThreadPoolExecutor(1);
            executor.schedule(() -> {
                temporaryBlacklist.remove(messageId);
            }, 10, TimeUnit.SECONDS);
        });
    }

    private static void sendBestOf(GuildMessageReactionAddEvent event, int reactionCount, List<Message.Attachment> attachmentList, String lang) {
        var user = event.getUser();
        var guild = event.getGuild();
        var internalGuild = new Guild(guild.getIdLong());
        event.getChannel().retrieveMessageById(event.getMessageId()).queue(message -> {
            var channel = internalGuild.getBestOfImageChannel(guild, user);
            var author = message.getAuthor();

            for (var attachment : attachmentList) {
                var eb = new EmbedBuilder();
                eb.setColor(new User(author.getIdLong()).getColor(guild, user));
                eb.setAuthor(author.getName() + "#" + author.getDiscriminator(), null, author.getEffectiveAvatarUrl());
                eb.setTitle(!message.getContentDisplay().isEmpty() ? message.getContentDisplay() : null);
                eb.setDescription("[" + LanguageHandler.get(lang, "bestof_jump") + "](" + message.getJumpUrl() + ")");
                eb.setImage(attachment.getUrl());
                eb.setFooter(String.format(LanguageHandler.get(lang, "bestof_footer"), reactionCount, event.getChannel().getName()), null);
                eb.setTimestamp(message.getTimeCreated());

                channel.sendMessage(eb.build()).queue();

                // Spam prevention. Every message just once.
                internalGuild.addBestOfImageBlacklist(event.getMessageIdLong(), guild, user);
            }
        });
    }
}
