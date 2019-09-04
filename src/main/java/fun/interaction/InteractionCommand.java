// Author: Tancred423 (https://github.com/Tancred423)
package fun.interaction;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.core.entities.Emote;
import moderation.guild.Guild;
import servant.Log;
import utilities.Parser;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;

public abstract class InteractionCommand extends Command {
    String emoji;

    @Override
    protected void execute(CommandEvent event) {
        if (!Toggle.isEnabled(event, name)) return;

        var lang = LanguageHandler.getLanguage(event, name);
        var p = GuildHandler.getPrefix(event, name);
        if (event.getArgs().isEmpty()) {
            try {
                var description = LanguageHandler.get(lang, "interaction_description");
                var usage = name.equalsIgnoreCase("dab") ?
                        String.format(LanguageHandler.get(lang, "interaction_usage_dab"), name.substring(0, 1).toUpperCase(), name.substring(1).toLowerCase(), p, name, p, name) :
                        String.format(LanguageHandler.get(lang, "interaction_usage"), name.substring(0, 1).toUpperCase(), name.substring(1).toLowerCase(), p, name, p, name);
                event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, null).getEmbed());
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            }
            return;
        }

        // Check mentioned user.
        if (!Parser.hasMentionedUser(event.getMessage())) {
            event.reply(LanguageHandler.get(lang, "invalid_mention"));
            return;
        }

        // Get users.
        var mentioned = event.getMessage().getMentionedMembers().get(0).getUser();
        var author = event.getAuthor();
        var internalAuthor = new User(author.getIdLong());
        var internalMentioned = new User(mentioned.getIdLong());

        // Get random gif.
        String gif;
        try {
            gif = InteractionDatabase.getGifUrl(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            return;
        }

        // Increment author and mentioned command count.
        try {
            internalAuthor.incrementInteractionCount(name, true);
            internalMentioned.incrementInteractionCount(name, false);
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            return;
        }

        // Get author and mentioned command count.
        int authorCount;
        int mentionedCount;
        try {
            authorCount = internalAuthor.getInteractionCount(name, true);
            mentionedCount = internalMentioned.getInteractionCount(name, false);
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            return;
        }

        // Get emote.
        Emote emote;
        try {
            emote = utilities.Emote.getEmote(name);
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            return;
        }

        InteractionEmbed embed;
        try {
            embed = new InteractionEmbed(name, emote, emoji, gif, author, mentioned, authorCount, mentionedCount);
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            return;
        }

        event.reply(embed.getEmbed());

        // Statistics.
        try {
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }
    }
}
