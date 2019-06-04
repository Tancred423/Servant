package interaction;

import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.doc.standard.Error;
import com.jagrosh.jdautilities.doc.standard.RequiredPermissions;
import com.jagrosh.jdautilities.examples.doc.Author;
import net.dv8tion.jda.core.Permission;

@CommandInfo(
        name = {"Pat"},
        description = "Pat someone!",
        usage = "pat @user",
        requirements = {"The bot has all required permissions."}
)
@Error(
        value = "If arguments are provided, but they are not a mention.",
        response = "[Argument] is not a valid mention!"
)
@RequiredPermissions({Permission.MESSAGE_EMBED_LINKS})
@Author("Tancred")
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
