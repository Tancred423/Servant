// Author: Tancred423 (https://github.com/Tancred423)
package commands.interaction;

import files.language.LanguageHandler;
import servant.LoggingTask;
import servant.MyGuild;
import servant.MyUser;
import servant.Servant;
import utilities.JsonReader;
import utilities.MessageUtil;
import utilities.Parser;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.io.IOException;

public abstract class InteractionCommand extends Command {
    String emoji;
    String shared;
    String received;

    @Override
    protected void execute(CommandEvent event) {
        var lang = LanguageHandler.getLanguage(event);
        var p = new MyGuild(event.getGuild()).getPrefix();
        if (event.getArgs().isEmpty()) {
            var description = LanguageHandler.get(lang, "interaction_description_" + name);
            var usage = String.format(LanguageHandler.get(lang, "interaction_usage"), name.equalsIgnoreCase("shame") ? LanguageHandler.get(lang, "interaction_usage_shame") : description, p, name, p, name);
            event.reply(MessageUtil.createUsageEmbed(name, event.getAuthor(), lang, description, aliases, usage, null));
            return;
        }

        shared = getShared(lang);
        received = getReceived(lang);

        // Check mentioned user.
        if (!Parser.hasMentionedUser(event.getMessage())) {
            event.reply(LanguageHandler.get(lang, "invalid_mention"));
            return;
        }

        // Get users.
        var jda = event.getJDA();
        var user = event.getAuthor();
        var myUser = new MyUser(user);
        var mentionedUser = event.getMessage().getMentionedMembers().get(0).getUser();
        var mentionedMaster = new MyUser(mentionedUser);

        String gif = null;
        try {
            var json = JsonReader.readJsonFromUrl("https://api.servant.gg/" + this.name);
            for (int i = 0; i < 5; i++) {
                gif = json.get("imageUrl").toString();
                if (!gif.isEmpty()) break;
            }
        } catch (IOException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, event.getJDA(), name, event));
            gif = null; // Not needed, but just being explicit here
        }

        if (gif != null && gif.isEmpty()) gif = null;

        // Increment author and mentioned command count.
        myUser.incrementInteractionCount(name, true);
        mentionedMaster.incrementInteractionCount(name, false);

        var authorCount = myUser.getInteractionCount(name, true);
        var mentionedCount = mentionedMaster.getInteractionCount(name, false);
        var embed = new InteractionEmbed(jda, name, emoji, gif, user, mentionedUser, authorCount, mentionedCount, shared, received, lang);

        event.reply(embed.getEmbed());
    }

    private String getShared(String lang) {
        return LanguageHandler.get(lang, "interaction_shared_" + name);
    }

    private String getReceived(String lang) {
        return LanguageHandler.get(lang, "interaction_received_" + name);
    }
}