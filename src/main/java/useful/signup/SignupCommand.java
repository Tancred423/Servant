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
import servant.Log;
import servant.Servant;
import utilities.Constants;
import utilities.Emote;
import utilities.Image;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.awt.*;
import java.sql.SQLException;
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

            var lang = LanguageHandler.getLanguage(event, name);
            var p = GuildHandler.getPrefix(event, name);

            if (event.getArgs().isEmpty()) {
                try {
                    var description = LanguageHandler.get(lang, "signup_description");
                    var usage = String.format(LanguageHandler.get(lang, "signup_usage"), p, name, p, name);
                    var hint = LanguageHandler.get(lang, "signup_hint");
                    event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                }
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

            var author = event.getAuthor();
            var internalAuthor = new User(author.getIdLong());

            var eb = new EmbedBuilder();
            try {
                eb.setColor(internalAuthor.getColor());
            } catch (SQLException e) {
                eb.setColor(Color.decode(Servant.config.getDefaultColorCode()));
            }
            eb.setTitle(String.format(LanguageHandler.get(lang, "signup_embedtitle"), (title.isEmpty() ? "" : "for"), title));
            eb.setDescription(String.format(LanguageHandler.get(lang, "signup_embeddescription"), amount, Emote.getEmoji("upvote")));
            eb.setFooter(LanguageHandler.get(lang, "signup_timeout"), Image.getImageUrl("clock"));
            ZonedDateTime now;
            try {
                now = ZonedDateTime.now(ZoneOffset.of(new Guild(event.getGuild().getIdLong()).getOffset()));
            } catch (SQLException e) {
                now = ZonedDateTime.now(ZoneOffset.of(Servant.config.getDefaultOffset()));
            }
            eb.setTimestamp(now.toInstant().plusMillis(Constants.SIGNUP_TIMEOUT));

            event.getChannel().sendMessage(eb.build()).queue(sentMessage -> {
                sentMessage.addReaction(Emote.getEmoji("upvote")).queue();
                sentMessage.addReaction(Emote.getEmoji("end")).queue();
                var internalGuild = new Guild(event.getGuild().getIdLong());

                try {
                    internalGuild.setSignup(sentMessage.getIdLong(), author.getIdLong(), amount, title);
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                    return;
                }

                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                var guild = event.getGuild();
                                var internalGuild = new Guild(guild.getIdLong());
                                SignupListener.endSignup(internalGuild, sentMessage.getIdLong(), sentMessage, guild, author, true);
                            }
                        }, Constants.SIGNUP_TIMEOUT
                );
            });

            // Statistics.
            try {
                new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
                new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
            }
        });
    }
}
