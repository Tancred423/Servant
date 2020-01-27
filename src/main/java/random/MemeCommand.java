// Author: Tancred423 (https://github.com/Tancred423)
package random;

import moderation.guild.Guild;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import org.json.JSONObject;
import servant.Log;
import servant.Servant;
import utilities.Constants;
import utilities.JsonReader;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class MemeCommand extends Command {
    public MemeCommand() {
        this.name = "meme";
        this.aliases = new String[0];
        this.help = "Random meme.";
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
        CompletableFuture.runAsync(() -> {
            try {
                if (!Toggle.isEnabled(event, name)) return;
                event.getChannel().sendTyping().queue();

                var guild = event.getGuild();
                var author = event.getAuthor();

                JSONObject json;
                try {
                    json = JsonReader.readJsonFromUrl("https://some-random-api.ml/meme");
                    var eb = new EmbedBuilder();
                    eb.setColor(new User(event.getAuthor().getIdLong()).getColor(guild, author));
                    eb.setTitle(json.get("caption").toString());
                    eb.setImage(json.get("image").toString());
                    event.reply(eb.build());
                } catch (IOException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                }

                // Statistics.
                new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
                if (event.getGuild() != null)
                    new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Servant.threadPool);
    }
}
