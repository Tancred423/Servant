// Author: Tancred423 (https://github.com/Tancred423)
package commands.interaction;

import net.dv8tion.jda.api.Permission;
import utilities.Constants;

public class WinkCommand extends InteractionInterface {
    public WinkCommand() {
        this.name = "wink";
        this.aliases = new String[0];
        this.help = "Wink to someone";
        this.category = new Category("Interaction");
        this.arguments = "@user";
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.modCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS
        };

        this.emoji = "\uD83D\uDE09"; // 😉
    }
}
