// Author: Tancred423 (https://github.com/Tancred423)
package useful.polls.quickpoll;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import owner.blacklist.Blacklist;
import servant.Servant;
import useful.polls.PollsDatabase;
import utilities.Constants;
import utilities.Emote;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.CompletableFuture;

public class QuickpollCommand extends Command {
    public QuickpollCommand() {
        this.name = "quickpoll";
        this.aliases = new String[] { "quickvote" };
        this.help = "Smol poll with yes/no.";
        this.category = new Category("Useful");
        this.arguments = "[optional text]";
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
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
                var dateIn7DaysOtd = OffsetDateTime.now(ZoneOffset.UTC).plusDays(7).toLocalDateTime();
                var dateIn7Days = Timestamp.valueOf(dateIn7DaysOtd);
                eb.setTimestamp(dateIn7DaysOtd);

                var upvote = Emote.getEmoji("upvote");
                var downvote = Emote.getEmoji("downvote");
                var end = Emote.getEmoji("end");

                if (upvote == null || downvote == null || end == null) return;

                message.getChannel().sendMessage(eb.build()).queue(sentMessage -> {
                    sentMessage.addReaction(upvote).queue();
                    sentMessage.addReaction(downvote).queue();
                    sentMessage.addReaction(end).queue();
                    PollsDatabase.setVote(guild.getIdLong(), sentMessage.getChannel().getIdLong(), sentMessage.getIdLong(), author.getIdLong(), "quick", dateIn7Days, guild, author);
                });

                message.delete().queue();

                // Statistics.
                new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
                if (event.getGuild() != null)
                    new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Servant.threadPool);
    }
}
