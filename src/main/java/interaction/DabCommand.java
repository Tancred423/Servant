package interaction;

import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.doc.standard.Error;
import com.jagrosh.jdautilities.doc.standard.RequiredPermissions;
import com.jagrosh.jdautilities.examples.doc.Author;
import net.dv8tion.jda.core.Permission;

@CommandInfo(
        name = {"Dab"},
        description = "Dab on someone!",
        usage = "dab @user",
        requirements = {"The bot has all required permissions."}
)
@Error(
        value = "If arguments are provided, but they are not a mention.",
        response = "[Argument] is not a valid mention!"
)
@RequiredPermissions({Permission.MESSAGE_EMBED_LINKS})
@Author("Tancred")
public class DabCommand extends InteractionCommand {
    public DabCommand() {
        this.name = "dab";
        this.help = "mentions someone you wanna dab on. YEET";
        this.category = new Category("Interaction");
        this.arguments = "@user";
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.guildOnly = true;
        this.emoji = "\uD83E\uDD26"; // 🤦.
    }
}
