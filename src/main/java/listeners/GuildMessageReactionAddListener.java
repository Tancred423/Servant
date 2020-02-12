// Author: Tancred423 (https://github.com/Tancred423)
package listeners;

import files.language.LanguageHandler;
import moderation.bestOfQuote.BestOfQuote;
import moderation.guild.Server;
import moderation.reactionRole.ReactionRole;
import moderation.toggle.Toggle;
import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import owner.blacklist.Blacklist;
import servant.Servant;
import useful.giveaway.Giveaway;
import useful.giveaway.GiveawayHandler;
import useful.polls.Poll;
import useful.signup.Signup;
import utilities.EmoteUtil;
import utilities.TimeUtil;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GuildMessageReactionAddListener extends ListenerAdapter {
    // A temporary blacklist to prevent multiple reactions processes.
    private static List<Long> temporaryBlacklist = new ArrayList<>();

    // This event will be thrown if a user reacts on a message in a guild.
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        var guild = event.getGuild();
        var user = event.getUser();
        var channel = event.getChannel();
        var jda = event.getJDA();

        /* Certain conditions must meet, so this event is allowed to be executed:
         * 1.   Ignore any request from the Discord Bot List as this big guild
         *      invoke a lot of events, but never use this bot actively.
         * 2.   Ignore any request from bots to prevent infinite loops.
         * 3.   Ignore any request from blacklisted users and guilds.
         */
        if (guild.getIdLong() == 264445053596991498L) return; // Discord Bot List
        if (user.isBot()) return;
        if (Blacklist.isBlacklisted(guild, user)) return;

        CompletableFuture.runAsync(() -> {
            var server = new Server(guild);
            var messageId = event.getMessageIdLong();
            var lang = server.getLanguage();

            // Best Of Image
            if (Toggle.isEnabled(event, "bestofimage") && !temporaryBlacklist.contains(messageId)) {
                temporaryBlacklist.add(messageId);
                if (server.bestOfImageIsBlacklisted(messageId)) temporaryBlacklist.remove(messageId);
                else processBestOfImage(event, server, messageId);
            }

            // Best Of Quote
            if (Toggle.isEnabled(event, "bestofquote") && !temporaryBlacklist.contains(messageId)) {
                temporaryBlacklist.add(messageId);
                var bestOfQuote = new BestOfQuote(jda, guild.getIdLong());
                if (bestOfQuote.isBlacklisted(messageId)) temporaryBlacklist.remove(messageId);
                else processBestOfQuote(event, messageId);
            }

            // Giveaway
            if (Toggle.isEnabled(event, "giveaway")
                    && server.isGiveaway(channel.getIdLong(), messageId)
                    && (event.getReactionEmote().isEmoji() && event.getReactionEmote().getName().equals(EmoteUtil.getEmoji("end")))) {
                channel.retrieveMessageById(messageId).queue(message -> {
                    var giveaway = new Giveaway(jda, guild.getIdLong(), channel.getIdLong(), messageId);
                    if (giveaway.getHostId() == user.getIdLong())
                        GiveawayHandler.announceWinners(message, giveaway.getAmountWinners(), giveaway.getPrize(), lang, jda.getUserById(giveaway.getHostId()));
                });
            }

            channel.retrieveMessageById(messageId).queue(message -> {
                // Quickpoll
                var poll = new Poll(jda, lang, message);
                if (Toggle.isEnabled(event, "quickvote") && poll.isQuickPoll()) {
                    processQuickpollEnd(event, poll, user, messageId);
                    processQuickpollMultipleVote(event, poll, user, messageId);
                }

                // Poll
                if (Toggle.isEnabled(event, "vote") && (poll.isPoll() || poll.isRadioPoll())) {
                    processPollEnd(event, poll, user, messageId);
                }

                // Radiopoll
                if (Toggle.isEnabled(event, "radiovote") && poll.isRadioPoll()) {
                    processRadiopollMultipleVote(event, poll, user, messageId);
                }
            }, f -> {});

            // Reaction Role
            if (Toggle.isEnabled(event, "reactionrole")) processReactionRole(event, guild);

            // Signup
            var signup = new Signup(jda, messageId);
            if (Toggle.isEnabled(event, "signup") && signup.isSignup()) {
                processSignup(event, user, server, signup, messageId);
            }
        }, Servant.fixedThreadPool);
    }

    private void processBestOfImage(GuildMessageReactionAddEvent event, Server server, long messageId) {
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

            var voteEmote = server.getBestOfImageEmote();
            var voteEmoji = server.getBestOfImageEmoji();
            var reactionCount = 0;

            var reactionEmote = event.getReactionEmote();
            if (voteEmote != null) {
                // Emote
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

            var number = server.getBestOfImageNumber();
            var percentage = server.getBestOfImagePercentage();

            var onlineMemberCount = 0;
            var members = event.getGuild().getMembers();
            for (var member : members) if (member.getOnlineStatus() == OnlineStatus.ONLINE) onlineMemberCount++;
            var onlineMemberPercentage = (int) Math.ceil(onlineMemberCount * percentage / 100.0);

            var lang = new Server(event.getGuild()).getLanguage();

            if (number != 0 && percentage != 0) {
                if (reactionCount >= number && reactionCount >= onlineMemberPercentage)
                    sendBestOfImage(message, server, reactionCount, attachments, lang);
                else temporaryBlacklist.remove(messageId);
            } else if (number != 0) {
                if (reactionCount >= number)
                    sendBestOfImage(message, server, reactionCount, attachments, lang);
                else temporaryBlacklist.remove(messageId);
            } else if (percentage != 0) {
                if (reactionCount >= onlineMemberPercentage)
                    sendBestOfImage(message, server, reactionCount, attachments, lang);
                else temporaryBlacklist.remove(messageId);
            } else temporaryBlacklist.remove(messageId);
        }, failure -> { /* Ignored */ });

        new Timer().schedule(TimeUtil.wrap(() -> temporaryBlacklist.remove(messageId)), 10 * 1000);
    }

    private static void sendBestOfImage(Message message, Server server, int reactionCount, List<Message.Attachment> attachmentList, String lang) {
        var channel = server.getBestOfImageChannel();
        var author = message.getAuthor();

        for (var attachment : attachmentList) {
            var eb = new EmbedBuilder();
            eb.setColor(new Master(author).getColor());
            eb.setAuthor(author.getName() + "#" + author.getDiscriminator(), null, author.getEffectiveAvatarUrl());
            eb.setTitle(!message.getContentDisplay().isEmpty() ? message.getContentDisplay() : null);
            eb.setDescription("[" + LanguageHandler.get(lang, "bestof_jump") + "](" + message.getJumpUrl() + ")");
            eb.setImage(attachment.getUrl());
            eb.setFooter(String.format(LanguageHandler.get(lang, "bestof_footer"), reactionCount, message.getChannel().getName()), null);
            eb.setTimestamp(message.getTimeCreated());

            channel.sendMessage(eb.build()).queue();

            // Spam prevention. Every message just once.
            server.addBestOfImageBlacklist(message.getIdLong());
        }
    }

    private void processBestOfQuote(GuildMessageReactionAddEvent event, long messageId) {
        event.getChannel().retrieveMessageById(messageId).queue(message -> {
            var attachments = message.getAttachments();
            if (!attachments.isEmpty()) {
                temporaryBlacklist.remove(messageId);
                return;
            }

            var bestOfQuote = new BestOfQuote(event.getJDA(), message.getGuild().getIdLong());
            var voteEmote = bestOfQuote.getEmote();
            var voteEmoji = bestOfQuote.getEmoji();
            var reactionCount = 0;

            var reactionEmote = event.getReactionEmote();
            if (voteEmote != null) {
                // Emote
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

            var number = bestOfQuote.getNumber();
            var percentage = bestOfQuote.getPercentage();

            var onlineMemberCount = 0;
            var members = event.getGuild().getMembers();
            for (var member : members) if (member.getOnlineStatus() == OnlineStatus.ONLINE) onlineMemberCount++;
            var onlineMemberPercentage = (int) Math.ceil(onlineMemberCount * percentage / 100.0);
            var lang = new Server(event.getGuild()).getLanguage();

            if (number != 0 && percentage != 0) {
                if (reactionCount >= number && reactionCount >= onlineMemberPercentage)
                    sendBestOfQuote(message, reactionCount, lang);
                else temporaryBlacklist.remove(messageId);
            } else if (number != 0) {
                if (reactionCount >= number)
                    sendBestOfQuote(message, reactionCount, lang);
                else temporaryBlacklist.remove(messageId);
            } else if (percentage != 0) {
                if (reactionCount >= onlineMemberPercentage)
                    sendBestOfQuote(message, reactionCount, lang);
                else temporaryBlacklist.remove(messageId);
            } else temporaryBlacklist.remove(messageId);
        }, failure -> { /* Ignored */ });

        final var executor = new ScheduledThreadPoolExecutor(1);
        executor.schedule(() -> {
            temporaryBlacklist.remove(messageId);
        }, 10, TimeUnit.SECONDS);
    }

    private static void sendBestOfQuote(Message message, int reactionCount, String lang) {
        var bestOfQuote = new BestOfQuote(message.getJDA(), message.getGuild().getIdLong());
        var channel = bestOfQuote.getChannel();
        var author = message.getAuthor();

        var eb = new EmbedBuilder();
        eb.setColor(new Master(author).getColor());
        eb.setAuthor(author.getName() + "#" + author.getDiscriminator(), null, author.getEffectiveAvatarUrl());
        eb.setTitle(!message.getContentDisplay().isEmpty() ? message.getContentDisplay() : null);
        eb.setDescription("[" + LanguageHandler.get(lang, "bestof_jump") + "](" + message.getJumpUrl() + ")");
        eb.setFooter(String.format(LanguageHandler.get(lang, "bestof_footer"), reactionCount, message.getChannel().getName()), null);
        eb.setTimestamp(message.getTimeCreated());

        channel.sendMessage(eb.build()).queue();

        // Spam prevention. Every message just once.
        bestOfQuote.addBlacklist(message.getIdLong());
    }

    private static void processQuickpollEnd(GuildMessageReactionAddEvent event, Poll poll, User user, long messageId) {
        var reactionEmote = event.getReactionEmote();
        if (!reactionEmote.isEmote()) {
            if (!reactionEmote.getName().equals(EmoteUtil.getEmoji("end"))) return; // Has to be the end emoji.
            if (user.getIdLong() != poll.getAuthorId()) return; // Has to be done by author.

            // The author has reacted with an ending emote on their quickpoll.
            event.getChannel().retrieveMessageById(messageId).queue(message -> poll.endQuickPoll());
        }
    }

    private static void processQuickpollMultipleVote(GuildMessageReactionAddEvent event, Poll poll, User user, long messageId) {
        // Just react to Upvote, Shrug and Downvote.
        var reactionEmote = event.getReactionEmote();
        if (!reactionEmote.getName().equals(EmoteUtil.getEmoji("upvote"))
                && !reactionEmote.getName().equals(EmoteUtil.getEmoji("shrug"))
                && !reactionEmote.getName().equals(EmoteUtil.getEmoji("downvote"))) return;

        event.getChannel().retrieveMessageById(messageId).queue(message -> {
            var userId = user.getIdLong();
            if (poll.hasVoted(userId)) event.getReaction().removeReaction(user).queue();
            else poll.setVote(userId, (reactionEmote.isEmote() ? reactionEmote.getEmote().getIdLong() : 0), (reactionEmote.isEmote() ? "" : reactionEmote.getName()));
        });
    }

    private static void processPollEnd(GuildMessageReactionAddEvent event, Poll poll, User user, long messageId) {
        var reactionEmote = event.getReactionEmote();
        if (!reactionEmote.isEmote()) {
            if (!reactionEmote.getName().equals(EmoteUtil.getEmoji("end"))) return;
        } else return;

        if (user.getIdLong() != poll.getAuthorId()) return; // Has to be done by author.

        // The author has reacted with an ending emote on their poll.
        event.getChannel().retrieveMessageById(messageId).queue(message -> poll.end());
    }

    private static void processRadiopollMultipleVote(GuildMessageReactionAddEvent event, Poll poll, User user, long messageId) {
        // Just react to One - Ten.
        var reactionEmote = event.getReactionEmote();
        if (!reactionEmote.isEmote()) {
            if (!reactionEmote.getName().equals(EmoteUtil.getEmoji("one"))
                    && !reactionEmote.getName().equals(EmoteUtil.getEmoji("two"))
                    && !reactionEmote.getName().equals(EmoteUtil.getEmoji("three"))
                    && !reactionEmote.getName().equals(EmoteUtil.getEmoji("four"))
                    && !reactionEmote.getName().equals(EmoteUtil.getEmoji("five"))
                    && !reactionEmote.getName().equals(EmoteUtil.getEmoji("six"))
                    && !reactionEmote.getName().equals(EmoteUtil.getEmoji("seven"))
                    && !reactionEmote.getName().equals(EmoteUtil.getEmoji("eight"))
                    && !reactionEmote.getName().equals(EmoteUtil.getEmoji("nine"))
                    && !reactionEmote.getName().equals(EmoteUtil.getEmoji("ten"))
            ) return;
        } else return;

        event.getChannel().retrieveMessageById(messageId).queue(message -> {
            var userId = user.getIdLong();
            if (poll.hasVoted(userId)) event.getReaction().removeReaction(user).queue();
            else poll.setVote(userId, (reactionEmote.isEmote() ? reactionEmote.getEmote().getIdLong() : 0), (reactionEmote.isEmote() ? "" : reactionEmote.getName()));
        });
    }

    private static void processReactionRole(GuildMessageReactionAddEvent event, Guild guild) {
        var guildId = guild.getIdLong();
        var channelId = event.getChannel().getIdLong();
        var messageId = event.getMessageIdLong();

        String emoji = null;
        var emoteGuildId = 0L;
        var emoteId = 0L;

        var reactionEmote = event.getReactionEmote();
        if (reactionEmote.isEmote()) {
            if (reactionEmote.getEmote().getGuild() == null) return;
            emoteGuildId = reactionEmote.getEmote().getGuild().getIdLong();
            emoteId = reactionEmote.getEmote().getIdLong();
        } else {
            emoji = reactionEmote.getName();
        }

        var reactionRole = new ReactionRole(event.getJDA(), guildId, channelId, messageId, emoji, emoteGuildId, emoteId);

        if (reactionRole.hasEntry()) {
            var roleId = reactionRole.getRoleId();
            try {
                var rolesToAdd = new ArrayList<Role>();
                rolesToAdd.add(event.getGuild().getRoleById(roleId));
                event.getGuild().modifyMemberRoles(event.getMember(), rolesToAdd, null).queue();
            } catch (InsufficientPermissionException | HierarchyException e) {
                event.getChannel().sendMessage(LanguageHandler.get(new Server(event.getGuild()).getLanguage(), "reactionrole_insufficient")).queue();
            }
        }
    }

    private static void processSignup(GuildMessageReactionAddEvent event, User user, Server server, Signup signup, long messageId) {
        var forceEnd = false;
        var endEmoji = EmoteUtil.getEmoji("end");
        if (!event.getReactionEmote().isEmote() && event.getReactionEmote().getName().equals(endEmoji)) {
            if (event.getUser().getIdLong() != signup.getAuthorId())
                event.getReaction().removeReaction(user).queue();
            else forceEnd = true;
        }

        var expiration = signup.getTime();
        var isCustomDate = signup.isCustomDate();
        if (!isCustomDate) expiration = Timestamp.from(OffsetDateTime.now(ZoneOffset.of(server.getOffset())).toInstant());

        var finalForceEnd = forceEnd;
        var finalExpiration = expiration;
        event.getChannel().retrieveMessageById(messageId).queue(message -> signup.end(server, message, finalForceEnd, finalExpiration.toInstant()));
    }
}
