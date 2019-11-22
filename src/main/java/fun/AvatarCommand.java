// Author: Tancred423 (https://github.com/Tancred423)
package fun;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.api.Permission;
import owner.blacklist.Blacklist;
import utilities.Constants;
import utilities.MessageHandler;
import utilities.Parser;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.util.concurrent.CompletableFuture;

public class AvatarCommand extends Command {
    public AvatarCommand() {
        this.name = "avatar";
        this.aliases = new String[] { "ava" };
        this.help = "Returns mentioned user's avatar.";
        this.category = new Category("Fun");
        this.arguments = "@user";
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected void execute(CommandEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                if (!Toggle.isEnabled(event, name)) return;
                if (Blacklist.isBlacklisted(event.getAuthor(), event.getGuild())) return;

                var lang = LanguageHandler.getLanguage(event);
                var p = GuildHandler.getPrefix(event);

                if (event.getArgs().isEmpty()) {
                    var description = LanguageHandler.get(lang, "avatar_description");
                    var usage = String.format(LanguageHandler.get(lang, "avatar_usage"), p, name);
                    event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, null).getEmbed());
                    return;
                }

                // Check mentioned user.
                if (!Parser.hasMentionedUser(event.getMessage())) {
                    event.reply(LanguageHandler.get(lang, "invalid_mention"));
                    return;
                }

                var guild = event.getGuild();
                var message = event.getMessage();
                var channel = message.getChannel();
                var author = message.getAuthor();
                var mentioned = message.getMentionedUsers().get(0);
                var avatarUrl = mentioned.getAvatarUrl();
                var color = new User(author.getIdLong()).getColor(guild, author);

                new MessageHandler().sendEmbed(
                        channel,
                        color,
                        "Avatar", null, null,
                        null,
                        null,
                        String.format(LanguageHandler.get(lang, "avatar_stolen"), author.getAsMention(), mentioned.getAsMention()),
                        null,
                        avatarUrl,
                        null,
                        null
                );

                // Statistics.
                new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
                if (event.getGuild() != null)
                    new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
