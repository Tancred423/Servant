// Author: Tancred423 (https://github.com/Tancred423)
package commands.random;

import net.dv8tion.jda.api.EmbedBuilder;
import servant.LoggingTask;
import servant.MyUser;
import servant.Servant;
import utilities.JsonReader;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.awt.*;
import java.io.IOException;

public abstract class RandomInterface extends Command {
    @Override
    protected void execute(CommandEvent event) {
        try {
            var json = JsonReader.readJsonFromUrl("https://api.servant.gg/" + this.name);
            var eb = new EmbedBuilder();
            eb.setColor(Color.decode(new MyUser(event.getAuthor()).getColorCode()));
            eb.setImage(json.get("image_url").toString());
            event.reply(eb.build());
        } catch (IOException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, event.getJDA(), name, event));
        }
    }
}
