// Author: Tancred423 (https://github.com/Tancred423)
package interaction;

import net.dv8tion.jda.core.Permission;

public class HugCommand extends InteractionCommand {
    public HugCommand() {
        this.name = "hug";
        this.aliases = new String[]{"cuddle"};
        this.help = "Hug someone.";
        this.category = new Category("Interaction");
        this.arguments = "@user";
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = 5;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};

        this.emoji = "\uD83E\uDD17"; // 🤗.
    }
}
