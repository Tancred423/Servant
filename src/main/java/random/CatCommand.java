// Author: Tancred423 (https://github.com/Tancred423)
package random;

import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import servant.LoggingTask;
import utilities.Constants;
import utilities.JsonReader;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.io.IOException;

public class CatCommand extends Command {
    public CatCommand() {
        this.name = "cat";
        this.aliases = new String[] { "catto" };
        this.help = "Random cat picture.";
        this.category = new Category("Random");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getChannel().sendTyping().queue();

        try {
            var json = JsonReader.readJsonFromUrl("https://some-random-api.ml/img/cat");
            var eb = new EmbedBuilder();
            eb.setColor(new Master(event.getAuthor()).getColor());
            eb.setImage(json.get("link").toString());
            event.reply(eb.build());
        } catch (IOException | IllegalArgumentException e) {
            new LoggingTask(e, event.getJDA(), name, event);
        }
    }
}
