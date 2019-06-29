package interaction;

import net.dv8tion.jda.core.Permission;

public class PatCommand extends InteractionCommand {
    public PatCommand() {
        this.name = "pat";
        this.help = "mentions someone for a pat";
        this.category = new Category("Interaction");
        this.arguments = "@user";
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.guildOnly = true;
        this.emoji = "\uD83D\uDC4B"; // 👋.
    }
}
