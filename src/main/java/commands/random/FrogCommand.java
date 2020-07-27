// Author: Tancred423 (https://github.com/Tancred423)
package commands.random;

import net.dv8tion.jda.api.Permission;
import utilities.Constants;

public class FrogCommand extends RandomInterface {
    public FrogCommand() {
        this.name = "frog";
        this.aliases = new String[] {"froggo", "arenanet", "anet"};
        this.help = "Random frog picture";
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
