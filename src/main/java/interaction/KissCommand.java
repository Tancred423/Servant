package interaction;

import net.dv8tion.jda.core.Permission;

public class KissCommand extends InteractionCommand {
    public KissCommand() {
        this.name = "kiss";
        this.help = "mentions someone for a kiss";
        this.category = new Category("Interaction");
        this.arguments = "@user";
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.guildOnly = true;
        this.emoji = "\uD83D\uDC8B"; // 💋.
    }
}
