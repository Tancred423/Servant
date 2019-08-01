// Author: Tancred423 (https://github.com/Tancred423)
package interaction;

import net.dv8tion.jda.core.Permission;

public class DabCommand extends InteractionCommand {
    public DabCommand() {
        this.name = "dab";
        this.aliases = new String[]{"yeet"};
        this.help = "Dab one someone. YEET";
        this.category = new Category("Interaction");
        this.arguments = "@user";
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = 5;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};

        this.emoji = "\uD83E\uDD26"; // 🤦.
    }
}
