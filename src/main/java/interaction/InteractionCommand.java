package interaction;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.User;
import servant.Guild;
import servant.Log;
import servant.Servant;
import utilities.Parser;
import utilities.UsageEmbed;

import java.sql.SQLException;

public abstract class InteractionCommand extends Command {
    String emoji;

    @Override
    protected void execute(CommandEvent event) {
        // Enabled?
        try {
            if (!new servant.Guild(event.getGuild().getIdLong()).getToggleStatus("interaction")) return;
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }

        String prefix = Servant.config.getDefaultPrefix();
        if (event.getArgs().isEmpty()) {
            // Usage
            try {
                String usage = "**" + name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase() + (name.toLowerCase().equals("dab") ? " on" : "") +  " someone**\n" +
                        "Command: `" + prefix + name + " [@user]`\n" +
                        "Example: `" + prefix + name + " @Servant`";

                event.reply(new UsageEmbed(name, event.getAuthor(), ownerCommand, userPermissions, aliases, usage, null).getEmbed());
            } catch (SQLException e) {
                new Log(e, event, name).sendLogSqlCommandEvent(true);
            }
            return;
        }

        // Check mentioned user.
        if (!Parser.hasMentionedUser(event.getMessage())) {
            event.reply("Invalid mention.");
            return;
        }

        // Get users.
        User mentioned = event.getMessage().getMentionedMembers().get(0).getUser();
        User author = event.getAuthor();
        servant.User internalAuthor;
        servant.User internalMentioned;
        try {
            internalAuthor = new servant.User(author.getIdLong());
            internalMentioned = new servant.User(mentioned.getIdLong());
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(true);
            return;
        }

        // Get random gif.
        String gif;
        try {
            gif = InteractionDatabase.getGifUrl(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(true);
            return;
        }

        // Increment author and mentioned command count.
        try {
            internalAuthor.incrementInteractionCount(name, true);
            internalMentioned.incrementInteractionCount(name, false);
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(true);
            return;
        }

        // Get author and mentioned command count.
        int authorCount;
        int mentionedCount;
        try {
            authorCount = internalAuthor.getInteractionCount(name, true);
            mentionedCount = internalMentioned.getInteractionCount(name, false);
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(true);
            return;
        }

        // Get emote.
        Emote emote;
        try {
            emote = servant.Emote.getEmote(name);
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(true);
            return;
        }

        InteractionEmbed embed;
        try {
            embed = new InteractionEmbed(name, emote, emoji, gif, author, mentioned, authorCount, mentionedCount);
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(true);
            return;
        }

        event.reply(embed.getEmbed());

        // Statistics.
        try {
            new servant.User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }
    }
}
