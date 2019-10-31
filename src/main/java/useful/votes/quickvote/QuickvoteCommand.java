// Author: Tancred423 (https://github.com/Tancred423)
package useful.votes.quickvote;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import owner.blacklist.Blacklist;
import servant.Servant;
import useful.votes.VotesDatabase;
import utilities.Constants;
import utilities.Emote;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.concurrent.CompletableFuture;

public class QuickvoteCommand extends Command {
    public QuickvoteCommand() {
        this.name = "quickpoll";
        this.aliases = new String[]{"quickvote"};
        this.help = "Smol poll with yes/no.";
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
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, name)) return;
            if (Blacklist.isBlacklisted(event.getAuthor(), event.getGuild())) return;

            var lang = LanguageHandler.getLanguage(event);
            var message = event.getMessage();
            var guild = event.getGuild();
            var author = message.getAuthor();
            var internalAuthor = new User(author.getIdLong());
            var eb = new EmbedBuilder();

            eb.setColor(internalAuthor.getColor(guild, author));
            eb.setAuthor(String.format(LanguageHandler.get(lang, "quickvote_started"), author.getName()), null, author.getEffectiveAvatarUrl());
            eb.setDescription(event.getArgs());
            eb.setFooter(LanguageHandler.get(lang, "votes_active"), event.getJDA().getSelfUser().getAvatarUrl());
            try {
                eb.setTimestamp(OffsetDateTime.now(ZoneId.of(new Guild(event.getGuild().getIdLong()).getOffset(guild, author))));
            } catch (DateTimeException e) {
                eb.setTimestamp(OffsetDateTime.now(ZoneId.of(Servant.config.getDefaultOffset())).getOffset());
            }
            message.getChannel().sendMessage(eb.build()).queue(sentMessage -> {
                sentMessage.addReaction(Emote.getEmoji("upvote")).queue();
                sentMessage.addReaction(Emote.getEmoji("downvote")).queue();
                sentMessage.addReaction(Emote.getEmoji("end")).queue();
                VotesDatabase.setVote(sentMessage.getIdLong(), author.getIdLong(), "quick", guild, author);
            });

            message.delete().queue();

            // Statistics.
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
            if (event.getGuild() != null) new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
        });
    }
}
