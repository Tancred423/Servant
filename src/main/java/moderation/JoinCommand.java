// Author: Tancred423 (https://github.com/Tancred423)
package moderation;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import owner.blacklist.Blacklist;
import servant.Servant;
import utilities.Constants;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.util.concurrent.CompletableFuture;

public class JoinCommand extends Command {
    public JoinCommand() {
        this.name = "join";
        this.aliases = new String[0];
        this.help = "Alert for joining user.";
        this.category = new Category("Moderation");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.MOD_COOLDOWN;
        this.cooldownScope = CooldownScope.GUILD;
        this.userPermissions = new Permission[] { Permission.MANAGE_CHANNEL };
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                if (!Toggle.isEnabled(event, name)) return;
                if (Blacklist.isBlacklisted(event.getAuthor(), event.getGuild())) return;

                var lang = LanguageHandler.getLanguage(event);
                var p = GuildHandler.getPrefix(event);

                var author = event.getAuthor();
                var guild = event.getGuild();
                var internalGuild = new moderation.guild.Guild(guild.getIdLong());
                if (event.getArgs().isEmpty()) {
                    var description = LanguageHandler.get(lang, "join_description");
                    var usage = String.format(LanguageHandler.get(lang, "join_usage"), p, name, p, name, p, name, p, name);
                    event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, null).getEmbed());
                    return;
                }

                var args = event.getArgs().split(" ");
                MessageChannel channel;

                switch (args[0].toLowerCase()) {
                    case "set":
                    case "s":
                        if (args.length < 2 || event.getMessage().getMentionedChannels().isEmpty()) {
                            event.reply(LanguageHandler.get(lang, "joinleave_nochannel_mention"));
                            return;
                        }

                        channel = event.getMessage().getMentionedChannels().get(0);

                        internalGuild.setJoinNotifierChannel(channel, guild, author);
                        event.reactSuccess();
                        break;

                    case "unset":
                    case "u":
                        boolean wasUnset;
                        wasUnset = internalGuild.unsetJoinNotifierChannel(guild, author);
                        if (wasUnset) event.reactSuccess();
                        else event.reply(LanguageHandler.get(lang, "joinleave_unset_fail"));
                        break;

                    case "show":
                    case "sh":
                        channel = internalGuild.getJoinNotifierChannel(guild, author);
                        if (channel == null) event.reply(LanguageHandler.get(lang, "joinleave_nochannel_set"));
                        else
                            event.reply(String.format(LanguageHandler.get(lang, "joinleave_current"), channel.getName()));
                        break;

                    default:
                        event.reply(LanguageHandler.get(lang, "joinleave_firstarg"));
                        event.reactError();
                        break;
                }

                // Statistics.
                new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
                if (event.getGuild() != null)
                    new moderation.guild.Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Servant.threadPool);
    }
}
