package interaction;

import net.dv8tion.jda.core.Permission;

public class HugCommand extends InteractionCommand {
    public HugCommand() {
        this.name = "hug";
        this.aliases = new String[]{"cuddle"};
        this.help = "mentions someone for a hug";
        this.category = new Category("Interaction");
        this.arguments = "@user";
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.guildOnly = true;
        this.emoji = "\uD83E\uDD17"; // 🤗.
    }
}
