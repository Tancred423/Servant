// Author: Tancred423 (https://github.com/Tancred423)
package moderation;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.guild.Server;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageReaction;
import utilities.Constants;
import utilities.EmoteUtil;
import utilities.MessageUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.util.List;

public class ReactionRoleCommand extends Command {
    public ReactionRoleCommand() {
        this.name = "reactionrole";
        this.aliases = new String[0];
        this.help = "Role management via reactions.";
        this.category = new Category("Moderation");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.MOD_COOLDOWN;
        this.cooldownScope = CooldownScope.GUILD;
        this.userPermissions = new Permission[] { Permission.MANAGE_ROLES };
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS, Permission.MANAGE_ROLES, Permission.MESSAGE_ADD_REACTION
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var guild = event.getGuild();
        var author = event.getAuthor();
        var lang = LanguageHandler.getLanguage(event);
        var p = GuildHandler.getPrefix(event);
        var tancWave = EmoteUtil.getEmote("tancWave", guild, author);
        if (tancWave == null) return;

        if (event.getArgs().isEmpty()) {
            var description = LanguageHandler.get(lang, "reactionrole_description");
            var usage = String.format(LanguageHandler.get(lang, "reactionrole_usage"), p, name, p, name, tancWave.getAsMention(), p, name, p, name, tancWave.getAsMention());
            var hint = LanguageHandler.get(lang, "reactionrole_hint");

            event.reply(MessageUtil.createUsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint));
            return;
        }

        var message = event.getMessage();
        var args = event.getArgs().trim().replaceAll(" +", " ").split(" ");
        long channelId;
        MessageChannel reactionChannel;
        long messageId;
        var internalGuild = new Server(guild);

        switch (args[0].toLowerCase()) {
            case "set":
            case "s":
                if (args.length < 5) {
                    event.reactError();
                    event.reply(LanguageHandler.get(lang, "reactionrole_toofewargs"));
                    return;
                }

                // Channel ID
                if (message.getMentionedChannels().isEmpty()) channelId = Long.parseLong(args[1]);
                else channelId = message.getMentionedChannels().get(0).getIdLong();

                reactionChannel = event.getGuild().getTextChannelById(channelId);
                if (reactionChannel == null) return;

                // Message ID
                try {
                    messageId = Long.parseLong(args[2]);
                } catch (NumberFormatException e) {
                    event.replyError(LanguageHandler.get(lang, "reactionrole_invalidmessageid"));
                    return;
                }

                reactionChannel.retrieveMessageById(messageId).queue(reactionMessage -> {
                    // Emoji / Emote
                    String emoji = null;
                    Emote emote = null;
                    if (message.getEmotes().isEmpty()) emoji = args[3];
                    else emote = message.getEmotes().get(0);

                    try {
                        if (emoji == null && emote != null) message.addReaction(emote).queue();
                        else if (emoji != null) message.addReaction(emoji).queue();
                    } catch (IllegalArgumentException e) {
                        event.reactWarning();
                        event.reply(LanguageHandler.get(lang, "reactionrole_unavailable_emote"));
                        return;
                    }

                    // Role
                    long roleId;
                    if (message.getMentionedRoles().isEmpty()) roleId = Long.parseLong(args[4]);
                    else roleId = message.getMentionedRoles().get(0).getIdLong();
                    event.getGuild().getRoleById(roleId);

                    var success = internalGuild.setReactionRole(
                            event.getGuild().getIdLong(),
                            channelId,
                            messageId,
                            emoji,
                            (emote == null ? 0 : (emote.getGuild() == null ? 0 : emote.getGuild().getIdLong())),
                            (emote == null ? 0 : emote.getIdLong()),
                            roleId);

                    if (success) {
                        event.reactError();
                        event.reply(LanguageHandler.get(lang, "reactionrole_alreadyset"));
                    } else {
                        event.reactSuccess();
                        var finalEmoji = emoji;
                        var finalEmote = emote;
                        var tc = event.getGuild().getTextChannelById(channelId);
                        if (tc == null) return;
                        tc.retrieveMessageById(messageId).queue(msg -> {
                            if (finalEmoji == null && finalEmote != null) msg.addReaction(finalEmote).queue();
                            else if (finalEmoji != null) msg.addReaction(finalEmoji).queue();
                        });
                    }
                }, failure -> event.replyError(LanguageHandler.get(lang, "unknown_message")));
                break;

            case "unset":
            case "u":
                if (args.length < 4) {
                    event.reactError();
                    event.reply(LanguageHandler.get(lang, "reactionrole_toofewargs"));
                    return;
                }

                // Channel ID
                if (message.getMentionedChannels().isEmpty()) channelId = Long.parseLong(args[1]);
                else channelId = message.getMentionedChannels().get(0).getIdLong();

                reactionChannel = event.getGuild().getTextChannelById(channelId);
                if (reactionChannel == null) return;

                // Message ID
                messageId = Long.parseLong(args[2]);
                reactionChannel.retrieveMessageById(messageId).queue(reactionMessage -> {
                    // Emoji / Emote
                    String emoji = null;
                    Emote emote = null;
                    if (message.getEmotes().isEmpty()) emoji = args[3];
                    else emote = message.getEmotes().get(0);
                    if (emoji == null && emote != null) message.addReaction(emote).queue();
                    else if (emoji != null) message.addReaction(emoji).queue();

                    boolean success;
                    success = internalGuild.unsetReactionRole(
                            event.getGuild().getIdLong(),
                            channelId,
                            messageId,
                            emoji,
                            (emote == null ? 0 : (emote.getGuild() == null ? 0 : emote.getGuild().getIdLong())),
                            (emote == null ? 0 : emote.getIdLong()));

                    if (success) {
                        event.reactError();
                        event.reply(LanguageHandler.get(lang, "reactionrole_notset"));
                    } else {
                        event.reactSuccess();
                        var finalEmoji = emoji;
                        var finalEmote = emote;
                        var tc = event.getGuild().getTextChannelById(channelId);
                        if (tc == null) return;
                        tc.retrieveMessageById(messageId).queue(msg -> {
                            List<MessageReaction> reactions = msg.getReactions();
                            for (MessageReaction reaction : reactions)
                                if (reaction.getReactionEmote().isEmote()) {
                                    if (reaction.getReactionEmote().getEmote().equals(finalEmote)) {
                                        reaction.removeReaction().queue();
                                        break;
                                    }
                                } else {
                                    if (reaction.getReactionEmote().getName().equals(finalEmoji)) {
                                        reaction.removeReaction().queue();
                                        break;
                                    }
                                }
                        });
                    }

                });
                break;

            default:
                event.reactError();
                event.reply(LanguageHandler.get(lang, "reactionrole_firstarg"));
                break;
        }
    }
}
