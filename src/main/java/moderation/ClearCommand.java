// Author: Tancred423 (https://github.com/Tancred423)
package moderation;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import moderation.guild.Guild;
import servant.Log;
import utilities.Constants;
import utilities.MessageHandler;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClearCommand extends Command {
    public ClearCommand() {
        this.name = "clear";
        this.aliases = new String[]{"clean", "remove", "delete", "purge"};
        this.help = "Delete messages.";
        this.category = new Category("Moderation");
        this.arguments = "[1 - 100]";
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.MOD_COOLDOWN;
        this.cooldownScope = CooldownScope.GUILD;
        this.userPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_MANAGE};
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!Toggle.isEnabled(event, name)) return;

        var arg = event.getArgs();
        var lang = LanguageHandler.getLanguage(event, name);
        var p = GuildHandler.getPrefix(event, name);

        if (arg.isEmpty()) {
            try {
                var description = LanguageHandler.get(lang, "clear_description");
                var usage = String.format(LanguageHandler.get(lang, "clear_usage"), p, name, p, name, p, name);
                var hint = LanguageHandler.get(lang, "clear_hint");
                event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            }
            return;
        }

        var channel = event.getChannel();
        if (event.getMessage().getMentionedMembers().isEmpty()) {
            if (!arg.matches("[0-9]+")) {
                event.reply(LanguageHandler.get(lang, "clear_input"));
                return;
            }

            var clearValue = Math.min(Integer.parseInt(arg), 100);

            if (clearValue < 1) {
                event.reply(LanguageHandler.get(lang, "clear_sub_one"));
                return;
            }

            event.getMessage().delete().queue(success -> new MessageHistory(channel).retrievePast(clearValue).queue(messages -> {
                channel.purgeMessages(messages);

                new MessageHandler().sendAndExpire(
                        channel,
                        new MessageBuilder().setContent(String.format(LanguageHandler.get(lang, "clear_cleared"), clearValue)).build(),
                        5 * 1000 // 5 seconds.
                );
            }));
        } else {
            var user = event.getMessage().getMentionedMembers().get(0).getUser();
            event.getMessage().delete().queue(success -> new MessageHistory(channel).retrievePast(100).queue(messages -> {
                List<Message> deleteList = new ArrayList<>();
                for (Message message : messages) if (message.getAuthor().equals(user)) deleteList.add(message);
                channel.purgeMessages(deleteList);

                new MessageHandler().sendAndExpire(
                        channel,
                        new MessageBuilder().setContent(String.format(LanguageHandler.get(lang, "clear_cleared"), deleteList.size())).build(),
                        5 * 1000 // 5 seconds.
                );
            }));
        }

        // Statistics.
        try {
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }
    }
}
