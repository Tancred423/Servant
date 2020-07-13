// Author: Tancred423 (https://github.com/Tancred423)
package commands.random.randomImgur;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.Permission;
import org.jsoup.Jsoup;
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
        this.aliases = new String[] { "imgur" };
        this.help = "Random image of your choice";
        this.category = new Category("Random");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.modCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected void execute(CommandEvent event) {
        var lang = LanguageHandler.getLanguage(event);
        event.replyWarning(LanguageHandler.get(lang, "tmp_disabled"));
//        try {
//            event.getChannel().sendTyping().queue();
//            var lang = LanguageHandler.getLanguage(event);
//
//            Image image;
//            if (event.getArgs().isEmpty()) image = getRandomImage();
//            else image = getRandomImage(URLEncoder.encode(event.getArgs(), StandardCharsets.UTF_8));
//
//            if (image == null) {
//                event.replyWarning(LanguageHandler.get(lang, "random_empty"));
//                return;
//            }
//
//            var eb = new EmbedBuilder();
//            eb.setTitle(image.getTitle(), image.getLink());
//            eb.setImage(image.getDirectLink());
//            eb.setColor(Color.decode(new MyUser(event.getAuthor()).getColorCode()));
//            event.reply(eb.build());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private Image getRandomImage(String keyword) throws IOException {
        var galleryLinks = new ArrayList<String>();

        // Get gallery links from imgur search
        var urlObject = new URL("https://imgur.com/search?q=title:" + keyword);
        var doc = Jsoup.parse(urlObject, 10 * 60 * 1000);
        var content = doc.getElementById("content");
        var links = content.getElementsByTag("a");
        for (var link : links) {
            var linkHref = link.attr("href");
            if (linkHref.startsWith("/gallery/")) galleryLinks.add(linkHref);
        }

        if (galleryLinks.isEmpty()) return null;

        // Get commands.random image (.jpg, .png or .gif from gallery links)
        var imageTitle = "";
        var imageLink = "";
        var imageDirectLink = "";

        var counter = 0;
        while (!imageDirectLink.endsWith(".jpg") && !imageDirectLink.endsWith(".png") && !imageDirectLink.endsWith(".gif") && counter < 5) {
            int random;
            if (galleryLinks.size() == 1) random = 0;
            else random = ThreadLocalRandom.current().nextInt(0, galleryLinks.size() - 1);
            var galleryLink = galleryLinks.get(random);
            imageLink = "https://imgur.com" + galleryLink;

            urlObject = new URL(imageLink);
            doc = Jsoup.parse(urlObject, 10 * 60 * 1000);

            // Get Image Title
            var title = doc.getElementsByAttribute("name");
            for (var tit : title) if (tit.attr("name").equals("twitter:title")) imageTitle = tit.attr("content");

            // Get Image Link
            var ele = doc.getElementsByAttribute("rel");
            for (var el : ele) if (el.attr("rel").equals("image_src")) imageDirectLink = el.attr("href");
            counter++;
        }

        if (imageTitle.isEmpty() || imageLink.isEmpty() || imageDirectLink.isEmpty()) return null;
        else return new Image(imageTitle, imageLink, imageDirectLink);
    }

    private Image getRandomImage() throws IOException {
        var imageTitle = "";
        var imageLink = "";
        var imageDirectLink = "";

        var counter = 0;
        while (!imageDirectLink.endsWith(".jpg") && !imageDirectLink.endsWith(".png") && !imageDirectLink.endsWith(".gif")) {
            if (counter >= 3) break;
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
            counter++;
        }

        if (imageTitle.isEmpty() || imageLink.isEmpty() || imageDirectLink.isEmpty()) return null;
        else return new Image(imageTitle, imageLink, imageDirectLink);
    }
}
