// Author: Tancred423 (https://github.com/Tancred423)
package commands.random;

import net.dv8tion.jda.api.Permission;
import utilities.Constants;

public class FoxCommand extends RandomInterface {
    public FoxCommand() {
        this.name = "fox";
        this.aliases = new String[0];
        this.help = "Random fox picture";
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
