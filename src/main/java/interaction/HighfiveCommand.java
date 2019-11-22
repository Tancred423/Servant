// Author: Tancred423 (https://github.com/Tancred423)
package interaction;

import net.dv8tion.jda.api.Permission;
import utilities.Constants;

public class HighfiveCommand extends InteractionCommand {
    public HighfiveCommand() {
        this.name = "highfive";
        this.aliases = new String[0];
        this.help = "Give someone a high five.";
        this.category = new Category("Interaction");
        this.arguments = "@user";
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS
        };

        this.emoji = "\uD83D\uDE4F"; // 🙏.
    }
}
