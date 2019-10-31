// Author: Tancred423 (https://github.com/Tancred423)
package information;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.user.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import owner.blacklist.Blacklist;

import java.util.concurrent.CompletableFuture;

public class PrefixListener extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent event) {
        CompletableFuture.runAsync(() -> {
            if (event.getAuthor().isBot()) return;

            if (event.getMessage().getContentRaw().equals("<@!" + event.getJDA().getSelfUser().getIdLong() + ">")
                    || event.getMessage().getContentRaw().equals("<@" + event.getJDA().getSelfUser().getIdLong() + ">")) {
                if (Blacklist.isBlacklisted(event.getAuthor(), event.getGuild())) return;
                var guild = event.getGuild();
                var author = event.getAuthor();
                String prefix;
                String lang;

                if (guild == null) {
                    var internalUser = new User(event.getAuthor().getIdLong());
                    prefix = internalUser.getPrefix(null, author);
                    lang = internalUser.getLanguage(null, author);
                } else {
                    var internalGuild = new Guild(event.getGuild().getIdLong());
                    prefix = internalGuild.getPrefix(guild, author);
                    lang = internalGuild.getLanguage(guild, author);
                }

                event.getChannel().sendMessage(String.format(LanguageHandler.get(lang, "current_prefix"), prefix)).queue();
            }
        });
    }
}
