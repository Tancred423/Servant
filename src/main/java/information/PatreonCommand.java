// Author: Tancred423 (https://github.com/Tancred423)
package information;

import files.language.LanguageHandler;
import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import utilities.Constants;
import utilities.ImageUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;
import zJdaUtilsLib.com.jagrosh.jdautilities.examples.doc.Author;

@Author("John Grosh (jagrosh)")
public class PatreonCommand extends Command {
    public PatreonCommand() {
        this.name = "patreon";
        this.aliases = new String[] { "donation", "serverboost", "boost" };
        this.help = "Support me <3";
        this.category = new Category("Information");
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
        var lang = LanguageHandler.getLanguage(event);
        var jda = event.getJDA();
        var user = event.getAuthor();
        var master = new Master(user);

        event.reply(new EmbedBuilder()
                .setColor(master.getColor())
                .setAuthor(LanguageHandler.get(lang, "patreon_supportserver"), null, "https://i.imgur.com/rCnhGKA.jpg") // Patreon Icon
                .setDescription(LanguageHandler.get(lang, "patreon_description"))
                .setThumbnail(ImageUtil.getImageUrl(jda, "love"))
                .addField("1. " + LanguageHandler.get(lang, "patreon_patreontitle"), LanguageHandler.get(lang, "patreon_subscription"), false)
                .addField("$1+/month", LanguageHandler.get(lang, "patreon_$1"), true)
                .addField("$3+/month", LanguageHandler.get(lang, "patreon_$3"), true)
                .addField("$5+/month", LanguageHandler.get(lang, "patreon_$5"), true)
                .addField("$10+/month", LanguageHandler.get(lang, "patreon_$10"), true)
                .addField("2. " + LanguageHandler.get(lang, "patreon_donationtitle"), LanguageHandler.get(lang, "patreon_donation"), false)
                .addField("$5+ Donated In Lifetime", LanguageHandler.get(lang, "patreon_donation_$5"), true)
                .addField("3. " + LanguageHandler.get(lang, "patreon_serverboosttitle"), LanguageHandler.get(lang, "patreon_serverboost"), false)
                .setFooter(LanguageHandler.get(lang, "patreon_thanks"), event.getSelfUser().getAvatarUrl()).build()
        );
    }
}
