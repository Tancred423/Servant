// Author: Tancred423 (https://github.com/Tancred423)
package commands.random;

import net.dv8tion.jda.api.Permission;
import utilities.Constants;

public class FennecCommand extends RandomInterface {
    public FennecCommand() {
        this.name = "fennec";
        this.aliases = new String[0];
        this.help = "Random fennec picture";
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
                Permission.MESSAGE_EMBED_LINKS
        };
    }
}
