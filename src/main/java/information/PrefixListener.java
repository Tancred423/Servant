// Author: Tancred423 (https://github.com/Tancred423)
package information;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.user.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import owner.blacklist.Blacklist;
import servant.Servant;

import java.util.concurrent.CompletableFuture;

public class PrefixListener extends ListenerAdapter {
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.isFromGuild() && event.getGuild().getIdLong() == 264445053596991498L) return; // Discord Bot List
        CompletableFuture.runAsync(() -> {
            try {
                if (event.getAuthor().isBot()) return;

                if (event.getMessage().getContentRaw().equals("<@!" + event.getJDA().getSelfUser().getIdLong() + ">")
                        || event.getMessage().getContentRaw().equals("<@" + event.getJDA().getSelfUser().getIdLong() + ">")) {
                    if (Blacklist.isBlacklisted(event.getAuthor(), event.isFromGuild() ? event.getGuild() : null)) return;
                    var author = event.getAuthor();
                    String prefix;
                    String lang;

                    if (event.isFromGuild()) {
                        var internalGuild = new Guild(event.getGuild().getIdLong());
                        var guild = event.getGuild();
                        prefix = internalGuild.getPrefix(guild, author);
                        lang = internalGuild.getLanguage(guild, author);
                    } else {
                        var internalAuthor = new User(author.getIdLong());
                        prefix = internalAuthor.getPrefix(null, author);
                        lang = internalAuthor.getLanguage(null, author);
                    }

                    event.getChannel().sendMessage(String.format(LanguageHandler.get(lang, "current_prefix"), prefix)).queue();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Servant.cpuPool);
    }
}
