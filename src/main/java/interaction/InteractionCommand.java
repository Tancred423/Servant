// Author: Tancred423 (https://github.com/Tancred423)
package interaction;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.user.Master;
import utilities.EmoteUtil;
import utilities.JsonReader;
import utilities.MessageUtil;
import utilities.Parser;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.io.IOException;

public abstract class InteractionCommand extends Command {
    String emoji;

    @Override
    protected void execute(CommandEvent event) {
        var lang = LanguageHandler.getLanguage(event);
        var p = GuildHandler.getPrefix(event);
        if (event.getArgs().isEmpty()) {
            var description = LanguageHandler.get(lang, "interaction_description");
            var usage = name.equalsIgnoreCase("dab") || name.equalsIgnoreCase("flex") ?
                    String.format(LanguageHandler.get(lang, "interaction_usage_on"), name.substring(0, 1).toUpperCase(), name.substring(1).toLowerCase(), p, name, p, name) :
                    String.format(LanguageHandler.get(lang, "interaction_usage"), name.substring(0, 1).toUpperCase(), name.substring(1).toLowerCase(), p, name, p, name);
            event.reply(MessageUtil.createUsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, null));
            return;
        }

        // Check mentioned user.
        if (!Parser.hasMentionedUser(event.getMessage())) {
            event.reply(LanguageHandler.get(lang, "invalid_mention"));
            return;
        }

        // Get users.
        var jda = event.getJDA();
        var guild = event.getGuild();
        var user = event.getAuthor();
        var master = new Master(user);
        var mentionedUser = event.getMessage().getMentionedMembers().get(0).getUser();
        var mentionedMaster = new Master(mentionedUser);

        // Get random gif.
        var gif = "";
        if (name.equals("wink") || name.equals("pat") || name.equals("hug")) {
            try {
                gif = JsonReader.readJsonFromUrl("https://some-random-api.ml/animu/" + name).get("link").toString();
            } catch (IOException e) {
                gif = Interaction.getGifUrl(jda, name.toLowerCase());
            }
        } else gif = Interaction.getGifUrl(jda, name.toLowerCase());

        // Increment author and mentioned command count.
        master.incrementInteractionCount(name, true);
        mentionedMaster.incrementInteractionCount(name, false);

        var authorCount = master.getInteractionCount(name, true);
        var mentionedCount = mentionedMaster.getInteractionCount(name, false);
        var emote = EmoteUtil.getEmote(jda, name);
        var embed = new InteractionEmbed(name, emote, emoji, gif, user, mentionedUser, authorCount, mentionedCount);

        event.reply(embed.getEmbed());
    }
}
