package interaction;

import net.dv8tion.jda.core.Permission;

public class CookieCommand extends InteractionCommand {
    public CookieCommand() {
        this.name = "cookie";
        this.aliases = new String[]{"cookie", "biscuit"};
        this.help = "mention someone to share a cookie";
        this.category = new Category("Interaction");
        this.arguments = "@user";
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = 5;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};

        this.emoji = "\uD83C\uDF6A"; // 🍪.
    }
}
