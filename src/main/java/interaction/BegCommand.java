package interaction;


import net.dv8tion.jda.core.Permission;

public class BegCommand extends InteractionCommand {
    public BegCommand() {
        this.name = "beg";
        this.help = "mentions someone to beg";
        this.category = new Category("Interaction");
        this.arguments = "@user";
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.guildOnly = true;
        this.emoji = "\uD83D\uDE4C"; // 🙌.
    }
}
