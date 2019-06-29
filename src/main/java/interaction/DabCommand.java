package interaction;

import net.dv8tion.jda.core.Permission;

public class DabCommand extends InteractionCommand {
    public DabCommand() {
        this.name = "dab";
        this.help = "mentions someone you wanna dab on. YEET";
        this.category = new Category("Interaction");
        this.arguments = "@user";
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.guildOnly = true;
        this.emoji = "\uD83E\uDD26"; // 🤦.
    }
}
