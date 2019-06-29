package interaction;

import net.dv8tion.jda.core.Permission;

public class CookieCommand extends InteractionCommand {
    public CookieCommand() {
        this.name = "cookie";
        this.aliases = new String[]{"cookie", "biscuit"};
        this.help = "mentions someone to share a cookie";
        this.category = new Category("Interaction");
        this.arguments = "@user";
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.guildOnly = true;
        this.emoji = "\uD83C\uDF6A"; // 🍪.
    }
}
