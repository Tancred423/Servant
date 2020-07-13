// Author: Tancred423 (https://github.com/Tancred423)
package commands.fun.coinFlip;

import files.language.LanguageHandler;
import servant.MyUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import utilities.Constants;
import utilities.ImageUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class CoinFlipCommand extends Command {
    public CoinFlipCommand() {
        this.name = "coinflip";
        this.aliases = new String[] {"cointoss"};
        this.help = "Flip a coin";
        this.category = new Category("Fun");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.modCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.botPermissions = new Permission[]{
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var jda = event.getJDA();
        var lang = LanguageHandler.getLanguage(event);
        var myUser = new MyUser(event.getAuthor());

        var coinflip = ThreadLocalRandom.current().nextInt(2); // 0 - 1

        if (coinflip == 0)
            event.reply(
                    new EmbedBuilder()
                            .setColor(Color.decode(myUser.getColorCode()))
                            .setTitle(LanguageHandler.get(lang, "coinflip_head"))
                            .setImage(ImageUtil.getUrl(jda, "cointoss_head"))
                            .build()
            );
        else
            event.reply(
                    new EmbedBuilder()
                            .setColor(Color.decode(myUser.getColorCode()))
                            .setTitle(LanguageHandler.get(lang, "coinflip_tail"))
                            .setImage(ImageUtil.getUrl(jda, "cointoss_tail"))
                            .build()
            );
    }
}
