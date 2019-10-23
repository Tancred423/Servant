package information;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.user.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import owner.blacklist.Blacklist;
import servant.Log;
import servant.Servant;

import java.sql.SQLException;

public class PrefixListener extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        try {
            if (Blacklist.isBlacklisted(event.getAuthor().getIdLong())) return;
            if (event.getGuild() != null) if (Blacklist.isBlacklisted(event.getGuild().getIdLong())) return;
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), "prefixlistener", null).sendLog(false);
        }

        if (event.getMessage().getContentRaw().equals("<@!" + event.getJDA().getSelfUser().getIdLong() + ">")
                || event.getMessage().getContentRaw().equals("<@" + event.getJDA().getSelfUser().getIdLong() + ">")) {
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
    }
}
