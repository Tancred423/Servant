// Author: Tancred423 (https://github.com/Tancred423)
package useful.signup;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import owner.blacklist.Blacklist;
import utilities.Constants;
import utilities.Emote;
import utilities.Image;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;

public class SignupCommand extends Command {
    public SignupCommand() {
        this.name = "signup";
        this.aliases = new String[0];
        this.help = "Organise events.";
        this.category = new Category("Useful");
        this.arguments = null;
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
            var p = GuildHandler.getPrefix(event);

            if (event.getArgs().isEmpty()) {
                var description = LanguageHandler.get(lang, "signup_description");
                var usage = String.format(LanguageHandler.get(lang, "signup_usage"), p, name, p, name);
                var hint = LanguageHandler.get(lang, "signup_hint");
                event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
                return;
            }

            var args = event.getArgs().split(" ");

            int amount;

            try {
                amount = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                event.reactError();
                event.reply(LanguageHandler.get(lang, "signup_invalidamount"));
                return;
            }

            if (amount < 1 || amount > 100) {
                event.reactWarning();
                event.reply(LanguageHandler.get(lang, "signup_amountrange"));
                return;
            }

            var sb = new StringBuilder();
            for (int i = 1; i < args.length; i++) sb.append(args[i]).append(" ");
            var title = sb.toString().trim();

            if (title.length() > 256) {
                event.reactWarning();
                event.reply(LanguageHandler.get(lang, "signup_titlelength"));
                return;
            }

            var guild = event.getGuild();
            var author = event.getAuthor();
            var internalAuthor = new User(author.getIdLong());

            var eb = new EmbedBuilder();
            eb.setColor(internalAuthor.getColor(guild, author));
            eb.setTitle(String.format(LanguageHandler.get(lang, "signup_embedtitle"), (title.isEmpty() ? "" : "for"), title));
            eb.setDescription(String.format(LanguageHandler.get(lang, "signup_embeddescription"), amount, Emote.getEmoji("upvote")));
            eb.setFooter(LanguageHandler.get(lang, "signup_timeout"), Image.getImageUrl("clock", guild, author));
            var internalGuild = new Guild(event.getGuild().getIdLong());
            var expiration = ZonedDateTime.now(ZoneOffset.of(internalGuild.getOffset(guild, author))).plusDays(7);
            eb.setTimestamp(expiration.toInstant());

            event.getChannel().sendMessage(eb.build()).queue(sentMessage -> {
                sentMessage.addReaction(Emote.getEmoji("upvote")).queue();
                sentMessage.addReaction(Emote.getEmoji("end")).queue();
                internalGuild.setSignup(sentMessage.getIdLong(), author.getIdLong(), amount, title, Timestamp.valueOf(expiration.toLocalDateTime()), sentMessage.getChannel().getIdLong(), guild, author);
            });

            // Statistics.
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
            new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
        });
    }
}
