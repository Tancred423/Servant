package interaction;

import net.dv8tion.jda.core.Permission;

public class HighfiveCommand extends InteractionCommand {
    public HighfiveCommand() {
        this.name = "highfive";
        this.aliases = new String[]{"high5", "^5", "^five"};
        this.help = "mentions someone for a highfive";
        this.category = new Category("Interaction");
        this.arguments = "@user";
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.guildOnly = true;
        this.emoji = "\uD83D\uDE4F"; // 🙏.
    }
}
