// Author: Tancred423 (https://github.com/Tancred423)
package random;

import files.language.LanguageHandler;
import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import servant.LoggingTask;
import servant.Servant;
import utilities.Constants;
import utilities.JsonReader;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.io.IOException;

public class SlothCommand extends Command {
    public SlothCommand() {
        this.name = "sloth";
        this.aliases = new String[] { "hiriko", "hirik0" };
        this.help = "Random sloth picture.";
        this.category = new Category("Random");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getChannel().sendTyping().queue();

        try {
            var lang = LanguageHandler.getLanguage(event);
            var json = JsonReader.readJsonFromUrl("https://api.unsplash.com/photos/random?query=sloth&client_id=" + Servant.config.getUnsplashClientId());
            var eb = new EmbedBuilder()
                    .setColor(new Master(event.getAuthor()).getColor());

            /*
             * Credits
             */
            // Image Link
            var imageLink = json.getJSONObject("links").getString("html");

            // User (Name + Profile Link)
            var user = json.getJSONObject("user");
            var userName = user.getString("name");
            var userProfile = user.getJSONObject("links").getString("html");

            // User Instagram
            var userInstagramUsername = user.get("instagram_username").toString();
            var userInstagramProfileUrl = userInstagramUsername.equals("null") ? null : "https://instagram.com/" + userInstagramUsername;

            // User Portfolio
            var portfolioUrl = user.get("portfolio_url").toString();

            // User Twitter
            var userTwitterUsername = user.get("twitter_username").toString();
            var userTwitterProfileUrl = userTwitterUsername.equals("null") ? null : "https://twitter.com/" + userTwitterUsername;

            // Building the credits
            eb.setDescription("[" + LanguageHandler.get(lang, "random_photo") + "](" + imageLink + ") " + LanguageHandler.get(lang, "random_by") + " [" + userName + "](" + userProfile + ")" +
                    " (" +
                    (portfolioUrl.equals("null") ? "" : " [Portfolio](" + portfolioUrl + ")") +
                    (userInstagramProfileUrl == null ? "" : " [Instagram](" + userInstagramProfileUrl + ")") +
                    (userTwitterProfileUrl == null ? "" : " [Twitter](" + userTwitterProfileUrl + ")") +
                    " )");

            /*
             * The actual use of the image
             */
            // Image Direct Link
            var directImageLink = json.getJSONObject("urls").getString("full");
            eb.setImage(directImageLink);
            event.reply(eb.build());
        } catch (IOException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, event.getJDA(), name, event));
        }
    }
}
