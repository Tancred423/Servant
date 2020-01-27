// Author: Tancred423 (https://github.com/Tancred423)
package interaction;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import owner.blacklist.Blacklist;
import servant.Log;
import servant.Servant;
import utilities.JsonReader;
import utilities.Parser;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public abstract class InteractionCommand extends Command {
    String emoji;

    @Override
    protected void execute(CommandEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                if (!Toggle.isEnabled(event, name)) return;
                if (Blacklist.isBlacklisted(event.getAuthor(), event.getGuild())) return;

                var lang = LanguageHandler.getLanguage(event);
                var p = GuildHandler.getPrefix(event);
                if (event.getArgs().isEmpty()) {
                    var description = LanguageHandler.get(lang, "interaction_description");
                    var usage = name.equalsIgnoreCase("dab") || name.equalsIgnoreCase("flex") ?
                            String.format(LanguageHandler.get(lang, "interaction_usage_on"), name.substring(0, 1).toUpperCase(), name.substring(1).toLowerCase(), p, name, p, name) :
                            String.format(LanguageHandler.get(lang, "interaction_usage"), name.substring(0, 1).toUpperCase(), name.substring(1).toLowerCase(), p, name, p, name);
                    event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, null).getEmbed());
                    return;
                }

                // Check mentioned user.
                if (!Parser.hasMentionedUser(event.getMessage())) {
                    event.reply(LanguageHandler.get(lang, "invalid_mention"));
                    return;
                }

                // Get users.
                var mentioned = event.getMessage().getMentionedMembers().get(0).getUser();
                var guild = event.getGuild();
                var author = event.getAuthor();
                var internalAuthor = new User(author.getIdLong());
                var internalMentioned = new User(mentioned.getIdLong());

                // Get random gif.
                var gif = "";
                if (name.equals("wink") || name.equals("pat") || name.equals("hug")) {
                    try {
                        gif = JsonReader.readJsonFromUrl("https://some-random-api.ml/animu/" + name).get("link").toString();
                    } catch (IOException e) {
                        new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                        return;
                    }
                } else gif = InteractionDatabase.getGifUrl(name.toLowerCase(), guild, author);

                // Increment author and mentioned command count.
                internalAuthor.incrementInteractionCount(name, true, guild, author);
                internalMentioned.incrementInteractionCount(name, false, guild, author);

                var authorCount = internalAuthor.getInteractionCount(name, true, guild, author);
                var mentionedCount = internalMentioned.getInteractionCount(name, false, guild, author);
                var emote = utilities.Emote.getEmote(name, guild, author);
                var embed = new InteractionEmbed(name, emote, emoji, gif, author, mentioned, authorCount, mentionedCount, guild);

                event.reply(embed.getEmbed());

                // Statistics.
                new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
                new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Servant.threadPool);
    }
}
