// Author: Tancred423 (https://github.com/Tancred423)
package commands.interaction;


import net.dv8tion.jda.api.Permission;
import utilities.Constants;

public class ShameCommand extends InteractionCommand {
    public ShameCommand() {
        this.name = "shame";
        this.aliases = new String[] { "cersei" };
        this.help = "Shame \uD83D\uDD14 Shame \uD83D\uDD14 Shame \uD83D\uDD14";
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

        this.emoji = "\uD83D\uDD14"; // 🔔
    }
}
