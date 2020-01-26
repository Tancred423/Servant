// Author: Tancred423 (https://github.com/Tancred423)
package listeners;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.toggle.Toggle;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
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
import useful.polls.PollsDatabase;
import useful.signup.Signup;
import utilities.Emote;
import utilities.Time;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
        if (Blacklist.isBlacklisted(user, guild)) return;

        CompletableFuture.runAsync(() -> {
            var internalGuild = new Guild(guild.getIdLong());
            var messageId = event.getMessageIdLong();
            var lang = new Guild(event.getGuild().getIdLong()).getLanguage(guild, user);

            // Best Of Image
            if (Toggle.isEnabled(event, "bestofimage") && !temporaryBlacklist.contains(messageId)) {
                temporaryBlacklist.add(messageId);
                if (internalGuild.bestOfImageIsBlacklisted(messageId, guild, user)) temporaryBlacklist.remove(messageId);
                else processBestOfImage(event, guild, user, internalGuild, messageId);
            }

            // Best Of Quote
            if (Toggle.isEnabled(event, "bestofquote") && !temporaryBlacklist.contains(messageId)) {
                temporaryBlacklist.add(messageId);
                if (internalGuild.bestOfQuoteIsBlacklisted(messageId, guild, user)) temporaryBlacklist.remove(messageId);
                else processBestOfQuote(event, guild, user, internalGuild, messageId);
            }

            // Giveaway
            if (Toggle.isEnabled(event, "giveaway")
                    && GiveawayHandler.isGiveaway(guild.getIdLong(), channel.getIdLong(), messageId, guild, user)
                    && (event.getReactionEmote().isEmoji() && event.getReactionEmote().getName().equals(Emote.getEmoji("end")))) {
                channel.retrieveMessageById(messageId).queue(message -> {
                    var giveaway = new Giveaway(guild.getIdLong(), channel.getIdLong(), messageId, jda.getSelfUser());
                    GiveawayHandler.announceWinners(message, giveaway.getAmountWinners(), giveaway.getPrize(), lang, jda.getUserById(giveaway.getHostId()));
                });
            }

            // Quickpoll
            if (Toggle.isEnabled(event, "quickvote") && PollsDatabase.isQuickvote(messageId, guild, user)) {
                processQuickpollEnd(event, guild, user, messageId, lang);
                processQuickpollMultipleVote(event, guild, user, messageId);
            }

            // Poll
            if (Toggle.isEnabled(event, "vote") && (PollsDatabase.isVote(messageId, guild, user) || PollsDatabase.isRadioVote(messageId, guild, user))) {
                processPollEnd(event, guild, user, messageId, lang);
            }

            // Radiopoll
            if (Toggle.isEnabled(event, "radiovote") && PollsDatabase.isRadioVote(messageId, guild, user)) {
                processRadiopollMultipleVote(event, guild, user, messageId);
            }

            // Reaction Role
            if (Toggle.isEnabled(event, "reactionrole")) {
                processReactionRole(event, guild, user, internalGuild);
            }

            // Signup
            if (Toggle.isEnabled(event, "signup") && internalGuild.isSignupMessage(messageId, guild, event.getUser())) {
                processSignup(event, guild, user, internalGuild, messageId);
            }
        }, Servant.threadPool);
    }

    private void processBestOfImage(GuildMessageReactionAddEvent event, net.dv8tion.jda.api.entities.Guild guild, User user, Guild internalGuild, long messageId) {
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
                    sendBestOfImage(message, guild, user, internalGuild, reactionCount, attachments, lang);
                else temporaryBlacklist.remove(messageId);
            } else if (number != 0) {
                if (reactionCount >= number)
                    sendBestOfImage(message, guild, user, internalGuild, reactionCount, attachments, lang);
                else temporaryBlacklist.remove(messageId);
            } else if (percentage != 0) {
                if (reactionCount >= onlineMemberPercentage)
                    sendBestOfImage(message, guild, user, internalGuild, reactionCount, attachments, lang);
                else temporaryBlacklist.remove(messageId);
            } else temporaryBlacklist.remove(messageId);
        }, failure -> { /* Ignored */ });

        new Timer().schedule(Time.wrap(() -> temporaryBlacklist.remove(messageId)), 10 * 1000);
    }

    private static void sendBestOfImage(Message message, net.dv8tion.jda.api.entities.Guild guild, User user,
                                   Guild internalGuild, int reactionCount, List<Message.Attachment> attachmentList, String lang) {
        var channel = internalGuild.getBestOfImageChannel(guild, user);
        var author = message.getAuthor();

        for (var attachment : attachmentList) {
            var eb = new EmbedBuilder();
            eb.setColor(new moderation.user.User(author.getIdLong()).getColor(guild, user));
            eb.setAuthor(author.getName() + "#" + author.getDiscriminator(), null, author.getEffectiveAvatarUrl());
            eb.setTitle(!message.getContentDisplay().isEmpty() ? message.getContentDisplay() : null);
            eb.setDescription("[" + LanguageHandler.get(lang, "bestof_jump") + "](" + message.getJumpUrl() + ")");
            eb.setImage(attachment.getUrl());
            eb.setFooter(String.format(LanguageHandler.get(lang, "bestof_footer"), reactionCount, message.getChannel().getName()), null);
            eb.setTimestamp(message.getTimeCreated());

            channel.sendMessage(eb.build()).queue();

            // Spam prevention. Every message just once.
            internalGuild.addBestOfImageBlacklist(message.getIdLong(), guild, user);
        }
    }

    private void processBestOfQuote(GuildMessageReactionAddEvent event, net.dv8tion.jda.api.entities.Guild guild, User user, Guild internalGuild, long messageId) {
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
                    sendBestOfQuote(message, guild, user, internalGuild, reactionCount, lang);
                else temporaryBlacklist.remove(messageId);
            } else if (number != 0) {
                if (reactionCount >= number)
                    sendBestOfQuote(message, guild, user, internalGuild, reactionCount, lang);
                else temporaryBlacklist.remove(messageId);
            } else if (percentage != 0) {
                if (reactionCount >= onlineMemberPercentage)
                    sendBestOfQuote(message, guild, user, internalGuild, reactionCount, lang);
                else temporaryBlacklist.remove(messageId);
            } else temporaryBlacklist.remove(messageId);
        }, failure -> { /* Ignored */ });

        final var executor = new ScheduledThreadPoolExecutor(1);
        executor.schedule(() -> {
            temporaryBlacklist.remove(messageId);
        }, 10, TimeUnit.SECONDS);
    }

    private static void sendBestOfQuote(Message message, net.dv8tion.jda.api.entities.Guild guild, User user,
                                        Guild internalGuild, int reactionCount, String lang) {
        var channel = internalGuild.getBestOfQuoteChannel(guild, user);
        var author = message.getAuthor();

        var eb = new EmbedBuilder();
        eb.setColor(new moderation.user.User(author.getIdLong()).getColor(guild, user));
        eb.setAuthor(author.getName() + "#" + author.getDiscriminator(), null, author.getEffectiveAvatarUrl());
        eb.setTitle(!message.getContentDisplay().isEmpty() ? message.getContentDisplay() : null);
        eb.setDescription("[" + LanguageHandler.get(lang, "bestof_jump") + "](" + message.getJumpUrl() + ")");
        eb.setFooter(String.format(LanguageHandler.get(lang, "bestof_footer"), reactionCount, message.getChannel().getName()), null);
        eb.setTimestamp(message.getTimeCreated());

        channel.sendMessage(eb.build()).queue();

        // Spam prevention. Every message just once.
        internalGuild.addBestOfQuoteBlacklist(message.getIdLong(), guild, user);
    }

    private static void processQuickpollEnd(GuildMessageReactionAddEvent event, net.dv8tion.jda.api.entities.Guild guild,
                                            User user, long messageId, String lang) {
        var reactionEmote = event.getReactionEmote();
        if (reactionEmote.isEmote()) {
            if (!reactionEmote.getEmote().equals(Emote.getEmote("end", guild, user))) return; // Has to be the end emote ...
        } else {
            if (!reactionEmote.getName().equals(Emote.getEmoji("end"))) return; // ... or the end emoji.
        }

        if (user.getIdLong() != PollsDatabase.getAuthorId(messageId, guild, user)) return; // Has to be done by author.

        // The author has reacted with an ending emote on their quickpoll.
        event.getChannel().retrieveMessageById(messageId).queue(message -> Poll.endQuickpoll(guild, user, message, lang, event.getJDA()));
    }

    private static void processQuickpollMultipleVote(GuildMessageReactionAddEvent event, net.dv8tion.jda.api.entities.Guild guild,
                                                     User user, long messageId) {
        // Just react to Upvote, Shrug and Downvote.
        var reactionEmote = event.getReactionEmote();
        if (!reactionEmote.getName().equals(Emote.getEmoji("upvote"))
                && !reactionEmote.getName().equals(Emote.getEmoji("shrug"))
                && !reactionEmote.getName().equals(Emote.getEmoji("downvote"))) return;

        event.getChannel().retrieveMessageById(messageId).queue(message -> {
            var userId = user.getIdLong();
            if (PollsDatabase.hasVoted(messageId, userId, guild, user)) event.getReaction().removeReaction(user).queue();
            else PollsDatabase.setUserVote(messageId, userId, (reactionEmote.isEmote() ? reactionEmote.getEmote().getIdLong() : 0), (reactionEmote.isEmote() ? "" : reactionEmote.getName()), guild, user);
        });
    }

    private static void processPollEnd(GuildMessageReactionAddEvent event, net.dv8tion.jda.api.entities.Guild guild, User user, long messageId, String lang) {
        var reactionEmote = event.getReactionEmote();
        if (!reactionEmote.isEmote()) {
            if (!reactionEmote.getName().equals(Emote.getEmoji("end"))) return;
        } else return;

        if (user.getIdLong() != PollsDatabase.getAuthorId(messageId, guild, user)) return; // Has to be done by author.

        // The author has reacted with an ending emote on their poll.
        event.getChannel().retrieveMessageById(messageId).queue(message -> Poll.endPoll(guild, user, message, lang, event.getJDA()));
    }

    private static void processRadiopollMultipleVote(GuildMessageReactionAddEvent event, net.dv8tion.jda.api.entities.Guild guild,
                                                     User user, long messageId) {
        // Just react to One - Ten.
        var reactionEmote = event.getReactionEmote();
        if (reactionEmote.isEmote()) {
            if (!reactionEmote.getEmote().equals(Emote.getEmote("one", guild, user))
                    && !reactionEmote.getEmote().equals(Emote.getEmote("two", guild, user))
                    && !reactionEmote.getEmote().equals(Emote.getEmote("three", guild, user))
                    && !reactionEmote.getEmote().equals(Emote.getEmote("four", guild, user))
                    && !reactionEmote.getEmote().equals(Emote.getEmote("five", guild, user))
                    && !reactionEmote.getEmote().equals(Emote.getEmote("six", guild, user))
                    && !reactionEmote.getEmote().equals(Emote.getEmote("seven", guild, user))
                    && !reactionEmote.getEmote().equals(Emote.getEmote("eight", guild, user))
                    && !reactionEmote.getEmote().equals(Emote.getEmote("nine", guild, user))
                    && !reactionEmote.getEmote().equals(Emote.getEmote("ten", guild, user))
            ) return;
        } else {
            if (!reactionEmote.getName().equals(Emote.getEmoji("one"))
                    && !reactionEmote.getName().equals(Emote.getEmoji("two"))
                    && !reactionEmote.getName().equals(Emote.getEmoji("three"))
                    && !reactionEmote.getName().equals(Emote.getEmoji("four"))
                    && !reactionEmote.getName().equals(Emote.getEmoji("five"))
                    && !reactionEmote.getName().equals(Emote.getEmoji("six"))
                    && !reactionEmote.getName().equals(Emote.getEmoji("seven"))
                    && !reactionEmote.getName().equals(Emote.getEmoji("eight"))
                    && !reactionEmote.getName().equals(Emote.getEmoji("nine"))
                    && !reactionEmote.getName().equals(Emote.getEmoji("ten"))
            ) return;
        }

        event.getChannel().retrieveMessageById(messageId).queue(message -> {
            var userId = user.getIdLong();
            if (PollsDatabase.hasVoted(messageId, userId, guild, user)) event.getReaction().removeReaction(user).queue();
            else PollsDatabase.setUserVote(messageId, userId, (reactionEmote.isEmote() ? reactionEmote.getEmote().getIdLong() : 0), (reactionEmote.isEmote() ? "" : reactionEmote.getName()), guild, user);
        });
    }

    private static void processReactionRole(GuildMessageReactionAddEvent event, net.dv8tion.jda.api.entities.Guild guild, User user, Guild internalGuild) {
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

        if (internalGuild.reactionRoleHasEntry(guildId, channelId, messageId, emoji, emoteGuildId, emoteId, guild, user)) {
            var roleId = internalGuild.getRoleId(guildId, channelId, messageId, emoji, emoteGuildId, emoteId, guild, user);
            try {
                var rolesToAdd = new ArrayList<Role>();
                rolesToAdd.add(event.getGuild().getRoleById(roleId));
                event.getGuild().modifyMemberRoles(event.getMember(), rolesToAdd, null).queue();
            } catch (InsufficientPermissionException | HierarchyException e) {
                event.getChannel().sendMessage(LanguageHandler.get(new Guild(event.getGuild().getIdLong()).getLanguage(guild, user), "reactionrole_insufficient")).queue();
            }
        }
    }

    private static void processSignup(GuildMessageReactionAddEvent event, net.dv8tion.jda.api.entities.Guild guild, User user, Guild internalGuild, long messageId) {
        var forceEnd = false;
        var endEmoji = Emote.getEmoji("end");
        if (!event.getReactionEmote().isEmote() && event.getReactionEmote().getName().equals(endEmoji)) {
            if (event.getUser().getIdLong() != internalGuild.getSignupAuthorId(event.getMessageIdLong(), guild, user))
                event.getReaction().removeReaction(user).queue();
            else forceEnd = true;
        }

        var expiration = internalGuild.getSignupTime(messageId, guild, user);
        var isCustomDate = internalGuild.signupIsCustomDate(messageId, guild, user);
        if (!isCustomDate) expiration = ZonedDateTime.now(ZoneOffset.of(internalGuild.getOffset(guild, user)));

        var finalForceEnd = forceEnd;
        var finalExpiration = expiration;
        event.getChannel().retrieveMessageById(messageId).queue(message -> Signup.endSignup(internalGuild, messageId, message, guild, event.getUser(), finalForceEnd, finalExpiration));
    }
}
