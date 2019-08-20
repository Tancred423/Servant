// Author: Tancred423 (https://github.com/Tancred423)
package vote;

import moderation.guild.Guild;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import servant.Emote;
import servant.Log;
import servant.Servant;
import servant.User;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public class QuickvoteCommand extends Command {
    public QuickvoteCommand() {
        this.name = "quickvote";
        this.aliases = new String[]{"qv"};
        this.help = "Smol vote with yes/no/idc.";
        this.category = new Category("Vote");
        this.arguments = "[optional text]";
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = 5; // 5 seconds
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[]{Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_EXT_EMOJI};
    }

    @Override
    protected void execute(CommandEvent event) {
        // Enabled?
        try {
            if (!new Guild(event.getGuild().getIdLong()).getToggleStatus("guild")) return;
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }

        var message = event.getMessage();
        var author = message.getAuthor();
        var internalAuthor = new servant.User(author.getIdLong());
        var eb = new EmbedBuilder();

        try {
            eb.setColor(internalAuthor.getColor());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            return;
        }
        eb.setAuthor(author.getName() + " started a quickvote!", null, author.getAvatarUrl());
        eb.setDescription(event.getArgs());
        eb.setFooter("This vote is active.", event.getJDA().getSelfUser().getAvatarUrl());
        try {
            eb.setTimestamp(OffsetDateTime.now(ZoneId.of(new Guild(event.getGuild().getIdLong()).getOffset())));
        } catch (SQLException | DateTimeException e) {
            eb.setTimestamp(OffsetDateTime.now(ZoneId.of(Servant.config.getDefaultOffset())).getOffset());
        }
        message.getChannel().sendMessage(eb.build()).queue(sentMessage -> {
            try {
                var upvoteEmote     = Emote.getEmote("upvote");
                var shrugEmote      = Emote.getEmote("shrug");
                var downvoteEmote   = Emote.getEmote("downvote");
                var endEmote        = Emote.getEmote("end");

                var upvoteEmoji     = Emote.getEmoji("upvote");
                var shrugEmoji      = Emote.getEmoji("shrug");
                var downvoteEmoji   = Emote.getEmoji("downvote");
                var endEmoji        = Emote.getEmoji("end");

                if (upvoteEmote != null && shrugEmote != null && downvoteEmote != null) {
                    sentMessage.addReaction(upvoteEmote).queue();
                    sentMessage.addReaction(shrugEmote).queue();
                    sentMessage.addReaction(downvoteEmote).queue();
                    sentMessage.addReaction(endEmote).queue();
                    VoteDatabase.setQuickvote(sentMessage.getIdLong(), author.getIdLong(), "quick");
                } else if (upvoteEmoji != null && shrugEmoji != null && downvoteEmoji != null) {
                    sentMessage.addReaction(upvoteEmoji).queue();
                    sentMessage.addReaction(shrugEmoji).queue();
                    sentMessage.addReaction(downvoteEmoji).queue();
                    sentMessage.addReaction(endEmoji).queue();
                    VoteDatabase.setQuickvote(sentMessage.getIdLong(), author.getIdLong(), "quick");
                } else {
                    event.reactError();
                    event.reply("It's not your fault, ...! I cannot find the correct emotes. I'll let my master know ~");
                    event.getJDA().getUserById(Servant.config.getBotOwnerId()).openPrivateChannel().queue(privateChannel ->
                            privateChannel.sendMessage("Greetings master! I was not able to pull of a quickvote as I am missing the following emotes:\n" +
                                    (upvoteEmote    == null ? "upvote (Emote)\n"    : "") +
                                    (upvoteEmoji    == null ? "upvote (Emoji)\n"    : "") +
                                    (shrugEmote     == null ? "shrug (Emote)\n"     : "") +
                                    (shrugEmoji     == null ? "shrug (Emoji)\n"     : "") +
                                    (downvoteEmote  == null ? "downvote (Emote)\n"  : "") +
                                    (downvoteEmoji  == null ? "downvote (Emoji)\n"  : "") +
                                    (endEmote       == null ? "end (Emote)\n"       : "") +
                                    (endEmoji       == null ? "end (Emoji)\n"       : "")
                            ).queue());
                }
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            }
        });

        message.delete().queue();

        // Statistics.
        try {
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            if (event.getGuild() != null) new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }
    }
}
