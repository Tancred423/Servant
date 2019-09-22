// Author: Tancred423 (https://github.com/Tancred423)
package moderation.join;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageChannel;
import servant.Log;
import utilities.Constants;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;

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
        this.userPermissions = new Permission[]{Permission.MANAGE_CHANNEL};
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!Toggle.isEnabled(event, name)) return;

        var lang = LanguageHandler.getLanguage(event, name);
        var p = GuildHandler.getPrefix(event, name);

        var guild = event.getGuild();
        var internalGuild = new moderation.guild.Guild(guild.getIdLong());
        if (event.getArgs().isEmpty()) {
            try {
                var description = LanguageHandler.get(lang, "join_description");
                var usage = String.format(LanguageHandler.get(lang, "join_usage"), p, name, p, name, p, name, p, name);
                var hint = LanguageHandler.get(lang, "join_hint");
                event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            }
            return;
        }

        var args = event.getArgs().split(" ");
        MessageChannel channel;

        switch (args[0].toLowerCase()) {
            case "set":
            case "s":
                if (args.length < 2) {
                    event.reply(LanguageHandler.get(lang, "joinleave_nochannel_mention"));
                    return;
                }

                channel = event.getMessage().getMentionedChannels().get(0);

                try {
                    internalGuild.setJoinNotifierChannel(channel);
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                    return;
                }
                event.reactSuccess();
                break;

            case "unset":
            case "u":
                boolean wasUnset;
                try {
                    wasUnset = internalGuild.unsetJoinNotifierChannel();
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                    return;
                }
                if (wasUnset) event.reactSuccess();
                else event.reply(LanguageHandler.get(lang, "joinleave_unset_fail"));
                break;

            case "show":
            case "sh":
                try {
                    channel = internalGuild.getJoinNotifierChannel();
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                    return;
                }
                if (channel == null) event.reply(LanguageHandler.get(lang, "joinleave_nochannel_set"));
                else event.reply(String.format(LanguageHandler.get(lang, "joinleave_current"), channel.getName()));
                break;

            default:
                event.reply(LanguageHandler.get(lang, "joinleave_firstarg"));
                event.reactError();
                break;
        }

        // Statistics.
        try {
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            if (event.getGuild() != null) new moderation.guild.Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }
    }
}
