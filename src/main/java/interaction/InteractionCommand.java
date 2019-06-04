package interaction;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.User;
import servant.Log;

import java.sql.SQLException;

public abstract class InteractionCommand extends Command {
    String emoji;

    @Override
    protected void execute(CommandEvent event) {
        // Get mentioned user.
        if (event.getArgs().isEmpty()) {
            event.reply("You have to mention a user.");
            return;
        }
        User mentioned;
        try {
            mentioned = event.getMessage().getMentionedUsers().get(0);
        } catch (IndexOutOfBoundsException e) {
            event.reply("Invalid mention.");
            return;
        }

        // Get users.
        User author = event.getAuthor();
        servant.User internalAuthor;
        servant.User internalMentioned;
        try {
            internalAuthor = new servant.User(author.getIdLong());
            internalMentioned = new servant.User(mentioned.getIdLong());
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSQL();
            return;
        }

        // Get random gif.
        String gif;
        try {
            gif = InteractionDatabase.getGifUrl(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSQL();
            return;
        }

        // Increment author and mentioned command count.
        try {
            internalAuthor.incrementInteractionCount(name, true);
            internalMentioned.incrementInteractionCount(name, false);
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSQL();
            return;
        }

        // Get author and mentioned command count.
        int authorCount;
        int mentionedCount;
        try {
            authorCount = internalAuthor.getInteractionCount(name, true);
            mentionedCount = internalMentioned.getInteractionCount(name, false);
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSQL();
            return;
        }

        // Get emote.
        Emote emote;
        try {
            emote = servant.Emote.getEmote(name);
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSQL();
            return;
        }

        InteractionEmbed embed;
        try {
            embed = new InteractionEmbed(name, emote, emoji, gif, author, mentioned, authorCount, mentionedCount);
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSQL();
            return;
        }

        event.reply(embed.getEmbed());
    }
}
