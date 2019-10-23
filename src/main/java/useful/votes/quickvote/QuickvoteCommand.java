// Author: Tancred423 (https://github.com/Tancred423)
package useful.votes.quickvote;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.toggle.Toggle;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import useful.votes.VotesDatabase;
import utilities.Emote;
import servant.Log;
import servant.Servant;
import moderation.user.User;
import utilities.Constants;
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
        this.help = "Smol vote with yes/shrug/no.";
        this.category = new Category("Useful");
        this.arguments = "[optional text]";
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION};
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!Toggle.isEnabled(event, name)) return;

        var lang = LanguageHandler.getLanguage(event, name);
        var message = event.getMessage();
        var author = message.getAuthor();
        var internalAuthor = new User(author.getIdLong());
        var eb = new EmbedBuilder();

        try {
            eb.setColor(internalAuthor.getColor());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            return;
        }
        eb.setAuthor(String.format(LanguageHandler.get(lang, "quickvote_started"), author.getName()), null, author.getEffectiveAvatarUrl());
        eb.setDescription(event.getArgs());
        eb.setFooter(LanguageHandler.get(lang, "votes_active"), event.getJDA().getSelfUser().getAvatarUrl());
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
                    VotesDatabase.setVote(sentMessage.getIdLong(), author.getIdLong(), "quick");
                } else if (upvoteEmoji != null && shrugEmoji != null && downvoteEmoji != null) {
                    sentMessage.addReaction(upvoteEmoji).queue();
                    sentMessage.addReaction(shrugEmoji).queue();
                    sentMessage.addReaction(downvoteEmoji).queue();
                    sentMessage.addReaction(endEmoji).queue();
                    VotesDatabase.setVote(sentMessage.getIdLong(), author.getIdLong(), "quick");
                } else {
                    event.reactError();
                    event.reply(LanguageHandler.get(lang, "votes_emote_fail"));
                    event.getJDA().getUserById(Servant.config.getBotOwnerId()).openPrivateChannel().queue(privateChannel ->
                            privateChannel.sendMessage(LanguageHandler.get(lang, "quickvote_emote_dm") + "\n" +
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
