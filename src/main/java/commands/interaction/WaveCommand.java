// Author: Tancred423 (https://github.com/Tancred423)
package commands.interaction;

import net.dv8tion.jda.api.Permission;
import utilities.Constants;

public class WaveCommand extends InteractionInterface {
    public WaveCommand() {
        this.name = "wave";
        this.aliases = new String[] { "greet" };
        this.help = "Wave to someone";
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

        this.emoji = "\uD83D\uDC4B"; // 👋
    }
}
