// Author: Tancred423 (https://github.com/Tancred423)
package fun.random;

import moderation.guild.Guild;
import moderation.user.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import org.jsoup.Jsoup;
import servant.Log;
import utilities.Constants;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class RandomCommand extends Command {
    public RandomCommand() {
        this.name = "random";
        this.aliases = new String[0];
        this.help = "Random image of your choice.";
        this.category = new Category("Fun");
        this.arguments = "[keyword]";
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getChannel().sendTyping().queue();
        try {
            var guild = event.getGuild();
            var author = event.getAuthor();

            Image image;
            if (event.getArgs().isEmpty()) image = getRandomImage();
            else image = getRandomImage(event.getArgs());

            var eb = new EmbedBuilder();
            eb.setTitle(image.getTitle(), image.getLink());
            eb.setImage(image.getDirectLink());
            eb.setColor(new User(event.getAuthor().getIdLong()).getColor(guild, author));
            event.reply(eb.build());

            // Statistics.
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
            if (event.getGuild() != null)
                new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
        } catch (IOException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
        }
    }

    private Image getRandomImage(String keyword) throws IOException {
        var galleryLinks = new ArrayList<String>();

        // Get gallery links from imgur search
        while (galleryLinks.isEmpty()) {
            var urlObject = new URL("https://imgur.com/search?q=" + keyword);
            var doc = Jsoup.parse(urlObject, 10 * 60 * 1000);
            var content = doc.getElementById("content");
            var links = content.getElementsByTag("a");
            for (var link : links) {
                var linkHref = link.attr("href");
                if (linkHref.startsWith("/gallery/")) galleryLinks.add(linkHref);
            }
        }

        // Get random image (.jpg, .png or .gif from gallery links)
        var imageTitle = "";
        var imageLink = "";
        var imageDirectLink = "";

        while (!imageDirectLink.endsWith(".jpg") && !imageDirectLink.endsWith(".png") && !imageDirectLink.endsWith(".gif")) {
            var random = ThreadLocalRandom.current().nextInt(0, galleryLinks.size() - 1);
            var galleryLink = galleryLinks.get(random);
            imageLink = "https://imgur.com" + galleryLink;

            var urlObject = new URL(imageLink);
            var doc = Jsoup.parse(urlObject, 10 * 60 * 1000);

            // Get Image Title
            var title = doc.getElementsByAttribute("name");
            for (var tit : title) if (tit.attr("name").equals("twitter:title")) imageTitle = tit.attr("content");

            // Get Image Link
            var ele = doc.getElementsByAttribute("rel");
            for (var el : ele) if (el.attr("rel").equals("image_src")) imageDirectLink = el.attr("href");
        }

        return new Image(imageTitle, imageLink, imageDirectLink);
    }

    private Image getRandomImage() throws IOException {
        var imageTitle = "";
        var imageLink = "";
        var imageDirectLink = "";

        while (!imageDirectLink.endsWith(".jpg") && !imageDirectLink.endsWith(".png") && !imageDirectLink.endsWith(".gif")) {
            var urlObject = new URL("https://imgur.com/random");
            var doc = Jsoup.parse(urlObject, 10 * 60 * 1000);
            urlObject = new URL(doc.baseUri());
            imageLink = doc.baseUri();
            doc = Jsoup.parse(urlObject, 10 * 60 * 1000);

            // Get Image Title
            var title = doc.getElementsByAttribute("name");
            for (var tit : title) if (tit.attr("name").equals("twitter:title")) imageTitle = tit.attr("content");

            // Get Image Link
            var ele = doc.getElementsByAttribute("rel");
            for (var el : ele) if (el.attr("rel").equals("image_src")) imageDirectLink = el.attr("href");
        }

        return new Image(imageTitle, imageLink, imageDirectLink);
    }
}
