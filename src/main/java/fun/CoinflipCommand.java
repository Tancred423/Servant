// Author: Tancred423 (https://github.com/Tancred423)
package fun;

import files.language.LanguageHandler;
import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import utilities.Constants;
import utilities.ImageUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.util.concurrent.ThreadLocalRandom;

public class CoinflipCommand extends Command {
    public CoinflipCommand() {
        this.name = "coinflip";
        this.aliases = new String[]{"cointoss"};
        this.help = "Returns head or tail.";
        this.category = new Category("Fun");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[]{
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var jda = event.getJDA();
        var lang = LanguageHandler.getLanguage(event);
        var master = new Master(event.getAuthor());

        var coinflip = ThreadLocalRandom.current().nextInt(2); // 0 - 1

        if (coinflip == 0)
            event.reply(
                    new EmbedBuilder()
                            .setColor(master.getColor())
                            .setTitle(LanguageHandler.get(lang, "coinflip_head"))
                            .setImage(ImageUtil.getImageUrl(jda, "cointoss_head"))
                            .build()
            );
        else
            event.reply(
                    new EmbedBuilder()
                            .setColor(master.getColor())
                            .setTitle(LanguageHandler.get(lang, "coinflip_tail"))
                            .setImage(ImageUtil.getImageUrl(jda, "cointoss_tail"))
                            .build()
            );
    }
}
