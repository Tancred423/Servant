// Author: Tancred423 (https://github.com/Tancred423)
package listeners;

import commands.owner.blacklist.Blacklist;
import commands.utility.polls.Poll;
import commands.utility.rate.Rating;
import commands.utility.signup.Signup;
import files.language.LanguageHandler;
import plugins.moderation.reactionRole.ReactionRole;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import servant.MyGuild;
import servant.MyMessage;
import servant.Servant;
import utilities.Constants;
import utilities.EmoteUtil;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class GuildMessageReactionRemoveListener extends ListenerAdapter {
    // This event will be thrown if a user removes their reaction from a message in a guild.
    public void onGuildMessageReactionRemove(@NotNull GuildMessageReactionRemoveEvent event) {
        var jda = event.getJDA();
        var guild = event.getGuild();
        var tc = event.getChannel();
        var msgId = event.getMessageIdLong();
        var user = event.getUser();

        // Blacklist
        if (guild.getIdLong() == Constants.DISCORD_BOT_LIST_ID) return;
        if (user == null) return;
        if (user.isBot()) return;
        if (Blacklist.isBlacklisted(guild, user)) return;

        CompletableFuture.runAsync(() -> {
            var myGuild = new MyGuild(guild);
            var myMessage = new MyMessage(jda, guild.getIdLong(), tc.getIdLong(), msgId);
            var lang = myGuild.getLanguageCode();
            var reactionEmote = event.getReactionEmote();

            // Quickpoll
            if (myGuild.commandIsEnabled("quickpoll") && myMessage.isQuickpoll() && reactionEmote.isEmoji()) {
                if (reactionEmote.getName().equals(EmoteUtil.getEmoji(jda, "upvote"))
                        || reactionEmote.getName().equals(EmoteUtil.getEmoji(jda, "downvote"))) {
                    var quickpoll = new Poll(jda, lang, guild.getIdLong(), tc.getIdLong(), msgId);

                    event.getChannel().retrieveMessageById(msgId).queue(message -> {
                        if (reactionEmote.getName().equals(quickpoll.getVoteEmoji(user.getIdLong())))
                            quickpoll.unsetVote(user.getIdLong(), reactionEmote.getName());
                    });
                }
            }

            // Poll
            if (myGuild.commandIsEnabled("poll") && (myMessage.isCheckpoll() || myMessage.isRadiopoll()) && reactionEmote.isEmoji()) {
                var poll = new Poll(jda, lang, guild.getIdLong(), tc.getIdLong(), msgId);

                var amountAnswers = poll.getAmountAnswers();
                var reactions = new ArrayList<String>();
                for (int i = 1; i <= amountAnswers; i++) {
                    String name = null;
                    switch (i) {
                        case 1:
                            name = "one";
                            break;
                        case 2:
                            name = "two";
                            break;
                        case 3:
                            name = "three";
                            break;
                        case 4:
                            name = "four";
                            break;
                        case 5:
                            name = "five";
                            break;
                        case 6:
                            name = "six";
                            break;
                        case 7:
                            name = "seven";
                            break;
                        case 8:
                            name = "eight";
                            break;
                        case 9:
                            name = "nine";
                            break;
                        case 10:
                            name = "ten";
                            break;
                    }
                    reactions.add(EmoteUtil.getEmoji(jda, name));
                }

                if (reactions.contains(reactionEmote.getName())) {
                    event.getChannel().retrieveMessageById(msgId).queue(message -> {
                        poll.unsetVote(user.getIdLong(), reactionEmote.getName());
                    });
                }
            }

            // Rating
            if (myGuild.commandIsEnabled("rating") && myMessage.isRating() && reactionEmote.isEmoji()) {
                var rating = new Rating(jda, lang, guild.getIdLong(), tc.getIdLong(), msgId);
                var reactionEmoteName = event.getReactionEmote().getName();

                if (rating.hasParticipated(user.getIdLong())
                        && rating.getParticipantEmoji(user.getIdLong()).equals(reactionEmoteName))
                    rating.unsetParticipant(user.getIdLong(), reactionEmote.getName());
            }

            // Reaction Role
            if (myGuild.pluginIsEnabled("reactionrole") & myGuild.categoryIsEnabled("moderation") && myMessage.isReactionRole() && reactionEmote.isEmoji()) {
                processReactionRole(event, guild);
            }

            // Signup
            if (myGuild.commandIsEnabled("signup") && myMessage.isSignup() && reactionEmote.isEmoji()) {
                if (reactionEmote.getName().equals(EmoteUtil.getEmoji(jda, "upvote"))) {
                    var signup = new Signup(jda, guild.getIdLong(), tc.getIdLong(), msgId);
                    signup.unsetParticipant(user.getIdLong());
                }
            }
        }, Servant.fixedThreadPool);
    }

    private static void processReactionRole(GuildMessageReactionRemoveEvent event, net.dv8tion.jda.api.entities.Guild guild) {
        var messageId = event.getMessageIdLong();
        var reactionEmote = event.getReactionEmote();
        var emoji = reactionEmote.getName();
        var reactionRole = new ReactionRole(event.getJDA(), messageId);
        var roleIds = reactionRole.getRoleIds(emoji);
        try {
            var rolesToRemove = new ArrayList<Role>();
            for (var roleId : roleIds) {
                var role = guild.getRoleById(roleId);
                if (role == null) {
                    reactionRole.deleteRoleId(roleId);
                } else rolesToRemove.add(role);
            }
            var member = event.getMember();
            if (member != null)
                guild.modifyMemberRoles(member, null, rolesToRemove).queue();
        } catch (InsufficientPermissionException | HierarchyException e) {
            event.getChannel().sendMessage(LanguageHandler.get(new MyGuild(event.getGuild()).getLanguageCode(), "reactionrole_insufficient")).queue();
        }
    }
}
