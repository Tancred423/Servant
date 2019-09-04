// Author: Tancred423 (https://github.com/Tancred423)
package fun.interaction;

import net.dv8tion.jda.core.Permission;
import utilities.Constants;

public class HugCommand extends InteractionCommand {
    public HugCommand() {
        this.name = "hug";
        this.aliases = new String[0];
        this.help = "Hug someone.";
        this.category = new Category("*Interaction*");
        this.arguments = "@user";
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};

        this.emoji = "\uD83E\uDD17"; // 🤗.
    }
}
