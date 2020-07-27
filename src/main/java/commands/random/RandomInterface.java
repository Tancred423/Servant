// Author: Tancred423 (https://github.com/Tancred423)
package commands.random;

import net.dv8tion.jda.api.EmbedBuilder;
import servant.LoggingTask;
import servant.MyGuild;
import servant.MyUser;
import servant.Servant;
import utilities.JsonReader;
import utilities.MessageUtil;
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
            eb.setImage(json.get("imageUrl").toString());
            event.reply(eb.build());

            // Achievement
            var content = event.getMessage().getContentDisplay();
            var myUser = new MyUser(event.getAuthor());
            if (content.contains("anet") || content.contains("arenanet")) {
                if (!myUser.hasAchievement("arenanet")
                        && (event.getGuild() != null && new MyGuild(event.getGuild()).featureIsEnabled("achievements"))) {
                    myUser.setAchievement("arenanet");
                    new MessageUtil().reactAchievement(event.getMessage());
                }
            }
        } catch (IOException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, event.getJDA(), name, event));
        }
    }
}
