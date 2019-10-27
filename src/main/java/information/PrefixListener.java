// Author: Tancred423 (https://github.com/Tancred423)
package information;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.user.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import owner.blacklist.Blacklist;
import servant.Servant;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class PrefixListener extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent event) {
        CompletableFuture.runAsync(() -> {
            if (event.getAuthor().isBot()) return;

            if (event.getMessage().getContentRaw().equals("<@!" + event.getJDA().getSelfUser().getIdLong() + ">")
                    || event.getMessage().getContentRaw().equals("<@" + event.getJDA().getSelfUser().getIdLong() + ">")) {
                if (Blacklist.isBlacklisted(event.getAuthor(), event.getGuild())) return;
                var guild = event.getGuild();
                String prefix;
                String lang;

                try {
                    if (guild == null) {
                        var internalUser = new User(event.getAuthor().getIdLong());
                        prefix = internalUser.getPrefix();
                        lang = internalUser.getLanguage();
                    } else {
                        var internalGuild = new Guild(event.getGuild().getIdLong());
                        prefix = internalGuild.getPrefix();
                        lang = internalGuild.getLanguage();
                    }
                } catch (SQLException e) {
                    prefix = Servant.config.getDefaultPrefix();
                    lang = Servant.config.getDefaultLanguage();
                }

                event.getChannel().sendMessage(String.format(LanguageHandler.get(lang, "current_prefix"), prefix)).queue();
            }
        });
    }
}
