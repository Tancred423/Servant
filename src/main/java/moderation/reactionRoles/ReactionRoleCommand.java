package moderation.reactionRoles;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import moderation.guild.Guild;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageReaction;
import servant.Log;
import servant.Servant;
import utilities.UsageEmbed;

import java.sql.SQLException;
import java.util.List;

public class ReactionRoleCommand extends Command {
    public ReactionRoleCommand() {
        this.name = "reactionrole";
        this.aliases = new String[]{"reactrole", "rr"};
        this.help = "set up automatic role management via reactions | Manage Roles";
        this.category = new Category("Moderation");
        this.arguments = "<message ID> <emoji/emote> [@role|role ID]";
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = 5;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[]{Permission.MANAGE_ROLES};
        this.botPermissions = new Permission[0];
    }
    @Override
    protected void execute(CommandEvent event) {
        // Enabled?
        try {
            if (!new Guild(event.getGuild().getIdLong()).getToggleStatus("reactionrole")) return;
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }

        var prefix = Servant.config.getDefaultPrefix();
        var tancWave = event.getJDA().getGuildById(436925371577925642L).getEmoteById(582852645765775360L);
        if (event.getArgs().isEmpty()) {
            // Usage
            try {
                var usage = "**Set up a reaction to manage user roles**\n" +
                        "Command: `" + prefix + name + " set [#channel | channel ID] [message ID] [emoji/emote] [@role | role ID]`\n" +
                        "Example: " + prefix + name + " set #test-channel 999999999999999999 " + tancWave.getAsMention() + " @role\n" +
                        "\n" +
                        "**Unset a reaction role**\n" +
                        "Command: `" + prefix + name + " unset [#channel | channel ID] [message Id] [emoji/emote]`\n" +
                        "Example: " + prefix + name + " unset #test-channel 999999999999999999 " + tancWave.getAsMention();

                var hint = "**How to get ID's:**\n" +
                        "Role: `\\@role` - THIS ALSO PINGS. Maybe do this is a non public channel.\n" +
                        "Channels: \n" +
                        "1. Activate Discord Developer Mode: User Settings → Appearance → ADVANCED → Developer Mode\n" +
                        "2. Rightclick a text channel → Copy ID";

                event.reply(new UsageEmbed(name, event.getAuthor(), ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
            } catch (SQLException e) {
                new Log(e, event, name).sendLogSqlCommandEvent(true);
            }
            return;
        }

        var message = event.getMessage();
        var args = event.getArgs().trim().replaceAll(" +", " ").split(" ");
        long channelId;
        MessageChannel reactionChannel;
        long messageId;

        switch (args[0].toLowerCase()) {
            case "set":
            case "s":
                if (args.length < 5) {
                    event.reactError();
                    event.reply("Too few arguments. You have to declare the channel ID, message ID, an emoji or emote and a role mention or role ID behind `set`.");
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

                    // Role
                    long roleId;
                    if (message.getMentionedRoles().isEmpty()) roleId = Long.parseLong(args[4]);
                    else roleId = message.getMentionedRoles().get(0).getIdLong();
                    event.getGuild().getRoleById(roleId);

                    var success = ReactionRoleDatabase.setReactionRole(event,
                            event.getGuild().getIdLong(),
                            channelId,
                            messageId,
                            emoji,
                            (emote == null ? 0 : emote.getGuild().getIdLong()),
                            (emote == null ? 0:  emote.getIdLong()),
                            roleId);

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
                        event.reply("This emoji or emote was already set. Unset first if you want to update the emoji or emote.");
                    }
                    else event.reactWarning();

                });
                break;

            case "unset":
            case "u":
                if (args.length < 4) {
                    event.reactError();
                    event.reply("Too few arguments. You have to declare the channel ID, message ID, and an emoji or emote behind `unset`.");
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

                    var success = ReactionRoleDatabase.unsetReactionRole(event,
                            event.getGuild().getIdLong(),
                            channelId,
                            messageId,
                            emoji,
                            (emote == null ? 0 : emote.getGuild().getIdLong()),
                            (emote == null ? 0:  emote.getIdLong()));

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
                        event.reply("This emoji or emote was not set.");
                    }
                    else event.reactWarning();

                });
                break;

            default:
                event.reactError();
                event.reply("Either `set` or `unset` a reaction.");
                break;
        }

        // Statistics.
        try {
            new servant.User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            if (event.getGuild() != null) new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }
    }
}
