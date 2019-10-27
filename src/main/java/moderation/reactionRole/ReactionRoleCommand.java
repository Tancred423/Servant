// Author: Tancred423 (https://github.com/Tancred423)
package moderation.reactionRole;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import moderation.guild.Guild;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageReaction;
import owner.blacklist.Blacklist;
import servant.Log;
import utilities.Constants;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
        this.userPermissions = new Permission[]{Permission.MANAGE_ROLES};
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.MANAGE_ROLES, Permission.MESSAGE_ADD_REACTION};
    }

    @Override
    protected void execute(CommandEvent event) {
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, name)) return;
            if (Blacklist.isBlacklisted(event.getAuthor(), event.getGuild())) return;

            var lang = LanguageHandler.getLanguage(event, name);
            var p = GuildHandler.getPrefix(event, name);
            Emote tancWave = null;
            try {
                tancWave = utilities.Emote.getEmote("tancWave");
            } catch (SQLException ignored) { }
            if (tancWave == null) tancWave = event.getJDA().getGuildById(436925371577925642L).getEmoteById(582852645765775360L); // Delete later

            if (event.getArgs().isEmpty()) {
                try {
                    var description = LanguageHandler.get(lang, "reactionrole_description");
                    var usage = String.format(LanguageHandler.get(lang, "reactionrole_usage"), p, name, p, name, tancWave.getAsMention(), p, name, p, name, tancWave.getAsMention());
                    var hint = LanguageHandler.get(lang, "reactionrole_hint");

                    event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                }
                return;
            }

            var message = event.getMessage();
            var args = event.getArgs().trim().replaceAll(" +", " ").split(" ");
            long channelId;
            MessageChannel reactionChannel;
            long messageId;
            var guild = event.getGuild();
            var internalGuild = new Guild(guild.getIdLong());

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

                    // Message ID
                    try {
                        messageId = Long.parseLong(args[2]);
                    } catch (NumberFormatException e) {
                        event.replyError(LanguageHandler.get(lang, "reactionrole_invalidmessageid"));
                        return;
                    }

                    reactionChannel.getMessageById(messageId).queue(reactionMessage -> {
                        // Emoji / Emote
                        String emoji = null;
                        Emote emote = null;
                        if (message.getEmotes().isEmpty()) emoji = args[3];
                        else emote = message.getEmotes().get(0);
                        if (emoji == null) message.addReaction(emote).queue();
                        else message.addReaction(emoji).queue();

                        // Role
                        long roleId;
                        if (message.getMentionedRoles().isEmpty()) roleId = Long.parseLong(args[4]);
                        else roleId = message.getMentionedRoles().get(0).getIdLong();
                        event.getGuild().getRoleById(roleId);

                        int success;
                        try {
                            success = internalGuild.setReactionRole(
                                    event.getGuild().getIdLong(),
                                    channelId,
                                    messageId,
                                    emoji,
                                    (emote == null ? 0 : emote.getGuild().getIdLong()),
                                    (emote == null ? 0:  emote.getIdLong()),
                                    roleId);
                        } catch (SQLException e) {
                            new Log(e, guild, event.getAuthor(), name, event).sendLog(true);
                            return;
                        }

                        if (success == 0) {
                            event.reactSuccess();
                            var finalEmoji = emoji;
                            var finalEmote = emote;
                            event.getGuild().getTextChannelById(channelId).getMessageById(messageId).queue(msg -> {
                                if (finalEmoji == null) msg.addReaction(finalEmote).queue();
                                else msg.addReaction(finalEmoji).queue();
                            });
                        }
                        else if (success == 1){
                            event.reactError();
                            event.reply(LanguageHandler.get(lang, "reactionrole_alreadyset"));
                        }
                        else event.reactWarning();

                    });
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

                    // Message ID
                    messageId = Long.parseLong(args[2]);
                    reactionChannel.getMessageById(messageId).queue(reactionMessage -> {
                        // Emoji / Emote
                        String emoji = null;
                        Emote emote = null;
                        if (message.getEmotes().isEmpty()) emoji = args[3];
                        else emote = message.getEmotes().get(0);
                        if (emoji == null) message.addReaction(emote).queue();
                        else message.addReaction(emoji).queue();

                        int success;
                        try {
                            success = internalGuild.unsetReactionRole(
                                    event.getGuild().getIdLong(),
                                    channelId,
                                    messageId,
                                    emoji,
                                    (emote == null ? 0 : emote.getGuild().getIdLong()),
                                    (emote == null ? 0:  emote.getIdLong()));
                        } catch (SQLException e) {
                            new Log(e, guild, event.getAuthor(), name, event).sendLog(true);
                            return;
                        }

                        if (success == 0) {
                            event.reactSuccess();
                            var finalEmoji = emoji;
                            var finalEmote = emote;
                            event.getGuild().getTextChannelById(channelId).getMessageById(messageId).queue(msg -> {
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
                        else if (success == 1){
                            event.reactError();
                            event.reply(LanguageHandler.get(lang, "reactionrole_notset"));
                        }
                        else event.reactWarning();

                    });
                    break;

                default:
                    event.reactError();
                    event.reply(LanguageHandler.get(lang, "reactionrole_firstarg"));
                    break;
            }

            // Statistics.
            try {
                new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
                if (event.getGuild() != null) new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
            }
        });
    }
}
