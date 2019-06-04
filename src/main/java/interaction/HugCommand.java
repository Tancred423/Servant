package interaction;

import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.doc.standard.Error;
import com.jagrosh.jdautilities.doc.standard.RequiredPermissions;
import com.jagrosh.jdautilities.examples.doc.Author;
import net.dv8tion.jda.core.Permission;

@CommandInfo(
        name = {"Hug", "Cuddle"},
        description = "Hug someone!",
        usage = "hug @user",
        requirements = {"The bot has all required permissions."}
)
@Error(
        value = "If arguments are provided, but they are not a mention.",
        response = "[Argument] is not a valid mention!"
)
@RequiredPermissions({Permission.MESSAGE_EMBED_LINKS})
@Author("Tancred")
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
