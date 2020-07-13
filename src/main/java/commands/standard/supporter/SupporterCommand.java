// Author: Tancred423 (https://github.com/Tancred423)
package commands.standard.supporter;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import servant.MyUser;
import utilities.Constants;
import utilities.ImageUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.awt.*;

public class SupporterCommand extends Command {
    public SupporterCommand() {
        this.name = "supporter";
        this.aliases = new String[] {"patreon", "patron", "donation", "donate", "serverboost", "boost" };
        this.help = "Support me ♥";
        this.category = new Category("Standard");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.modCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
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
        var myUser = new MyUser(user);

        event.reply(new EmbedBuilder()
                .setColor(Color.decode(myUser.getColorCode()))
                .setAuthor(LanguageHandler.get(lang, "supporter_supportservant"), null, ImageUtil.getUrl(jda, "patreon"))
                .setDescription(LanguageHandler.get(lang, "supporter_description"))
                .setThumbnail(ImageUtil.getUrl(jda, "kiss"))
                .addField("1. " + LanguageHandler.get(lang, "supporter_patreontitle"), LanguageHandler.get(lang, "supporter_subscription"), true)
                .addField("2. " + LanguageHandler.get(lang, "supporter_donationtitle"), LanguageHandler.get(lang, "supporter_donation"), true)
                .addField("3. " + LanguageHandler.get(lang, "supporter_serverboosttitle"), LanguageHandler.get(lang, "supporter_serverboost"), true)
                .setFooter(LanguageHandler.get(lang, "supporter_thanks"), event.getSelfUser().getAvatarUrl()).build()
        );
    }
}
