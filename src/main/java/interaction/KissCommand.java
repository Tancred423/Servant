package interaction;

import net.dv8tion.jda.core.Permission;

public class KissCommand extends InteractionCommand {
    public KissCommand() {
        this.name = "kiss";
        this.aliases = new String[0];
        this.help = "mentions someone for a kiss";
        this.category = new Category("Interaction");
        this.arguments = "@user";
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = 5;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};

        this.emoji = "\uD83D\uDC8B"; // 💋.
    }
}
