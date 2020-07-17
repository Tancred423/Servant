// Author: Tancred423 (https://github.com/Tancred423)
package listeners;

import commands.owner.blacklist.Blacklist;
import commands.utility.giveaway.Giveaway;
import commands.utility.polls.Poll;
import commands.utility.rate.Rating;
import commands.utility.remindme.RemindMe;
import commands.utility.signup.Signup;
import files.language.LanguageHandler;
import plugins.moderation.bestOfQuote.BestOfQuoteHandler;
import plugins.moderation.reactionRole.ReactionRole;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import servant.MyGuild;
import servant.MyMessage;
import servant.MyUser;
import servant.Servant;
import utilities.Constants;
import utilities.EmoteUtil;
import utilities.TimeUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.CompletableFuture;

public class GuildMessageReactionAddListener extends ListenerAdapter {
    // A temporary blacklist to prevent multiple reactions processes.
    private static final List<Long> temporaryBlacklistImage = new ArrayList<>();
    private static final List<Long> temporaryBlacklistQuote = new ArrayList<>();

    // This event will be thrown if a user reacts on a message in a guild.
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        var jda = event.getJDA();
        var guild = event.getGuild();
        var tc = event.getChannel();
        var msgId = event.getMessageIdLong();
        var user = event.getUser();

        // Blacklist
        if (guild.getIdLong() == Constants.DISCORD_BOT_LIST_ID) return;
        if (user.isBot()) return;
        if (Blacklist.isBlacklisted(guild, user)) return;

        CompletableFuture.runAsync(() -> {
            var myGuild = new MyGuild(guild);
            var myUser = new MyUser(user);
            var myMessage = new MyMessage(jda, guild.getIdLong(), tc.getIdLong(), msgId);
            var lang = myGuild.getLanguageCode();
            var reactionEmote = event.getReactionEmote();

            // Best Of Image
            if (myGuild.pluginIsEnabled("bestofimage") && myGuild.categoryIsEnabled("moderation") && !temporaryBlacklistImage.contains(msgId)) {
                temporaryBlacklistImage.add(msgId);
                if (myGuild.bestOfImageIsBlacklisted(msgId)) temporaryBlacklistImage.remove(msgId);
                else processBestOfImage(event, myGuild, msgId);
            }

            // Best Of Quote
            if (myGuild.pluginIsEnabled("bestofquote") && myGuild.categoryIsEnabled("moderation") && !temporaryBlacklistQuote.contains(msgId)) {
                temporaryBlacklistQuote.add(msgId);
                if (myGuild.bestOfQuoteIsBlacklisted(msgId)) temporaryBlacklistQuote.remove(msgId);
                else processBestOfQuote(event, myGuild, msgId);
            }

            // Giveaway
            if (myGuild.commandIsEnabled("giveaway") && myMessage.isGiveaway() && reactionEmote.isEmoji()) {
                if (reactionEmote.getName().equals(EmoteUtil.getEmoji(jda, "tada"))) {
                    // Enter participant
                    var giveaway = new Giveaway(jda, lang, guild.getIdLong(), tc.getIdLong(), msgId);
                    var wasSet = giveaway.setParticipant(user.getIdLong());
                    if (wasSet) {
                        tc.retrieveMessageById(msgId).queue(message -> event.getReaction().retrieveUsers().queue(users -> {
                            if (users.size() > 4900) {
                                user.openPrivateChannel().queue(pc ->
                                        pc.sendMessage(new EmbedBuilder()
                                                .setColor(Color.decode(myUser.getColorCode()))
                                                .setTitle("Giveaway")
                                                .setDescription(String.format(LanguageHandler.get(lang, "giveaway_dm"), message.getJumpUrl()))
                                                .setFooter(LanguageHandler.get(lang, "giveaway_dm_footer"))
                                                .build()
                                        ).queue());
                            }
                        }));
                    }
                } else if (reactionEmote.getName().equals(EmoteUtil.getEmoji(jda, "end"))) {
                    // Announce winners
                    var giveaway = new Giveaway(jda, lang, guild.getIdLong(), tc.getIdLong(), msgId);
                    if (giveaway.getAuthorId() == user.getIdLong()) {
                        tc.retrieveMessageById(msgId).queue(message -> giveaway.end());
                    }
                }
            }

            // Quickpoll
            if (myGuild.commandIsEnabled("quickpoll") && myMessage.isQuickpoll() && reactionEmote.isEmoji()) {
                var quickpoll = new Poll(jda, lang, guild.getIdLong(), tc.getIdLong(), msgId);
                if (reactionEmote.getName().equals(EmoteUtil.getEmoji(jda, "upvote"))
                        || reactionEmote.getName().equals(EmoteUtil.getEmoji(jda, "downvote"))) {
                    // Handle multi vote
                    event.getChannel().retrieveMessageById(msgId).queue(message -> {
                        var userId = user.getIdLong();
                        if (quickpoll.hasVoted(userId)) event.getReaction().removeReaction(user).queue();
                        else quickpoll.setVote(user.getIdLong(), reactionEmote.getName());
                    });
                } else if (reactionEmote.getName().equals(EmoteUtil.getEmoji(jda, "end"))) {
                    // End quickpoll
                    if (quickpoll.getAuthorId() == user.getIdLong())
                        tc.retrieveMessageById(msgId).queue(message -> quickpoll.endQuickPoll());
                }
            }

            // Poll
            if (myGuild.commandIsEnabled("poll") && (myMessage.isCheckpoll() || myMessage.isRadiopoll())) {
                var poll = new Poll(jda, lang, guild.getIdLong(), tc.getIdLong(), msgId);

                var amountAnswers = poll.getAmountAnswers();
                var reactions = new ArrayList<String>();
                for (int i = 1; i <= amountAnswers; i++) {
                    String name = null;
                    switch (i) {
                        case 1: name = "one";
                            break;
                        case 2: name = "two";
                            break;
                        case 3: name = "three";
                            break;
                        case 4: name = "four";
                            break;
                        case 5: name = "five";
                            break;
                        case 6: name = "six";
                            break;
                        case 7: name = "seven";
                            break;
                        case 8: name = "eight";
                            break;
                        case 9: name = "nine";
                            break;
                        case 10: name = "ten";
                            break;
                    }
                    reactions.add(EmoteUtil.getEmoji(jda, name));
                }

                if (reactions.contains(reactionEmote.getName())) {
                    // Add user vote
                    event.getChannel().retrieveMessageById(msgId).queue(message -> {
                        var userId = user.getIdLong();
                        if (poll.hasVoted(userId) && myMessage.isRadiopoll()) event.getReaction().removeReaction(user).queue();
                        else poll.setVote(userId, reactionEmote.getName());
                    });
                } else if (reactionEmote.getName().equals(EmoteUtil.getEmoji(jda, "end"))) {
                    // End poll
                    if (poll.getAuthorId() == user.getIdLong())
                        tc.retrieveMessageById(msgId).queue(message -> poll.endPoll());
                }
            }

            // Rating
            if (myGuild.commandIsEnabled("rating") && myMessage.isRating() && reactionEmote.isEmoji()) {
                var rating = new Rating(jda, lang, guild.getIdLong(), tc.getIdLong(), msgId);
                var reactionEmoteName = reactionEmote.getName();
                if (reactionEmoteName.equals(EmoteUtil.getEmoji(jda, "end"))) {
                    // End Rating
                    if (rating.getAuthorId() == user.getIdLong())
                        tc.retrieveMessageById(msgId).queue(message -> rating.end());
                    else event.getReaction().removeReaction(user).queue();
                } else if (reactionEmoteName.equals(EmoteUtil.getEmoji(jda, "one"))
                        || reactionEmoteName.equals(EmoteUtil.getEmoji(jda, "two"))
                        || reactionEmoteName.equals(EmoteUtil.getEmoji(jda, "three"))
                        || reactionEmoteName.equals(EmoteUtil.getEmoji(jda, "four"))
                        || reactionEmoteName.equals(EmoteUtil.getEmoji(jda, "five"))) {
                    if (rating.hasParticipated(user.getIdLong())) event.getReaction().removeReaction(user).queue();
                    else rating.setParticipant(user.getIdLong(), reactionEmoteName);
                }
            }

            // Reaction Role
            if (myGuild.pluginIsEnabled("reactionrole") && myGuild.categoryIsEnabled("moderation") && myMessage.isReactionRole() && reactionEmote.isEmoji())
                processReactionRole(event, guild);

            // RemindMe
            if (myGuild.commandIsEnabled("remindme") && myMessage.isRemindMe() && reactionEmote.isEmoji()) {
                if (reactionEmote.getName().equals(EmoteUtil.getEmoji(jda, "upvote"))) {
                    // Enter participant
                    var remindMe = new RemindMe(jda, guild.getIdLong(), tc.getIdLong(), msgId);
                    if (remindMe.getAuthorId() != event.getUserIdLong()) {
                        var wasSet = remindMe.setParticipant(user.getIdLong());
                        if (wasSet) {
                            tc.retrieveMessageById(msgId).queue(message -> event.getReaction().retrieveUsers().queue(users -> {
                                if (users.size() > 4900) {
                                    user.openPrivateChannel().queue(pc ->
                                            pc.sendMessage(new EmbedBuilder()
                                                    .setColor(Color.decode(myUser.getColorCode()))
                                                    .setTitle("RemindMe")
                                                    .setDescription(String.format(LanguageHandler.get(lang, "remindme_dm"), message.getJumpUrl()))
                                                    .setFooter(LanguageHandler.get(lang, "remindme_dm_footer"))
                                                    .build()
                                            ).queue());
                                }
                            }));
                        }
                    }
                }
            }

            // Signup
            if (myGuild.commandIsEnabled("signup") && myMessage.isSignup() && reactionEmote.isEmoji()) {
                if (reactionEmote.getName().equals(EmoteUtil.getEmoji(jda, "upvote"))) {
                    // Enter participant & check amount after
                    var signup = new Signup(jda, guild.getIdLong(), tc.getIdLong(), msgId);
                    var wasSet = signup.setParticipant(user.getIdLong());
                    if (wasSet && signup.getParticipants().size() >= signup.getAmountParticipants()) signup.end();
                } else if (reactionEmote.getName().equals(EmoteUtil.getEmoji(jda, "end"))) {
                    // End signup
                    var signup = new Signup(jda, guild.getIdLong(), tc.getIdLong(), msgId);
                    if (signup.getAuthorId() == user.getIdLong())
                        tc.retrieveMessageById(msgId).queue(message -> signup.end());
                    else event.getReaction().removeReaction(user).queue();
                }
            }
        }, Servant.fixedThreadPool);
    }

    private void processBestOfImage(GuildMessageReactionAddEvent event, MyGuild myGuild, long messageId) {
        event.getChannel().retrieveMessageById(messageId).queue(message -> {
            var attachments = message.getAttachments();
            if (attachments.isEmpty()) {
                temporaryBlacklistImage.remove(messageId);
                return;
            }

            for (var attachment : attachments) if (!attachment.isImage()) {
                temporaryBlacklistImage.remove(messageId);
                return;
            }

            var voteEmoji = myGuild.getBestOfImageEmoji();
            var reactionCount = 0;

            var reactionEmote = event.getReactionEmote();
            if (reactionEmote.isEmoji()) {
                // Emoji
                if (!reactionEmote.getName().equals(voteEmoji)) {
                    temporaryBlacklistImage.remove(messageId);
                    return;
                }

                var messageReactions = message.getReactions();
                for (var mr : messageReactions)
                    if (mr.getReactionEmote().getName().equals(voteEmoji)) {
                        reactionCount = mr.getCount();
                        break;
                    }

                if (reactionCount == 0) {
                    temporaryBlacklistImage.remove(messageId);
                    return;
                }
            } else {
                temporaryBlacklistImage.remove(messageId);
                return;
            }

            var number = myGuild.getBestOfImageMinVotesFlat();
            var percentage = myGuild.getBestOfImageMinVotesPercent();

            var onlineMemberCount = 0;
            var members = event.getGuild().getMembers();
            for (var member : members) if (member.getOnlineStatus() == OnlineStatus.ONLINE) onlineMemberCount++;
            var onlineMemberPercentage = (int) Math.ceil(onlineMemberCount * percentage / 100.0);
            var lang = new MyGuild(event.getGuild()).getLanguageCode();

            if (number != 0 && percentage != 0) {
                if (reactionCount >= number && reactionCount >= onlineMemberPercentage)
                    sendBestOfImage(message, myGuild, reactionCount, attachments, lang);
                else temporaryBlacklistImage.remove(messageId);
            } else if (number != 0) {
                if (reactionCount >= number)
                    sendBestOfImage(message, myGuild, reactionCount, attachments, lang);
                else temporaryBlacklistImage.remove(messageId);
            } else if (percentage != 0) {
                if (reactionCount >= onlineMemberPercentage)
                    sendBestOfImage(message, myGuild, reactionCount, attachments, lang);
                else temporaryBlacklistImage.remove(messageId);
            } else temporaryBlacklistImage.remove(messageId);
        }, failure -> { /* Ignored */ });

        new Timer().schedule(TimeUtil.wrap(() -> temporaryBlacklistImage.remove(messageId)), 10 * 1000);
    }

    private static void sendBestOfImage(Message message, MyGuild myGuild, int reactionCount, List<Message.Attachment> attachmentList, String lang) {
        var channel = myGuild.getBestOfImageChannel();
        if (channel == null) return;
        var author = message.getAuthor();

        for (var attachment : attachmentList) {
            var eb = new EmbedBuilder();
            eb.setColor(Color.decode(new MyUser(author).getColorCode()));
            eb.setAuthor(author.getName() + "#" + author.getDiscriminator(), null, author.getEffectiveAvatarUrl());
            eb.setTitle(!message.getContentDisplay().isEmpty() ? message.getContentDisplay() : null);
            eb.setDescription("[" + LanguageHandler.get(lang, "bestof_jump") + "](" + message.getJumpUrl() + ")");
            eb.setImage(attachment.getUrl());
            eb.setFooter(String.format(LanguageHandler.get(lang, "bestof_footer"), reactionCount, message.getChannel().getName()), null);
            eb.setTimestamp(message.getTimeCreated());

            channel.sendMessage(eb.build()).queue();

            // Spam prevention. Every message just once.
            myGuild.addBestOfImageBlacklist(message.getIdLong(), message.getTextChannel().getIdLong());
        }
    }

    private void processBestOfQuote(GuildMessageReactionAddEvent event, MyGuild myGuild, long msgId) {
        event.getChannel().retrieveMessageById(msgId).queue(message -> {
            var attachments = message.getAttachments();
            if (!attachments.isEmpty()) {
                temporaryBlacklistQuote.remove(msgId);
                return;
            }

            var voteEmoji = myGuild.getBestOfQuoteEmoji();
            var reactionCount = 0;

            var reactionEmote = event.getReactionEmote();
            if (reactionEmote.isEmoji()) {
                // Emoji
                if (!reactionEmote.getName().equals(voteEmoji)) {
                    temporaryBlacklistQuote.remove(msgId);
                    return;
                }

                var messageReactions = message.getReactions();
                for (var mr : messageReactions)
                    if (mr.getReactionEmote().getName().equals(voteEmoji)) {
                        reactionCount = mr.getCount();
                        break;
                    }

                if (reactionCount == 0) {
                    temporaryBlacklistQuote.remove(msgId);
                    return;
                }
            } else {
                temporaryBlacklistQuote.remove(msgId);
                return;
            }

            var number = myGuild.getBestOfQuoteMinVotesFlat();
            var percentage = myGuild.getBestOfQuoteMinVotesPercent();

            var onlineMemberCount = 0;
            var members = event.getGuild().getMembers();
            for (var member : members) if (member.getOnlineStatus() == OnlineStatus.ONLINE) onlineMemberCount++;
            var onlineMemberPercentage = (int) Math.ceil(onlineMemberCount * percentage / 100.0);
            var lang = new MyGuild(event.getGuild()).getLanguageCode();

            if (number != 0 && percentage != 0) {
                if (reactionCount >= number && reactionCount >= onlineMemberPercentage)
                    sendBestOfQuote(message, reactionCount, lang);
                else temporaryBlacklistQuote.remove(msgId);
            } else if (number != 0) {
                if (reactionCount >= number)
                    sendBestOfQuote(message, reactionCount, lang);
                else temporaryBlacklistQuote.remove(msgId);
            } else if (percentage != 0) {
                if (reactionCount >= onlineMemberPercentage)
                    sendBestOfQuote(message, reactionCount, lang);
                else temporaryBlacklistQuote.remove(msgId);
            } else temporaryBlacklistQuote.remove(msgId);
        }, failure -> { /* Ignored */ });

        new Timer().schedule(TimeUtil.wrap(() -> temporaryBlacklistQuote.remove(msgId)), 10 * 1000);
    }

    private static void sendBestOfQuote(Message message, int reactionCount, String lang) {
        var bestOfQuote = new BestOfQuoteHandler(message.getJDA(), message.getGuild().getIdLong());
        var channel = bestOfQuote.getChannel();
        if (channel == null) return;
        var author = message.getAuthor();

        var content = message.getContentDisplay().replaceAll("\n", "\n> ");
        content = "> " + content;

        var eb = new EmbedBuilder();
        eb.setColor(Color.decode(new MyUser(author).getColorCode()));
        eb.setAuthor(author.getName() + "#" + author.getDiscriminator(), null, author.getEffectiveAvatarUrl());
        eb.setDescription("[" + LanguageHandler.get(lang, "bestof_jump") + "](" + message.getJumpUrl() + ")\n\n " +
                content);
        eb.setFooter(String.format(LanguageHandler.get(lang, "bestof_footer"), reactionCount, message.getChannel().getName()), null);
        eb.setTimestamp(message.getTimeCreated());

        channel.sendMessage(eb.build()).queue();

        // Spam prevention. Every message just once.
        bestOfQuote.addBlacklist(message.getIdLong(), message.getTextChannel().getIdLong());
    }

    private static void processReactionRole(GuildMessageReactionAddEvent event, Guild guild) {
        var messageId = event.getMessageIdLong();

        var reactionEmote = event.getReactionEmote();
        var emoji = reactionEmote.getName();

        var reactionRole = new ReactionRole(event.getJDA(), messageId);

        var roleIds = reactionRole.getRoleIds(emoji);
        try {
            var rolesToAdd = new ArrayList<Role>();
            for (var roleId : roleIds) {
                var role = guild.getRoleById(roleId);
                if (role == null) {
                    reactionRole.deleteRoleId(roleId);
                } else rolesToAdd.add(role);
            }
            guild.modifyMemberRoles(event.getMember(), rolesToAdd, null).queue();
        } catch (InsufficientPermissionException | HierarchyException e) {
            event.getChannel().sendMessage(LanguageHandler.get(new MyGuild(event.getGuild()).getLanguageCode(), "reactionrole_insufficient")).queue();
        }
    }
}
